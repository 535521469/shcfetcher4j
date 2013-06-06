package pp.corleone.service.iautos;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.ResponseWrapper;

public class FetcherThread extends Thread {

	protected final Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void run() {

		ThreadPoolExecutor pe = IautosResource.threadPool;

		Fetcher fetcher = null;

		while (!isInterrupted()) {
			try {
				fetcher = IautosResource.fetchQueue.poll(3000,
						TimeUnit.MILLISECONDS);
				if (null == fetcher) {
					getLogger().info("non fetchable ...");
					continue;
				}

				if (fetcher.getRequestWrapper().getReferRequestWrappers()
						.size() > 0) {

					getLogger().debug(
							"fetch " + fetcher.getRequestWrapper().getUrl());
				} else {
					getLogger().debug(
							"fetch " + fetcher.getRequestWrapper().getUrl());
				}

				Future<ResponseWrapper> fu = pe.submit(fetcher);
				try {
					ResponseWrapper responseWrapper = fu.get();
					if (null != responseWrapper) {
						Callback cb = fetcher.getRequestWrapper().getCallback();
						cb.setResponseWrapper(responseWrapper);
						boolean offeredFlag = false;

						do {
							offeredFlag = IautosResource.extractQueue.offer(cb,
									500, TimeUnit.MILLISECONDS);

							getLogger().debug(
									"offer callback "
											+ cb.getClass()
											+ " refer to "
											+ cb.getResponseWrapper()
													.getReferRequestWrapper()
													.getUrl());

						} while (!offeredFlag);
					} else {
						getLogger().info(
								"ignore fetch :"
										+ fetcher.getRequestWrapper().getUrl());
					}
				} catch (ExecutionException e) {
					e.printStackTrace();
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// System.out.println("............");

	}
}
