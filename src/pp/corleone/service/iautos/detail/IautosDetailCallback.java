package pp.corleone.service.iautos.detail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pp.corleone.dao.DaoUtil;
import pp.corleone.dao.iautos.IautosCarInfoDao;
import pp.corleone.dao.iautos.IautosSellerInfoDao;
import pp.corleone.domain.iautos.FetcherConstants;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosConstant;
import pp.corleone.domain.iautos.IautosSellerInfo;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;
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
	private Fetcher getSellerFetcher(Element ele) {

		String shopUrl = null;
		Elements aTags = ele.select("div>a");
		Element aTag = aTags.first();
		shopUrl = aTag.attr("href");
		Callback sellerCallback = new IautosSellerCallback();
		RequestWrapper requestWrapper = new RequestWrapper(shopUrl,
				sellerCallback, this.getResponseWrapper()
						.getReferRequestWrapper());
		Fetcher sellerFetcher = new IautosSellerFetcher(requestWrapper);
		return sellerFetcher;
	}

	private void getDetailItem(Element doc, IautosCarInfo ici) {

		Element ele = doc.select("#car_detail").first();

		Element titleSpan = ele.select("div>span.span1").first();
		Element statusTypeSpan = ele.select("div>span.span2").first();
		Element detailDivTag = ele.select("div.div1").first();
		Element carStyleATag = detailDivTag.select("div").get(0)
				.select("span>a[target=_blank]").first(); // style
		Element priceFontTag = detailDivTag.select("div").get(2)
				.select("span>font").first();
		Element pTag = detailDivTag.select("p").get(0);
		Element displacementATag = pTag.select("a").get(0);
		Element gearboxATag = pTag.select("a").get(1);
		Element seeCarAddressPTag = detailDivTag.select("p").get(1);
		Element basicTableTag = doc.select("table.parameter").first();
		Elements basicTrTags = basicTableTag.select("tr");
		Element carTypeATag = basicTrTags.get(2).select("td").get(0)
				.select("a").first(); // pin pai
		Element roadHaulTdTag = basicTrTags.get(5).select("td").first();
		Element colorTdTag = basicTrTags.get(6).select("td").get(1);

		Element contacterPhoneDivTag = doc.select("div.div2_tel").first();

		ici.setTitle(titleSpan.text());
		ici.setPrice(priceFontTag.text());
		ici.setDisplacement(displacementATag.text());
		ici.setGearbox(gearboxATag.text());
		ici.setCarType(carTypeATag.text());
		ici.setRoadHaul(roadHaulTdTag.text());
		ici.setColor(colorTdTag.text());

		if (statusTypeSpan.text() == priceFontTag.text()) {
			ici.setStatusType(IautosCarInfo.STATUS_TYPE_FOR_SALE);
		} else if ("\u5DF2\u552E".equals(statusTypeSpan.text().trim())) {
			// sold
			ici.setStatusType(IautosCarInfo.STATUS_TYPE_SOLD);
		} else if ("\u903E\u671F".equals(statusTypeSpan.text().trim())) {
			// overdue
			ici.setStatusType(IautosCarInfo.STATUS_TYPE_OVERDUE);
		}

		Date now = new Date();
		ici.setFetchDate(now);
		ici.setLastActiveDate(now);

		ici.setContacterPhone(contacterPhoneDivTag.text());

	}

	@Override
	public Map<String, Collection<?>> call() throws Exception {
		Map<String, Collection<?>> fetched = new HashMap<String, Collection<?>>();
		Collection<Fetcher> fetchers = new ArrayList<Fetcher>();
		fetched.put(FetcherConstants.Fetcher, fetchers);

		Document doc = this.getResponseWrapper().getDoc();
		IautosCarInfo ici = (IautosCarInfo) this.getResponseWrapper()
				.getContext().get(IautosConstant.CAR_INFO);

		ici.setCarSourceUrl(this.getResponseWrapper().getUrl());

		Transaction tx = this.getSession().beginTransaction();
		try {
			if (IautosCarInfo.SELLER_TYPE_SHOP == ici.getSellerType()) {
				IautosSellerInfo isi = null;
				Element divTag = doc.select("div.box2>div.pjzl").first();

				if (divTag != null) {
					// check if shop is exist

					// get shop fetcher
					Fetcher sellerFetcher = this.getSellerFetcher(divTag);
					// get shop url
					String shopUrl = sellerFetcher.getRequestWrapper().getUrl();

					// set seller url
					ici.setShopUrl(shopUrl);

					isi = this.getSellerDao().getByShopUrl(shopUrl,
							this.getSession());

					if (null != isi) {
						// if shop exist
						getLogger().debug(
								"shop already exist " + isi.getSeqID() + ","
										+ isi.getShopUrl());
					} else {
						// add shop fetcher
						fetchers.add(sellerFetcher);

						isi = new IautosSellerInfo();
						isi.setShopUrl(ici.getShopUrl());

						// save to db
						this.getSellerDao().addShopInfo(isi, this.getSession());
					}

					// set shop
					ici.setIautosSellerInfo(isi);

				} else {
					getLogger().warn(
							"shop url is null ,"
									+ this.getResponseWrapper().getUrl());
				}
			}

			this.getDetailItem(doc, ici);
			// this.getCarDao().addCarInfo(ici);
			this.getCarDao().addCarInfo(ici, this.getSession());

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}

		return fetched;

	}
}
