package pp.corleone.service.iautos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.corleone.domain.iautos.FetcherConstants;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;

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
						result = f.get();
					} catch (ExecutionException e) {
						e.printStackTrace();
						continue;
					}

					if (null != result
							&& result.containsKey(FetcherConstants.Fetcher)) {

						Collection<?> fs = result.get(FetcherConstants.Fetcher);
						Map<String, Integer> offered = new HashMap<String, Integer>();
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

						for (Map.Entry<String, Integer> offer : offered
								.entrySet()) {
							getLogger().info(
									"add " + offer.getValue()
											+ " fetcher task :"
											+ offer.getKey());
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}
	}
}
