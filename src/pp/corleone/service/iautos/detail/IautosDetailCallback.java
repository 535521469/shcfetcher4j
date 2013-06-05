package pp.corleone.service.iautos.detail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import pp.corleone.domain.iautos.IautosSellerInfo;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;
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

		Element detailDivTag = doc.select("#car_detail").first();

		this.fillContacterAndPhone(doc, ici);

		this.fillPriceAndStatus(detailDivTag, ici);
		this.fillTitle(detailDivTag, ici);
		this.fillDisplacementAndGearbox(detailDivTag, ici);

		Element parameterTableTag = doc.select("table.parameter").first();
		this.fillParameters(parameterTableTag, ici);

		Date now = new Date();
		ici.setFetchDate(now);
		ici.setLastActiveDate(now);

	}

	private void fillContacterAndPhone(Element doc, IautosCarInfo ici) {
		Element contacterPhoneDivTag = doc.select("div.div2_tel").first();

		String contacterAndPhone = contacterPhoneDivTag.text();

		// \uFF1A : colon , fen hao
		String[] contacterAndPhoneArray = contacterAndPhone.split("\uFF1A");
		if (contacterAndPhoneArray.length > 1) {
			ici.setContacter(contacterAndPhoneArray[0]);
			ici.setContacterPhone(contacterAndPhoneArray[1]);
		} else {
			ici.setContacterPhone(contacterAndPhone);
		}
	}

	private void fillParameters(Element parameters, IautosCarInfo ici) {
		Element parameterTag = parameters.select("table.parameter").first();
		Elements basicTrTags = parameterTag.select("tr");
		Element carTypeATag = basicTrTags.get(2).select("td").get(0)
				.select("a").first(); // pin pai
		Element roadHaulTdTag = basicTrTags.get(5).select("td").first();
		Element colorTdTag = basicTrTags.get(6).select("td").get(1);

		ici.setCarType(carTypeATag.text());

		// \u884C\u9A76\u91CC\u7A0B\uFF1A : xing shi li cheng
		String roadHaul = StringUtils.replace(roadHaulTdTag.text(),
				"\u884C\u9A76\u91CC\u7A0B\uFF1A", "");
		// \u516C\u91CC : kilometer , gong li
		roadHaul = StringUtils.replace(roadHaul, "\u516C\u91CC", "");
		ici.setRoadHaul(roadHaul);

		// \u8F66\u8EAB\u989C\u8272\uFF1A : che shen yan se
		// \uFF0C : Chinese comma
		String color = StringUtils.replace(colorTdTag.text(),
				"\u8F66\u8EAB\u989C\u8272\uFF1A", "").split("\uFF0C")[0];
		ici.setColor(color);

	}

	private void fillTitle(Element detail, IautosCarInfo ici) {
		Element titleSpan = detail.select("div>span.span1").first();
		ici.setTitle(titleSpan.text());
	}

	private void fillDisplacementAndGearbox(Element detail, IautosCarInfo ici) {
		Element detailDivTag = detail.select("div.div1").first();
		Element displacementATag = null;
		Element gearboxATag = null;
		if (detailDivTag == null) {
			detailDivTag = detail.select("div.div4").first();
			Element divTag = detailDivTag.select(">div").get(3);
			Element spanTag = divTag.select("span").get(0);

			Elements aTags = spanTag.select("a");
			displacementATag = aTags.get(0);
			gearboxATag = aTags.get(1);
		} else {
			Elements aTags = detailDivTag.select("p>a");
			displacementATag = aTags.get(0);
			gearboxATag = aTags.get(1);
		}
		if (displacementATag != null) {
			ici.setDisplacement(displacementATag.text());
		} else {
			throw new IllegalArgumentException("Displacement");
		}
		if (gearboxATag != null) {
			ici.setGearbox(gearboxATag.text());
		} else {
			throw new IllegalArgumentException("Gearbox");
		}
	}

	private void fillPriceAndStatus(Element detail, IautosCarInfo ici) {
		Element detailDivTag = detail.select("div.div1").first();
		Element priceFontTag = null;
		if (detailDivTag == null) {
			detailDivTag = detail.select("div.div4").first();
			Element divTag = detailDivTag.select(">div").get(1);
			Element spanTag = divTag.select("span").get(0);

			Elements fontTags = spanTag.select("font");
			priceFontTag = fontTags.get(0);
		} else {
			Elements aTags = detailDivTag.select("p>a");
			priceFontTag = aTags.get(0);
		}
		if (priceFontTag != null) {
			ici.setPrice(priceFontTag.text());
		} else {
			throw new IllegalArgumentException("Price");
		}

		Element statusSpanTag = null;
		statusSpanTag = detail.select(">div>span.span2").first();
		String status = statusSpanTag.text().trim();
		if (status.length() == 0) {
			throw new IllegalArgumentException("StatusType is blank");
		}
		if (status == ici.getPrice()) {
			ici.setStatusType(IautosCarInfo.STATUS_TYPE_FOR_SALE);
		} else if ("\u5DF2\u552E".equals(status)) {
			// sold yi shou
			ici.setStatusType(IautosCarInfo.STATUS_TYPE_SOLD);
		} else if ("\u903E\u671F".equals(status)) {
			// overdue yu qi
			ici.setStatusType(IautosCarInfo.STATUS_TYPE_OVERDUE);
		} else {
			throw new IllegalArgumentException("StatusType is out of control"
					+ status);
		}
	}

	private void fillWatchAddress(Element doc, IautosCarInfo ici) {
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
