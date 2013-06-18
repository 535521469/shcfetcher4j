package pp.corleone.service.iautos;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.corleone.service.Fetcher;
import pp.corleone.service.StatusRequestWrapper;

public class StatusThread extends Thread {

	protected final Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void run() {

		StatusRequestWrapper statusRequestWrapper = null;

		while (!isInterrupted()) {

			try {
				statusRequestWrapper = IautosResource.statusQueue.poll(5,
						TimeUnit.SECONDS);

				if (null == statusRequestWrapper) {
					getLogger().info(
							"not reach check time, queue size :"
									+ IautosResource.statusQueue.size());
					continue;
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Fetcher statusFetcher = statusRequestWrapper.getFetcher();

			boolean offered = false;

			do {
				try {
					offered = IautosResource.fetchQueue.offer(statusFetcher, 5,
							TimeUnit.SECONDS);

					getLogger().debug(
							"offer status fetcher "
									+ statusFetcher.getRequestWrapper()
											.getUrl());

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (!offered);

		}

	}
}
