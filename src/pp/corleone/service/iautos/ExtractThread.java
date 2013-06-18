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

	private <T extends Fetcher> void doFetcher(Collection<T> fs,
			Map<String, Integer> ignored, Map<String, Integer> offered)
			throws InterruptedException {

		for (Fetcher fetcher : fs) {

			String fetcherKey = fetcher.getClass().getName();

			if (fetcher.isIgnore()) {
				this.getLogger().debug(
						"ignore fetch " + fetcher.getRequestWrapper().getUrl());
				if (ignored.containsKey(fetcherKey)) {
					ignored.put(fetcherKey, ignored.get(fetcherKey) + 1);
				} else {
					ignored.put(fetcherKey, 1);
				}
				continue;
			}

			boolean offeredFlag = false;
			do {
				offeredFlag = IautosResource.fetchQueue.offer(fetcher, 500,
						TimeUnit.MILLISECONDS);
				getLogger().debug(
						"offer " + fetcher.getRequestWrapper().getUrl());
			} while (!offeredFlag);
			if (offered.containsKey(fetcherKey)) {
				offered.put(fetcherKey, offered.get(fetcherKey) + 1);
			} else {
				offered.put(fetcherKey, 1);
			}
		}
	}

	private void doStatusRequestWrapper(Collection<StatusRequestWrapper> ss,
			Map<String, Integer> ignored, Map<String, Integer> offered)
			throws InterruptedException {
		for (StatusRequestWrapper srw : ss) {
			boolean offeredFlag = false;
			do {
				offeredFlag = IautosResource.statusQueue.offer(srw, 500,
						TimeUnit.MILLISECONDS);
				getLogger().debug(
						"offer "
								+ srw.getFetcher().getRequestWrapper().getUrl()
								+ " delay " + srw.getDelay(TimeUnit.SECONDS)
								+ "seconds");
			} while (!offeredFlag);
		}
	}

	private void logFetched(Map<String, Integer> offered) {
		for (Map.Entry<String, Integer> offer : offered.entrySet()) {
			getLogger().info(
					"append fetcher task:" + offer.getValue() + " "
							+ offer.getKey());
		}
	}

	private void logIgnored(Map<String, Integer> ignored) {
		for (Map.Entry<String, Integer> ignore : ignored.entrySet()) {
			getLogger().info(
					"ignore fetcher task:" + ignore.getValue() + " "
							+ ignore.getKey());
		}
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
						this.getLogger().error(
								"extract error :"
										+ cb.getResponseWrapper().getUrl());
						continue;
					} catch (TimeoutException e) {
						e.printStackTrace();
						this.getLogger().error(
								"extract error :"
										+ cb.getResponseWrapper().getUrl());
						continue;
					}

					if (null == result) {
						continue;
					}

					Map<String, Integer> offered = new HashMap<String, Integer>();
					Map<String, Integer> ignored = new HashMap<String, Integer>();

					if (result.containsKey(FetcherConstants.Fetcher)) {

						@SuppressWarnings("unchecked")
						Collection<Fetcher> fs = (Collection<Fetcher>) result
								.get(FetcherConstants.Fetcher);

						this.<Fetcher> doFetcher(fs, ignored, offered);
					}

					if (result.containsKey(FetcherConstants.STATUS)) {

						@SuppressWarnings("unchecked")
						Collection<StatusRequestWrapper> ss = (Collection<StatusRequestWrapper>) result
								.get(FetcherConstants.STATUS);

						this.doStatusRequestWrapper(ss, ignored, offered);
					}

					this.logFetched(offered);
					this.logIgnored(ignored);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}
	}
}
