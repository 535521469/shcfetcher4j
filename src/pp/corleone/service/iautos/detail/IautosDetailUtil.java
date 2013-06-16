package pp.corleone.service.iautos.detail;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosCarInfo.IautosStatusCode;
import pp.corleone.service.ResponseWrapper;

public class IautosDetailUtil {

	protected static final Logger getLogger() {
		return LoggerFactory.getLogger(IautosDetailUtil.class);
	}

	public static String getSellerUrl(Element ele,
			ResponseWrapper responseWrapper) {
		String shopUrl = null;
		Elements aTags = ele.select("div>a");
		Element aTag = aTags.first();
		shopUrl = aTag.attr("href");
		return shopUrl;
	}

	public static void getDetailItem(Element doc, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {

		fillContacterAndPhone(doc, ici, responseWrapper);


		Element detailDivTag = doc.select("#car_detail").first();
		fillPriceAndStatus(detailDivTag, ici, responseWrapper);
		fillDisplacementAndGearbox(detailDivTag, ici, responseWrapper);
		fillParkAddress(detailDivTag, ici, responseWrapper);

		Element parameterTableTag = doc.select("table.parameter").first();
		fillParameters(parameterTableTag, ici, responseWrapper);
		// this.fillTitle(detailDivTag, ici);
		// fill title at last , title = brand + displacement + gearbox
		fillTitle(ici, responseWrapper);

		Date now = new Date();
		ici.setFetchDate(now);
		ici.setLastActiveDate(now);

	}

	public static void fillContacterAndPhone(Element doc, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {
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
			getLogger().warn(
					"... contacter is null:" + responseWrapper.getUrl());
		}
	}

	public static void fillParameters(Element parameters, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {
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

	@Deprecated
	public static void fillTitle(Element detail, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {
		Element titleSpan = detail.select("div>span.span1").first();
		ici.setTitle(titleSpan.text());
	}

	public static void fillTitle(IautosCarInfo ici,
			ResponseWrapper responseWrapper) {
		ici.setTitle(ici.getBrand() + " " + ici.getDisplacement() + " "
				+ ici.getGearbox());
	}

	public static void fillDisplacementAndGearbox(Element detail,
			IautosCarInfo ici, ResponseWrapper responseWrapper) {
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

	public static IautosStatusCode getStatusCode(String priceString,
			String status) {

		if (status.equals(priceString)) {
			return IautosStatusCode.STATUS_TYPE_FOR_SALE;
		} else if ("\u5DF2\u552E".equals(status)) {
			// sold yi shou
			return IautosStatusCode.STATUS_TYPE_SOLD;
		} else if ("\u903E\u671F".equals(status)) {
			// overdue yu qi
			return IautosStatusCode.STATUS_TYPE_OVERDUE;
		}
		return null;
	}

	public static void fillPriceAndStatus(Element detail, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {

		String priceString = getPriceLiteral(detail);

		if (priceString.length() == 0) {
			throw new IllegalArgumentException("StatusType is blank");
		}
		ici.setPrice(priceString);

		String status = getStatusLiteral(detail);
		if (status.length() == 0) {
			throw new IllegalArgumentException("StatusType is blank");
		}

		IautosStatusCode statusCode = getStatusCode(priceString, status);

		if (null == statusCode) {
			throw new IllegalArgumentException("status code ," + status);
		}

		ici.setStatusType(statusCode);

	}

	public static String getStatusLiteral(Element detail) {
		Element statusSpanTag = null;
		statusSpanTag = detail.select(">div>span.span2").first();
		if (null != statusSpanTag) {
			return statusSpanTag.text().trim();
		} else {
			throw new IllegalArgumentException("Status ");
		}
	}

	public static String getPriceLiteral(Element detail) {
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
			return priceFontTag.text().trim();
		} else {
			throw new IllegalArgumentException("Price");
		}
	}

	public static void fillParkAddress(Element detail, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {
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

}
