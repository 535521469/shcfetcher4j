package pp.corleone.service.iautos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.corleone.domain.iautos.ConfigConstant;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;
import pp.corleone.service.ResponseWrapper;
import pp.corleone.service.Service;
import pp.corleone.service.iautos.changecity.IautosChangeCityCallback;
import pp.corleone.service.iautos.changecity.IautosChangeCityFetcher;

public class IautosService extends Service {

	public static void main(String[] args) {
		IautosService is = new IautosService();

		is.init();
		is.fetch();
		is.extract();

	}

	public void init() {
		ScheduledThreadPoolExecutor pe = (ScheduledThreadPoolExecutor) Executors
				.newScheduledThreadPool(1);
		ChangeCityFetcherManager ccf = this.new ChangeCityFetcherManager();
		pe.scheduleAtFixedRate(ccf, 0, 600, TimeUnit.SECONDS);
	}

	@Override
	public void fetch() {
		Thread fetcher = new FetcherThread();
		fetcher.start();
	}

	@Override
	public void extract() {
		Thread extract = new ExtractThread();
		extract.start();
	}

	class ChangeCityFetcherManager implements Runnable {

		protected final Logger getLogger() {
			return LoggerFactory.getLogger(this.getClass());
		}

		@Override
		public void run() {

			String city = IautosResource.prop
					.getProperty(ConfigConstant.cities);

			this.getLogger().info("get cities config ->" + city);

			Set<String> cities = new HashSet<String>(Arrays.asList(city
					.split(",")));
			Fetcher f = new IautosChangeCityFetcher(new RequestWrapper(
					IautosConstant.homePage + "city/",
					new IautosChangeCityCallback(cities)));
			try {
				ResponseWrapper rw = f.call();
				if (null != rw) {
					Callback cb = f.getRequestWrapper().getCallback();
					cb.setResponseWrapper(rw);
					boolean offeredFlag = false;
					do {
						offeredFlag = IautosResource.extractQueue.offer(cb,
								500, TimeUnit.MILLISECONDS);
					} while (!offeredFlag);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
