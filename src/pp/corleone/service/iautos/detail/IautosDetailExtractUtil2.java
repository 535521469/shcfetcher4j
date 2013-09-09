package pp.corleone.service.iautos.detail;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.corleone.Log;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosCarInfo.IautosStatusCode;
import pp.corleone.service.ResponseWrapper;

public class IautosDetailExtractUtil2 {

	public static void LogException(ResponseWrapper responseWrapper, String msg) {
		Log.error(msg + ".." + responseWrapper.getUrl());
	}

	public static String getSellerUrl(Element ele,
			ResponseWrapper responseWrapper) {
		Element divElement = ele.select("div[class=glb-dealer-info]").first();
		if (null == divElement) {
			// do nothing
		} else {
			Element sellerElement = divElement.select("p>a").first();
			if (null == sellerElement) {
				// do nothing
			} else {
				return sellerElement.attr("href");
			}
		}
		return "";
	}

	public static void getDetailItem(Element doc, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {

		fillContacterAndPhone(doc, ici, responseWrapper);

		Element detailDivTag = doc.select("div[class=h136]").first();

		if (null == detailDivTag) {
			Log.error("detail div element is not exist : div[class=h136]",
					new RuntimeException());
		} else {
			fillPriceAndStatus(detailDivTag, ici, responseWrapper);
			fillDisplacement(detailDivTag, ici, responseWrapper);

			fillParkAddress_LicenseDate(detailDivTag, ici, responseWrapper);
		}

		fillRoadhaul(doc, ici, responseWrapper);
		fillCarColor(doc, ici, responseWrapper);
		fillBrandAndManufacturer(doc, ici, responseWrapper);
		fillGearbox(doc, ici, responseWrapper);

		fillTitle(ici, responseWrapper);

		Date now = new Date();
		ici.setFetchDate(now);
		ici.setLastActiveDate(now);

	}

	public static void fillGearbox(Element doc, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {

		Element gearboxElement = doc.select("li[title^=变速方式]").first();
		if (null == gearboxElement) {
			LogException(responseWrapper, "gearbox");
		} else {
			String gearbox = gearboxElement.text();
			if (StringUtils.isBlank(gearbox)) {
				LogException(responseWrapper, "gearbox");
			} else {
				ici.setGearbox(gearbox);
			}
		}
	}

	public static void fillContacterAndPhone(Element doc, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {
		// Element contacterPhoneDivTag = doc.select("div.div2_tel").first();

		Element contacterPhoneTag = doc.select("p[class=call-num]").first();
		String contacterPhone = null;

		if (null == contacterPhoneTag) { 
//			do nothing
		} else {
			contacterPhone = contacterPhoneTag.text();
		}

		if (StringUtils.isBlank(contacterPhone)) {
			contacterPhoneTag = doc.select("p[class=call-num call-num2]")
					.first();
			if (null == contacterPhoneTag) {
//				do nothing
			}else{
				contacterPhone = contacterPhoneTag.text();
			}
		}

		if (StringUtils.isBlank(contacterPhone)) {
			LogException(responseWrapper, "Contracter phone");
		} else {
			ici.setContacterPhone(contacterPhone);
		}

		Element contacterSpanTag = doc.select("div[class=seller-name]").first()
				.select("span").first();

		if (null == contacterSpanTag) {
			LogException(responseWrapper, "Contracter");
		} else {
			String contacter = contacterSpanTag.text();

			if (StringUtils.isBlank(contacter)) {
				LogException(responseWrapper, "Contracter");
			} else {
				ici.setContacter(contacter);
			}
		}

	}

	// public static void fillParameters(Element parameters, IautosCarInfo ici,
	// ResponseWrapper responseWrapper) {
	// Element parameterTag = parameters.select("table.parameter").first();
	// Elements basicTrTags = parameterTag.select("tr");
	//
	// /* suo shu pin pai */
	// Element brandTdTag = basicTrTags.get(2).select("td").get(0).select("a")
	// .first(); // suo shu pin pai
	// ici.setBrand(brandTdTag.text());
	//
	// /* xing shi li cheng */
	// Element roadHaulTdTag = basicTrTags.get(5).select("td").first();
	// // \u884C\u9A76\u91CC\u7A0B\uFF1A : xing shi li cheng
	// String roadHaul = StringUtils.replace(roadHaulTdTag.text(),
	// "\u884C\u9A76\u91CC\u7A0B\uFF1A", "");
	// // \u516C\u91CC : kilometer , gong li
	// roadHaul = StringUtils.replace(roadHaul, "\u516C\u91CC", "");
	// ici.setRoadHaul(roadHaul);
	//
	// /* color */
	// Element colorTdTag = basicTrTags.get(6).select("td").get(1);
	// // \u8F66\u8EAB\u989C\u8272\uFF1A : che shen yan se
	// // \uFF0C : Chinese comma
	// String color = StringUtils.replace(colorTdTag.text(),
	// "\u8F66\u8EAB\u989C\u8272\uFF1A", "").split("\uFF0C")[0];
	// ici.setColor(color);
	//
	// if (null == ici.getDeclareDate()) {
	//
	// /* update date */
	// Element updateDateTdTag = basicTrTags.get(0).select("td").get(1);
	// // \u66F4\u65B0\u65F6\u95F4\uFF1A : update date and a chinese colon
	// String updateDateString = StringUtils.replace(
	// updateDateTdTag.text(), "\u66F4\u65B0\u65F6\u95F4\uFF1A",
	// "");
	//
	// try {
	// Date updateDate = new SimpleDateFormat("yyyy-MM-dd")
	// .parse(updateDateString);
	// if (ici.getDeclareDate() == null) {
	// ici.setDeclareDate(updateDate);
	// }
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /* shang pai shi jian or chu deng ri qi */
	// Element licensedDateTdTagElement = basicTrTags.get(4).select("td")
	// .get(0);
	// // \u521D\u767B\u65E5\u671F\uFF1A : chu deng ri qi with an chinese colon
	//
	// String licensedDateString = StringUtils.replace(
	// licensedDateTdTagElement.text(),
	// "\u521D\u767B\u65E5\u671F\uFF1A", "");
	// ici.setLicenseDate(licensedDateString);
	//
	// // \u751F\u4EA7\u5382\u5BB6\uFF1A : sheng chan chang shang ,
	// // manufacturer
	// Element manufacturerTdTagElement = basicTrTags.get(1).select("td")
	// .first();
	// String manufacturer = StringUtils.replace(
	// manufacturerTdTagElement.text(),
	// "\u751F\u4EA7\u5382\u5BB6\uFF1A", "");
	// ici.setManufacturer(manufacturer);
	//
	// }

	public static void fillTitle(IautosCarInfo ici,
			ResponseWrapper responseWrapper) {
		ici.setTitle(ici.getBrand() + " " + ici.getDisplacement() + " "
				+ ici.getGearbox());
	}

	public static void fillCarColor(Element doc, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {
		Element listElement = doc.select("ul[class=list list1]").first();
		if (null == listElement) {
			LogException(responseWrapper, "Color");
		} else {
			Elements eles = listElement.select("li");
			for (Element element : eles) {
				if (element.select("span").first().text().trim().equals("车身颜色")) {
					ici.setColor(element.text().replace("车身颜色", "").trim());
				}
			}
			if (StringUtils.isBlank(ici.getColor())) {
				LogException(responseWrapper, "Color");
			}
		}
	}

	public static void fillBrandAndManufacturer(Element doc, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {
		Element listElement = doc.select("ul[class=list]").first();
		if (null == listElement) {
			LogException(responseWrapper, "Gearbox And Manufacturer");
		} else {
			Elements eles = listElement.select("li");
			for (Element element : eles) {
				if (element.select("span").first().text().trim().equals("生产厂家")) {
					ici.setManufacturer(element.text().replace("生产厂家", "")
							.trim());
				} else if (element.select("span").first().text().trim()
						.equals("品牌车系")) {
					ici.setBrand(element.text().replace("品牌车系", "").trim());
				}
			}
			if (StringUtils.isBlank(ici.getBrand())) {
				LogException(responseWrapper, "Brand");
			}
			if (StringUtils.isBlank(ici.getManufacturer())) {
				LogException(responseWrapper, "Manufacturer");
			}
		}
	}

	public static void fillDisplacement(Element detail, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {

		Element displacementTag = detail.select("dl[class=clearfix]").get(1)
				.select("dd").first();
		if (null == displacementTag) {
			LogException(responseWrapper, "displacement");
		} else {
			String displacement = displacementTag.text();
			String[] values = displacement.split("，");

			for (String value : values) {
				if (value.trim().endsWith("L")) {
					ici.setDisplacement(value);
					break;
				}
			}

			if (StringUtils.isBlank(ici.getDisplacement())) {
				LogException(responseWrapper, "displacement");
			}
		}
	}

	public static void fillRoadhaul(Element doc, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {
		Element roadhaulElement = doc.select("li[class=w95 cd-icon3-1]")
				.first();
		if (null == roadhaulElement) {
			LogException(responseWrapper, "roadhaul");
		} else {
			String roadhaul = roadhaulElement.text();
			if (StringUtils.isBlank(roadhaul)) {
				LogException(responseWrapper, "roadhaul");
			} else {
				ici.setRoadHaul(roadhaul);
			}
		}
	}

	public static void fillPriceAndStatus(Element detail, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {

		String priceString = getPriceLiteral(detail);

		if (StringUtils.isBlank(priceString)) {
			LogException(responseWrapper, "StatusType is blank");
		}
		ici.setPrice(priceString);
		IautosStatusCode status = getStatusLiteral(responseWrapper.getDoc());
		if (null == status) {
			LogException(responseWrapper, "Status ");
		} else {
			ici.setStatusType(status.getCode());
		}
	}

	public static IautosStatusCode getStatusLiteral(Element detail) {
		Element statusSpanTag = null;
		statusSpanTag = detail.select("div[class$=phone-show]").first();
		if (null == statusSpanTag) {
			return null;
		} else {
			String statusStyle = statusSpanTag.attr("class");
			if (StringUtils.isBlank(statusStyle)) {
				return null;
			} else {
				if ("cd-call clearfix phone-show".equals(statusStyle)) {
					return IautosStatusCode.STATUS_TYPE_FOR_SALE;
				} else if ("cd-call-exceed phone-show".equals(statusStyle)) {
					return IautosStatusCode.STATUS_TYPE_OVERDUE;
				} else if ("cd-call-sold phone-show".equals(statusStyle)) {
					return IautosStatusCode.STATUS_TYPE_SOLD;
				}
			}
		}
		return null;
	}

	/**
	 * if the information is during review
	 */
	public static boolean isDuringValidate(Element doc) {
		String bodyString = doc.select("body").text();
		return StringUtils.isBlank(bodyString);
	}

	public static String getPriceLiteral(Element detail) {
		Element priceFontTag = detail.select("span[class=price]").first();
		if (priceFontTag != null) {
			return priceFontTag.text().trim();
		}
		return "";
	}

	public static void fillParkAddress(Element detail, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {

		Element addressElement = detail.select("dl[class=clearfix]").get(2)
				.select("dd").first();

		if (null == addressElement) {
			LogException(responseWrapper, "ParkAddress");
		} else {
			String parkAddress = addressElement.text();
			if (StringUtils.isBlank(parkAddress)) {
				LogException(responseWrapper, "ParkAddress");
			} else {
				ici.setParkAddress(parkAddress);
			}
		}
	}

	public static void fillParkAddress_LicenseDate(Element detail,
			IautosCarInfo ici, ResponseWrapper responseWrapper) {

		Elements clearfixElements = detail.select("dl[class=clearfix]");

		if (clearfixElements.size() == 0) {
			LogException(responseWrapper, "ParkAddress");
			LogException(responseWrapper, "LicenseDate");
		}

		for (Element clearfix : clearfixElements) {
			String attr = clearfix.select("dt").text();
			String val = clearfix.select("dd").text();
			if ("看车地址".equals(attr)) {
				ici.setParkAddress(val.trim());

			} else if ("首次上牌".equals(attr)) {
				ici.setLicenseDate(val.trim());
			}
		}

		if (StringUtils.isBlank(ici.getParkAddress())) {
			LogException(responseWrapper, "ParkAddress");
		}
		if (StringUtils.isBlank(ici.getLicenseDate())) {
			LogException(responseWrapper, "LicenseDate");
		}
	}

	public static void fillLicenceDate(Element detail, IautosCarInfo ici,
			ResponseWrapper responseWrapper) {

		Element licenceDateElement = detail.select("dl[class=clearfix]").get(0)
				.select("dd").first();

		if (null == licenceDateElement) {
			LogException(responseWrapper, "licenceDate");
		} else {
			String licenceDate = licenceDateElement.text();
			if (StringUtils.isBlank(licenceDate)) {
				LogException(responseWrapper, "licenceDate");
			} else {
				ici.setLicenseDate(licenceDate);
			}
		}

	}

}
