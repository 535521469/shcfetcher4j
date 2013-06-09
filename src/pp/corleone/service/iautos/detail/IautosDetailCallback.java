package pp.corleone.service.iautos.detail;

import java.text.DateFormat;
import java.text.ParseException;
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
import org.slf4j.MarkerFactory;

import pp.corleone.dao.DaoUtil;
import pp.corleone.dao.iautos.IautosCarInfoDao;
import pp.corleone.dao.iautos.IautosSellerInfoDao;
import pp.corleone.domain.iautos.FetcherConstants;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosSellerInfo;
import pp.corleone.domain.iautos.IautosCarInfo.IautosStatusCode;
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

	private String getSellerUrl(Element ele) {
		String shopUrl = null;
		Elements aTags = ele.select("div>a");
		Element aTag = aTags.first();
		shopUrl = aTag.attr("href");
		return shopUrl;
	}

	private void getDetailItem(Element doc, IautosCarInfo ici) {

		Element detailDivTag = doc.select("#car_detail").first();

		this.fillContacterAndPhone(doc, ici);

		this.fillPriceAndStatus(detailDivTag, ici);
		this.fillDisplacementAndGearbox(detailDivTag, ici);

		Element parameterTableTag = doc.select("table.parameter").first();
		this.fillParameters(parameterTableTag, ici);

		this.fillParkAddress(detailDivTag, ici);

		// this.fillTitle(detailDivTag, ici);
		// fill title at last , title = brand + displacement + gearbox
		this.fillTitle(ici);

		Date now = new Date();
		ici.setFetchDate(now);
		ici.setLastActiveDate(now);

	}

	private void fillContacterAndPhone(Element doc, IautosCarInfo ici) {
		Element contacterPhoneDivTag = doc.select("div.div2_tel").first();

		if (null != contacterPhoneDivTag) {
			String contacterAndPhone = contacterPhoneDivTag.text();

			// \uFF1A : colon , fen hao
			String[] contacterAndPhoneArray = contacterAndPhone.split("\uFF1A");
			if (contacterAndPhoneArray.length > 1) {
				ici.setContacter(contacterAndPhoneArray[0]);
				ici.setContacterPhone(contacterAndPhoneArray[1]);
			} else {
				ici.setContacterPhone(contacterAndPhone);
			}
		} else {
			this.getLogger().warn(
					"... contacter is null:"
							+ this.getResponseWrapper().getUrl());
		}
	}

	private void fillParameters(Element parameters, IautosCarInfo ici) {
		Element parameterTag = parameters.select("table.parameter").first();
		Elements basicTrTags = parameterTag.select("tr");

		/* suo shu pin pai */
		Element brandTdTag = basicTrTags.get(2).select("td").get(0).select("a")
				.first(); // suo shu pin pai
		ici.setBrand(brandTdTag.text());

		/* xing shi li cheng */
		Element roadHaulTdTag = basicTrTags.get(5).select("td").first();
		// \u884C\u9A76\u91CC\u7A0B\uFF1A : xing shi li cheng
		String roadHaul = StringUtils.replace(roadHaulTdTag.text(),
				"\u884C\u9A76\u91CC\u7A0B\uFF1A", "");
		// \u516C\u91CC : kilometer , gong li
		roadHaul = StringUtils.replace(roadHaul, "\u516C\u91CC", "");
		ici.setRoadHaul(roadHaul);

		/* color */
		Element colorTdTag = basicTrTags.get(6).select("td").get(1);
		// \u8F66\u8EAB\u989C\u8272\uFF1A : che shen yan se
		// \uFF0C : Chinese comma
		String color = StringUtils.replace(colorTdTag.text(),
				"\u8F66\u8EAB\u989C\u8272\uFF1A", "").split("\uFF0C")[0];
		ici.setColor(color);

		/* update date */
		Element updateDateTdTag = basicTrTags.get(0).select("td").get(1);
		// \u66F4\u65B0\u65F6\u95F4\uFF1A : update date and a chinese colon
		String updateDateString = StringUtils.replace(updateDateTdTag.text(),
				"\u66F4\u65B0\u65F6\u95F4\uFF1A", "");

		try {
			Date updateDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
					.parse(updateDateString);
			if (ici.getDeclareDate() == null) {
				ici.setDeclareDate(updateDate);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		/* shang pai shi jian or chu deng ri qi */
		Element licensedDateTdTagElement = basicTrTags.get(4).select("td")
				.get(0);
		// \u521D\u767B\u65E5\u671F\uFF1A : chu deng ri qi with an chinese colon

		String licensedDateString = StringUtils.replace(
				licensedDateTdTagElement.text(),
				"\u521D\u767B\u65E5\u671F\uFF1A", "");
		ici.setLicenseDate(licensedDateString);

		// \u751F\u4EA7\u5382\u5BB6\uFF1A : sheng chan chang shang ,
		// manufacturer
		Element manufacturerTdTagElement = basicTrTags.get(1).select("td")
				.first();
		String manufacturer = StringUtils.replace(
				manufacturerTdTagElement.text(),
				"\u751F\u4EA7\u5382\u5BB6\uFF1A", "");
		ici.setManufacturer(manufacturer);

	}

	@SuppressWarnings("unused")
	@Deprecated
	private void fillTitle(Element detail, IautosCarInfo ici) {
		Element titleSpan = detail.select("div>span.span1").first();
		ici.setTitle(titleSpan.text());
	}

	private void fillTitle(IautosCarInfo ici) {
		ici.setTitle(ici.getBrand() + " " + ici.getDisplacement() + " "
				+ ici.getGearbox());
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
			Elements spanTags = detailDivTag.select("div>span");
			for (Element spanTag : spanTags) {
				// \u9884\u552E\u4EF7\u683C : price ; yu shou jia ge
				if (spanTag.text().indexOf("\u9884\u552E\u4EF7\u683C") != -1) {
					priceFontTag = spanTag.select("font").first();
				}
			}
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
		if (status.equals(ici.getPrice())) {
			ici.setStatusType(IautosStatusCode.STATUS_TYPE_FOR_SALE);
		} else if ("\u5DF2\u552E".equals(status)) {
			// sold yi shou
			ici.setStatusType(IautosStatusCode.STATUS_TYPE_SOLD);
		} else if ("\u903E\u671F".equals(status)) {
			// overdue yu qi
			ici.setStatusType(IautosStatusCode.STATUS_TYPE_OVERDUE);
		} else {
			throw new IllegalArgumentException("StatusType is out of control "
					+ status + "---" + ici.getPrice() + "..."
					+ this.getResponseWrapper().getUrl());
		}
	}

	private void fillParkAddress(Element detail, IautosCarInfo ici) {
		Element detailDivTag = detail.select("div.div1").first();
		if (detailDivTag == null) {
			detailDivTag = detail.select("div.div4").first();
		}

		if (detailDivTag != null) {
			Elements pTags = detailDivTag.select("p");
			if (null != pTags && pTags.size() > 0) {
				for (Element pTag : pTags) {
					String tempString = pTag.text();

					// \u770B\u8F66\u5730\u5740\uFF1A : kan che di zhi
					// park address
					if (tempString.indexOf("\u770B\u8F66\u5730\u5740\uFF1A") != -1) {
						String parkAddress = StringUtils.replace(tempString,
								"\u770B\u8F66\u5730\u5740\uFF1A", "");
						ici.setParkAddress(parkAddress);
						break;
					}
				}
			}
		}

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

			// if (divTag != null || true) {
			if (divTag != null) {
				// check if shop is exist

				// get shop url
				String shopUrl = this.getSellerUrl(divTag);
				// String shopUrl = "http://reallycar.iautos.cn/";

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
					} else {
						// add shop fetcher
						fetchers.add(sellerFetcher);

						isi = new IautosSellerInfo();
						isi.setShopUrl(ici.getShopUrl());

						// save to db
						// this.getSellerDao().addShopInfo(isi,
						// this.getSession());
					}

					// set shop
					ici.setIautosSellerInfo(isi);
				}

			} else {
				getLogger().debug(
						"shop url is null ,"
								+ this.getResponseWrapper().getUrl());
			}

			this.getDetailItem(doc, ici);
			// this.getCarDao().addCarInfo(ici);

			if (null != this.getCarDao().getByCarUrlAndDeclareDate(
					ici.getCarSourceUrl(), ici.getDeclareDate(),
					this.getSession())) {
			} else {
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
