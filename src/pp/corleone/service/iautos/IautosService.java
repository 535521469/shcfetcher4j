package pp.corleone.service.iautos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.corleone.dao.DaoUtil;
import pp.corleone.dao.iautos.IautosSellerInfoDao;
import pp.corleone.domain.iautos.ConfigConstant;
import pp.corleone.domain.iautos.IautosSellerInfo;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;
import pp.corleone.service.RequestWrapper.PriorityEnum;
import pp.corleone.service.ResponseWrapper;
import pp.corleone.service.Service;
import pp.corleone.service.iautos.changecity.IautosChangeCityCallback;
import pp.corleone.service.iautos.changecity.IautosChangeCityFetcher;
import pp.corleone.service.iautos.seller.IautosSellerCallback;
import pp.corleone.service.iautos.seller.IautosSellerFetcher;

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
		pe.scheduleAtFixedRate(ccf, 0, 3600, TimeUnit.SECONDS);
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
			List<Fetcher> fs = new ArrayList<Fetcher>();
			// add change city fetcher
			Fetcher f = new IautosChangeCityFetcher(new RequestWrapper(
					IautosConstant.homePage + "city/",
					new IautosChangeCityCallback(cities), null,
					PriorityEnum.CHANGE_CITY));
			 fs.add(f);

			// add unfetched seller info fetcher
			Session session = DaoUtil.getCurrentSession();
			Transaction tx = session.beginTransaction();
			try {
				List<IautosSellerInfo> isis = new IautosSellerInfoDao(session)
						.getUnfetchedSellerInfos(session);
				tx.commit();
				for (IautosSellerInfo isi : isis) {
					Map<String, Object> contentMap = new HashMap<String, Object>();
					contentMap.put(IautosConstant.SELLER_INFO, isi);
					Fetcher sellerFetcher = new IautosSellerFetcher(
							new RequestWrapper(isi.getShopUrl(),
									new IautosSellerCallback(), null,
									PriorityEnum.SELLER.getValue(), null,
									contentMap));
					fs.add(sellerFetcher);
				}
				getLogger().info("add unfetched sellers :" + isis.size());
			} catch (Exception e) {
				tx.rollback();
			}

			Map<String, Integer> offered = new HashMap<String, Integer>();
			for (Object o : fs) {
				Fetcher fetcher = (Fetcher) o;
				String fetcherKey = fetcher.getClass().getName();
				boolean offeredFlag = false;
				do {

					try {
						offeredFlag = IautosResource.fetchQueue.offer(fetcher,
								500, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					getLogger().debug(
							"offer " + fetcher.getRequestWrapper().getUrl());

				} while (!offeredFlag);

				if (offered.containsKey(fetcherKey)) {
					offered.put(fetcherKey, offered.get(fetcherKey) + 1);
				} else {
					offered.put(fetcherKey, 1);
				}
			}

			for (Map.Entry<String, Integer> offer : offered.entrySet()) {
				getLogger().info(
						"add " + offer.getValue() + " fetcher task :"
								+ offer.getKey());
			}

		}
	}
}
