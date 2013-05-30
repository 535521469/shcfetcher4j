package pp.corleone.service.iautos;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.ResponseWrapper;

public class FetcherThread extends Thread {

	@Override
	public void run() {

		ThreadPoolExecutor pe = IautosResource.threadPool;

		Fetcher fetcher = null;

		do {
			try {
				fetcher = IautosResource.fetchQueue.poll(500,
						TimeUnit.MILLISECONDS);
				if (null == fetcher) {
					TimeUnit.MILLISECONDS.sleep(500);
					continue;
				}
				Future<ResponseWrapper> fu = pe.submit(fetcher);
				try {
					ResponseWrapper responseWrapper = fu.get();

					Callback cb = fetcher.getRequestWrapper().getCallback();
					responseWrapper.setReferRequestWrapper(fetcher
							.getRequestWrapper());

					boolean offeredFlag = false;

					do {
						IautosResource.extractQueue.offer(cb, 500,
								TimeUnit.MILLISECONDS);
					} while (offeredFlag);

				} catch (ExecutionException e) {
					e.printStackTrace();
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (null == fetcher || IautosResource.fetchQueue.size() > 0);

	}

}
