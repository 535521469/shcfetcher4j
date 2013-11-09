package pp.corleone.service.iautos;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pp.corleone.ConfigManager;
import pp.corleone.Log;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.ResponseWrapper;

public class FetcherThread extends Thread {

	@Override
	public void run() {

		ThreadPoolExecutor pe = IautosResource.fetcherPool;

		Fetcher fetcher = null;

		int fetcher_idle_sleep = ConfigManager.getInstance().getConfigItem(
				IautosConstant.FETCHER_IDLE_SLEEP, 30);

		while (!isInterrupted()) {
			try {

				fetcher = IautosResource.fetchQueue.poll(fetcher_idle_sleep,
						TimeUnit.SECONDS);
				if (null == fetcher) {
					Log.info("non fetchable ...");
					continue;
				}

				if (fetcher.getRequestWrapper().getReferRequestWrappers()
						.size() > 0) {

					Log.debug(
							"fetch priority "
									+ fetcher.getRequestWrapper().getPriority()
									+ " "
									+ fetcher.getRequestWrapper().getUrl()
									+ " refer to "
									+ fetcher.getRequestWrapper()
											.getLastRequestUrl());
				} else {
					Log.debug(
							"fetch priority "
									+ fetcher.getRequestWrapper().getPriority()
									+ " "
									+ fetcher.getRequestWrapper().getUrl());
				}

				Future<ResponseWrapper> fu = pe.submit(fetcher);

				try {
					ResponseWrapper responseWrapper = fu.get();
					if (null != responseWrapper) {
						Callback cb = fetcher.getRequestWrapper().getCallback();
						cb.setResponseWrapper(responseWrapper);
						boolean offeredFlag = false;

						do {
//							offeredFlag = IautosResource.extractQueue.offer(cb,
//									500, TimeUnit.MILLISECONDS);
							
							IautosResource.extractQueue.put(cb);
							offeredFlag = true;

							Log.debug(
									"offer callback "
											+ cb.getClass()
											+ " refer to "
											+ cb.getResponseWrapper()
													.getReferRequestWrapper()
													.getUrl());

						} while (!offeredFlag);
					} else {
						Log.debug(
								"ignore fetch :"
										+ fetcher.getRequestWrapper().getUrl());
					}
				} catch (ExecutionException e) {
					e.printStackTrace();
					continue;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
