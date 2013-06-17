package pp.corleone.service.iautos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.corleone.domain.iautos.FetcherConstants;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.StatusRequestWrapper;

public class ExtractThread extends Thread {

	protected final Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void run() {

		while (!isInterrupted()) {
			Callback cb = IautosResource.extractQueue.poll();

			if (null == cb) {
				try {
					TimeUnit.MILLISECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {

					Future<Map<String, Collection<?>>> f = IautosResource.threadPool
							.submit(cb);

					if (null == f) {
						getLogger().info("non callback ...");
						continue;
					}

					Map<String, Collection<?>> result = null;
					try {
						result = f.get(10, TimeUnit.SECONDS);
					} catch (ExecutionException e) {
						e.printStackTrace();
						getLogger().error(
								"extract error :"
										+ cb.getResponseWrapper().getUrl());
						continue;
					} catch (TimeoutException e) {
						e.printStackTrace();
						getLogger().error(
								"extract error :"
										+ cb.getResponseWrapper().getUrl());
						continue;
					}

					if (null == result) {
						continue;
					}

					Map<String, Integer> offered = new HashMap<String, Integer>();

					if (result.containsKey(FetcherConstants.Fetcher)) {

						Collection<?> fs = result.get(FetcherConstants.Fetcher);

						for (Object o : fs) {
							Fetcher fetcher = (Fetcher) o;
							String fetcherKey = fetcher.getClass().getName();
							boolean offeredFlag = false;
							do {
								offeredFlag = IautosResource.fetchQueue.offer(
										fetcher, 500, TimeUnit.MILLISECONDS);
								getLogger().debug(
										"offer "
												+ fetcher.getRequestWrapper()
														.getUrl());
							} while (!offeredFlag);
							if (offered.containsKey(fetcherKey)) {
								offered.put(fetcherKey,
										offered.get(fetcherKey) + 1);
							} else {
								offered.put(fetcherKey, 1);
							}
						}
					}
					if (result.containsKey(FetcherConstants.STATUS)) {

						Collection<?> ss = result.get(FetcherConstants.STATUS);

						for (Object o : ss) {
							StatusRequestWrapper srw = (StatusRequestWrapper) o;
							boolean offeredFlag = false;
							do {
								offeredFlag = IautosResource.statusQueue.offer(
										srw, 500, TimeUnit.MILLISECONDS);
								getLogger()
										.debug("offer "
												+ srw.getFetcher()
														.getRequestWrapper()
														.getUrl()
												+ " delay "
												+ srw.getDelay(TimeUnit.SECONDS)
												+ "seconds");
							} while (!offeredFlag);

						}
					}
					for (Map.Entry<String, Integer> offer : offered.entrySet()) {
						getLogger().info(
								"add " + offer.getValue() + " fetcher task :"
										+ offer.getKey());
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}
	}
}
