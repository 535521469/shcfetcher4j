package pp.corleone.service.iautos.detail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.nodes.Document;

import pp.corleone.dao.DaoUtil;
import pp.corleone.dao.iautos.IautosCarInfoDao;
import pp.corleone.dao.iautos.IautosSellerInfoDao;
import pp.corleone.domain.iautos.FetcherConstants;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosSellerInfo;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;
import pp.corleone.service.RequestWrapper.PriorityEnum;
import pp.corleone.service.iautos.IautosConstant;
import pp.corleone.service.iautos.seller.IautosSellerCallback;
import pp.corleone.service.iautos.seller.IautosSellerFetcher;

public class IautosDetailCallback extends Callback {

	private IautosSellerInfoDao sellerDao;
	private IautosCarInfoDao carDao;

	public IautosDetailCallback() {
		Session session = this.getSession();
		this.setSellerDao(new IautosSellerInfoDao(session));
		this.setCarDao(new IautosCarInfoDao(session));
	}

	public Session getSession() {
		return DaoUtil.getCurrentSession();
	}

	public IautosSellerInfoDao getSellerDao() {
		return sellerDao;
	}

	public IautosCarInfoDao getCarDao() {
		return carDao;
	}

	public void setCarDao(IautosCarInfoDao carDao) {
		this.carDao = carDao;
	}

	public void setSellerDao(IautosSellerInfoDao sellerDao) {
		this.sellerDao = sellerDao;
	}

	/**
	 * build shop fetcher
	 * 
	 * @param ele
	 * @return
	 */
	private Fetcher getSellerFetcher(String shopUrl) {
		Callback sellerCallback = new IautosSellerCallback();
		RequestWrapper referRequestWrapper = this.getResponseWrapper()
				.getReferRequestWrapper();

		RequestWrapper requestWrapper = new RequestWrapper(shopUrl,
				sellerCallback, this.getResponseWrapper()
						.getReferRequestWrapper(),
				PriorityEnum.SELLER.getValue(), referRequestWrapper.getMeta(),
				referRequestWrapper.getContext());

		Fetcher sellerFetcher = new IautosSellerFetcher(requestWrapper);
		return sellerFetcher;
	}

	@Override
	public Map<String, Collection<?>> call() throws Exception {
		Map<String, Collection<?>> fetched = new HashMap<String, Collection<?>>();
		Collection<Fetcher> fetchers = new ArrayList<Fetcher>();
		fetched.put(FetcherConstants.Fetcher, fetchers);

		Document doc = this.getResponseWrapper().getDoc();

		if (IautosDetailExtractUtil2.isDuringValidate(doc)) {
			getLogger().info(
					"body is blank:" + this.getResponseWrapper().getUrl());
			return null;
		}

		IautosCarInfo ici = (IautosCarInfo) this.getResponseWrapper()
				.getContext().get(IautosConstant.CAR_INFO);

		ici.setCarSourceUrl(this.getResponseWrapper().getUrl());

		Transaction tx = this.getSession().beginTransaction();
		try {
			IautosSellerInfo isi = null;

			// get shop url
			String shopUrl = IautosDetailExtractUtil2.getSellerUrl(doc,
					this.getResponseWrapper());

			// String shopUrl = "http://reallycar.iautos.cn/";

			if (!StringUtils.isBlank(shopUrl)) {

				if (shopUrl.trim().length() > 0
						&& !shopUrl.trim().equals(
								this.getResponseWrapper().getUrl())) {
					// set seller url
					ici.setShopUrl(shopUrl);

					// get shop fetcher
					Fetcher sellerFetcher = this.getSellerFetcher(shopUrl);

					isi = this.getSellerDao().getByShopUrl(shopUrl,
							this.getSession());

					if (null != isi) {
						// if shop exist
						getLogger().debug(
								"shop already exist " + isi.getSeqID() + ","
										+ isi.getShopUrl());

						if (null == isi.getShopPhone()) {
							isi.setShopPhone(ici.getContacterPhone());
						}

					} else {
						// add shop fetcher
						fetchers.add(sellerFetcher);

						isi = new IautosSellerInfo();
						isi.setShopUrl(ici.getShopUrl());

						if (sellerFetcher.isIgnore()) {
							// if seller fetch will ignore
							// use the contacter phone as shop phone
							isi.setShopPhone(ici.getContacterPhone());
						}
					}

					// set shop
					ici.setIautosSellerInfo(isi);
				}

			} else {
				getLogger().debug(
						"shop url is null ,"
								+ this.getResponseWrapper().getUrl());
			}

			IautosDetailExtractUtil2.getDetailItem(doc, ici,
					this.getResponseWrapper());
			// this.getCarDao().addCarInfo(ici);

			if (null == this.getCarDao().getByCarUrlAndDeclareDate(
					ici.getCarSourceUrl(), ici.getDeclareDate(),
					this.getSession())) {
				this.getCarDao().addCarInfo(ici, this.getSession());

			}
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}

		return fetched;

	}
}
