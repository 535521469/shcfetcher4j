package pp.corleone.service.iautos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pp.corleone.domain.iautos.FetcherConstants;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;

public class ExtractThread extends Thread {

	@Override
	public void run() {

		ThreadPoolExecutor pe = IautosResource.threadPool;

		while (true) {
			Callback cb = IautosResource.extractQueue.poll();

			if (null == cb) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
					Future<Map<String, Collection<?>>> f = pe.submit(cb);

					if (null == f) {
						continue;
					}

					Map<String, Collection<?>> result = null;
					try {
						result = f.get();
					} catch (ExecutionException e) {
						e.printStackTrace();
						continue;
					}

					if (result.containsKey(FetcherConstants.Fetcher)) {

						Collection<?> fs = result.get(FetcherConstants.Fetcher);
						Map<String, Integer> offered = new HashMap<String, Integer>();
						for (Object o : fs) {
							Fetcher fetcher = (Fetcher) o;
							String fetcherKey = fetcher.getClass().getName();

							boolean offeredFlag = false;

							do {
								offeredFlag = IautosResource.fetchQueue.offer(
										fetcher, 500, TimeUnit.MILLISECONDS);
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
							System.out.println(offer.getKey() + ":"
									+ offer.getValue());
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}
	}

}
