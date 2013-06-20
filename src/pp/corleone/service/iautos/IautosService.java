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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.corleone.ConfigManager;
import pp.corleone.dao.DaoUtil;
import pp.corleone.dao.iautos.IautosCarInfoDao;
import pp.corleone.dao.iautos.IautosSellerInfoDao;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosCarInfo.IautosStatusCode;
import pp.corleone.domain.iautos.IautosSellerInfo;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;
import pp.corleone.service.RequestWrapper.PriorityEnum;
import pp.corleone.service.Service;
import pp.corleone.service.StatusRequestWrapper;
import pp.corleone.service.iautos.changecity.IautosChangeCityCallback;
import pp.corleone.service.iautos.changecity.IautosChangeCityFetcher;
import pp.corleone.service.iautos.seller.IautosSellerCallback;
import pp.corleone.service.iautos.seller.IautosSellerFetcher;
import pp.corleone.service.iautos.status.IautosStatusCallback;
import pp.corleone.service.iautos.status.IautosStatusFetcher;

public class IautosService extends Service {

	public static void main(String[] args) {

		ConfigManager configManager = ConfigManager.getInstance();

		IautosService is = new IautosService();

		is.getLogger().info("start-------------------");

		String statusCheckFlag = configManager.getConfigItem(
				IautosConstant.STATUS_CHECK_FLAG, "1");
		String fetchFlag = configManager.getConfigItem(
				IautosConstant.ONGOING_FLAG, "1");

		if ("1".equals(fetchFlag)) {
			is.initOnGoing();
		}

		if ("1".equals(statusCheckFlag)) {
			is.initStatusCheck();
			is.statusFetch();
		}

		is.fetch();
		is.extract();

	}

	public void initOnGoing() {
		ScheduledThreadPoolExecutor pe = (ScheduledThreadPoolExecutor) Executors
				.newScheduledThreadPool(1);
		ChangeCityFetcherManager ccf = this.new ChangeCityFetcherManager();
		long ongoingCycleDelay = Long.valueOf(ConfigManager.getInstance()
				.getConfigItem(IautosConstant.ONGOING_CYCLE_DELAY, 86400));
		pe.scheduleAtFixedRate(ccf, 0, ongoingCycleDelay, TimeUnit.SECONDS);
	}

	public void initStatusCheck() {
		ScheduledThreadPoolExecutor pe = (ScheduledThreadPoolExecutor) Executors
				.newScheduledThreadPool(1);
		StatusFetcherManager sfm = this.new StatusFetcherManager();
		pe.scheduleAtFixedRate(sfm, 0, sfm.getStatusCheckRange(),
				TimeUnit.SECONDS);
	}

	@Override
	public void fetch() {
		Thread fetcher = new FetcherThread();
		fetcher.start();
	}

	public void statusFetch() {
		Thread status = new StatusThread();
		status.start();
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

			String city = ConfigManager.getInstance().getConfigItem(
					IautosConstant.CITIES, null);

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

	class StatusFetcherManager implements Runnable {

		protected final Logger getLogger() {
			return LoggerFactory.getLogger(this.getClass());
		}

		int statusCheckRange;
		int statusDelay;

		public int getStatusCheckRange() {
			return statusCheckRange;
		}

		public void setStatusCheckRange(int statusCheckRange) {
			this.statusCheckRange = statusCheckRange;
		}

		public int getStatusDelay() {
			return statusDelay;
		}

		public void setStatusDelay(int statusDelay) {
			this.statusDelay = statusDelay;
		}

		{
			this.setCarDao(new IautosCarInfoDao(this.getSession()));

			ConfigManager configManager = ConfigManager.getInstance();

			statusCheckRange = configManager.getConfigItem(
					IautosConstant.STATUS_CHECK_RANGE, Runtime.getRuntime()
							.availableProcessors());

			statusDelay = configManager.getConfigItem(
					IautosConstant.STATUS_DELAY, 28800);

		}

		private IautosCarInfoDao carDao;

		public StatusFetcherManager() {
		}

		public Session getSession() {
			return DaoUtil.getCurrentSession();
		}

		public IautosCarInfoDao getCarDao() {
			return carDao;
		}

		public void setCarDao(IautosCarInfoDao carDao) {
			this.carDao = carDao;
		}

		@Override
		public void run() {

			this.getLogger()
					.info("...... query carinfo for status check......");

			DateTime dateTime = new DateTime();
			if (this.getStatusCheckRange() > 1) {
				dateTime = dateTime.minusSeconds(this.getStatusDelay());
				dateTime = dateTime.plusSeconds(this.getStatusCheckRange());
			}

			this.getLogger().info(
					"...... before last active date time:"
							+ dateTime.toString("yyyy-MM-dd HH:mm:ss"));

			Transaction tx = this.getSession().beginTransaction();

			List<IautosCarInfo> icis = null;
			try {
				icis = this.getCarDao().listByStatusCodeAndLastActiveDateTime(
						IautosStatusCode.STATUS_TYPE_FOR_SALE,
						dateTime.toString("yyyy-MM-dd HH:mm:ss"),
						this.getSession());
				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollback();
			}

			if (null != icis) {
				List<StatusRequestWrapper> statusRequestWrappers = new ArrayList<StatusRequestWrapper>();
				for (IautosCarInfo iautosCarInfo : icis) {

					Callback callback = new IautosStatusCallback();
					Map<String, Object> contextMap = new HashMap<String, Object>();
					contextMap.put(IautosConstant.CAR_INFO, iautosCarInfo);

					RequestWrapper requestWrapper = new RequestWrapper(
							iautosCarInfo.getCarSourceUrl(), callback, null,
							PriorityEnum.STATUS.getValue(), null, contextMap);

					Fetcher fetcher = new IautosStatusFetcher(requestWrapper);

					StatusRequestWrapper statusRequestWrapper = new StatusRequestWrapper(
							fetcher);

					statusRequestWrappers.add(statusRequestWrapper);

				}
				IautosResource.statusQueue.addAll(statusRequestWrappers);
				this.getLogger().info(
						"...... add status:" + statusRequestWrappers.size());
			}
		}
	}
}
