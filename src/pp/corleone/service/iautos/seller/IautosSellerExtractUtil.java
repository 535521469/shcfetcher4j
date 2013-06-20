package pp.corleone.service.iautos.seller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pp.corleone.domain.iautos.IautosSellerInfo;

public class IautosSellerExtractUtil {

	private static String replaceBlank(String text, String... strings) {
		// System.out.println(text);
		for (String string : strings) {
			// System.out.println(text + ":" + string+"before ");
			text = StringUtils.replace(text, string, "");
			// System.out.println(text + ":" + string + "after");
		}
		return text;
	}

	public static void fillSeller(Element doc, IautosSellerInfo isi) {
		Elements liTags = null;

		if (null == liTags || liTags.size() == 0) {
			Element shxxDiv = doc.select("div.shxx").first();
			if (null != shxxDiv) {
				liTags = shxxDiv.select(">ul>li");
			}
		}

		if (null == liTags || liTags.size() == 0) {
			Element shxxDiv = doc.select("div.shxxCon").first();
			if (null != shxxDiv) {
				liTags = shxxDiv.select(">ul>li");
			}
		}

		if (null == liTags || liTags.size() == 0) {
			Element ggulElement = doc.select("div.ggul").first();
			if (null != ggulElement) {
				liTags = ggulElement.select("div.ggli");
			}
		}

		if (null == liTags || liTags.size() == 0) {
			Element ggulElement = doc.select("div.content").first();
			if (null != ggulElement) {
				liTags = ggulElement.select(">p");
			}
		}

		if (null == liTags || liTags.size() == 0) {
			Element ggulElement = doc.select("div.box_11").first();
			if (null != ggulElement) {
				liTags = ggulElement.select(">div>p");
				liTags.addAll(ggulElement.select(">div>div"));
			}
		}

		if (null == liTags || liTags.size() == 0) {
			Elements ggulElements = doc.select("div.box_1");
			Element ggulElement = ggulElements.first();
			if (null != ggulElement) {
				liTags = ggulElement.select(">div>p");
				liTags.addAll(ggulElement.select(">div>div"));
			}

		}

		if (null == liTags || liTags.size() == 0) {
			Elements ggulElements = doc.select("div.box");
			Element ggulElement = ggulElements.first();
			if (null != ggulElement) {
				liTags = ggulElement.select(">div>div>p");
			}

			if (liTags == null) {
				if (ggulElements.size() > 4) {
					ggulElement = ggulElements.get(4);
					if (null != ggulElement) {
						liTags = ggulElement.select(">div>div>p");
					}
				}
			}

		}

		if (null == liTags || liTags.size() == 0) {
			Element ggulElement = doc.select("div.shop_show").first();
			if (null != ggulElement) {
				liTags = ggulElement.select(">p");
			}
		}

		if (null == liTags || liTags.size() == 0) {
			Element ggulElement = doc.select("div.shzs").first();
			if (null != ggulElement) {
				liTags = ggulElement.select(">ul>li");
			}
		}

		if (null == liTags || liTags.size() == 0) {
			Element ggulElement = doc.select("table.info").first();
			if (null != ggulElement) {
				liTags = ggulElement.select(">tr>th");
			}
		}

		if (null == liTags || liTags.size() == 0) {
			Element ggulElement = doc.select("div.gsjjRight").first();
			if (null != ggulElement) {
				liTags = ggulElement.select(">p");
			}
		}

		if (null != liTags && liTags.size() > 0) {

			for (Element liTag : liTags) {
				String parentText = liTag.text();
				Element spanTag = liTag.select("span").first();

				if (null != spanTag) {

					String spanString = spanTag.text();
					// \u5546\u6237\u540D\u79F0
					// shang hu ming cheng
					String pattern = "\u5546\u6237\u540D\u79F0";
					if (spanString.indexOf(pattern) != -1) {
						String shopName = replaceBlank(parentText, ":",
								"\uFF1A", pattern).trim();
						isi.setShopName(shopName);
						continue;
					}

					if (parentText.indexOf(pattern) != -1) {
						isi.setShopName(spanString);
						continue;
					}

					// \u5546\u6237\u540D
					// shang hu ming
					pattern = "\u5546\u6237\u540D";
					if (spanString.indexOf(pattern) != -1) {
						String shopName = replaceBlank(parentText, ":",
								"\uFF1A", pattern).trim();
						isi.setShopName(shopName);
						continue;
					}

					if (parentText.indexOf(pattern) != -1) {
						isi.setShopName(spanString);
						continue;
					}

					// \u516C\u53F8\u5168\u79F0
					// gong si quan cheng
					pattern = "\u516C\u53F8\u5168\u79F0";
					if (spanString.indexOf(pattern) != -1) {
						String shopName = replaceBlank(parentText, ":",
								"\uFF1A", pattern).trim();
						isi.setShopName(shopName);
						continue;
					}

					if (parentText.indexOf(pattern) != -1) {
						isi.setShopName(spanString);
						continue;
					}

					// \u516C\u53F8\u540D\u79F0
					// gong si ming cheng
					pattern = "\u516C\u53F8\u540D\u79F0";
					if (spanString.indexOf(pattern) != -1) {
						String shopName = replaceBlank(parentText, ":",
								"\uFF1A", pattern).trim();
						isi.setShopName(shopName);
						continue;
					}

					if (parentText.indexOf(pattern) != -1) {
						isi.setShopName(spanString);
						continue;
					}

					// \u516C\u53F8\u5730\u5740
					// gong si di zhi
					pattern = "\u516C\u53F8\u5730\u5740";
					if (spanString.indexOf(pattern) != -1) {
						String shopAddress = replaceBlank(parentText, ":",
								"\uFF1A", pattern).trim();
						isi.setShopAddress(shopAddress);
						continue;
					}

					if (parentText.indexOf(pattern) != -1) {
						isi.setShopAddress(spanString);
						continue;
					}

					// \u5730\u3000\u5740
					// di \t zhi
					pattern = "\u5730\u3000\u5740";
					if (spanString.indexOf(pattern) != -1) {
						String shopAddress = replaceBlank(parentText, ":",
								"\uFF1A", pattern).trim();
						isi.setShopAddress(shopAddress);
						continue;
					}

					if (parentText.indexOf(pattern) != -1) {
						isi.setShopAddress(spanString);
						continue;
					}

					// \u6240\u5728\u5730\u5740
					// suo zai di zhi
					pattern = "\u6240\u5728\u5730\u5740";
					if (spanString.indexOf(pattern) != -1) {
						String shopAddress = replaceBlank(parentText, ":",
								"\uFF1A", pattern).trim();
						isi.setShopAddress(shopAddress);
						continue;
					}

					if (parentText.indexOf(pattern) != -1) {
						isi.setShopAddress(spanString);
						continue;
					}

					// \u7535 \u8BDD
					// dian hua
					pattern = "\u7535    \u8BDD";
					if (spanString.indexOf(pattern) != -1) {
						String shopPhone = replaceBlank(parentText, ":",
								"\uFF1A", pattern).trim();
						isi.setShopPhone(shopPhone);
						continue;
					}

					if (parentText.indexOf(pattern) != -1) {
						isi.setShopPhone(spanString);
						continue;
					}

					if (parentText.indexOf(pattern) != -1) {
						if (null == isi.getShopPhone()) {
							String phoneString = liTag.attr("title");
							if (!StringUtils.isBlank(phoneString)) {
								isi.setShopPhone(phoneString);
								continue;
							}
						}
					}

					// \u516C\u53F8\u7535\u8BDD
					// dian hua
					pattern = "\u516C\u53F8\u7535\u8BDD";
					if (spanString.indexOf(pattern) != -1) {
						String shopPhone = replaceBlank(parentText, ":",
								"\uFF1A", pattern).trim();
						isi.setShopPhone(shopPhone);
						continue;
					}

					if (parentText.indexOf(pattern) != -1) {
						isi.setShopPhone(spanString);
						continue;
					}

					// \u8054\u7CFB\u7535\u8BDD
					// lian xi dian hua
					pattern = "\u8054\u7CFB\u7535\u8BDD";
					if (spanString.indexOf(pattern) != -1) {
						String shopPhone = replaceBlank(parentText, ":",
								"\uFF1A", pattern).trim();
						isi.setShopPhone(shopPhone);
						continue;
					}

					if (parentText.indexOf(pattern) != -1) {
						isi.setShopPhone(spanString);
						continue;
					}

					Pattern patternReg = Pattern.compile("\u7535.+\u8BDD");
					Matcher matcher = patternReg.matcher(spanString);
					// \u7535\u8BDD
					// dian hua
					if (matcher.find()) {
						String shopPhone = replaceBlank(parentText, ":",
								"\uFF1A", matcher.group(0)).trim();
						isi.setShopPhone(shopPhone);
						continue;
					}

					// \u7535 \u8BDD
					// dian hua
					// pattern = "\u7535    \u8BDD";
					// if (spanString.indexOf(pattern) != -1) {

					pattern = "\u7535\u8BDD";

					if (spanString.length() > 1) {

						String dianhua = spanString.substring(0, 1)
								+ spanString.substring(spanString.length() - 2,
										spanString.length() - 1);

						if (pattern.equals(dianhua)) {
							String shopPhone = replaceBlank(parentText, ":",
									"\uFF1A", pattern).trim();
							isi.setShopPhone(shopPhone);
							continue;
						}
						if (parentText.indexOf(pattern) != -1) {
							isi.setShopPhone(spanString);
							continue;
						}
					}

				}
			}
		}

		Element focusElement = doc.select("#focus").first();
		if (null != focusElement) {
			Elements divDTElements = focusElement.select("div.dt");
			for (Element divDTElement : divDTElements) {
				String divHeaderString = divDTElement.text().trim();
				Element contentElement = divDTElement.nextElementSibling();
				String content = contentElement.text().trim();

				// \u5546\u6237\u540D\u79F0
				// shang hu ming cheng
				String pattern = "\u5546\u6237\u540D\u79F0";
				if (divHeaderString.indexOf(pattern) != -1) {
					isi.setShopName(content);
					continue;
				}

				// \u6240\u5728\u5730\u5740
				// suo zai di zhi
				pattern = "\u6240\u5728\u5730\u5740";
				if (divHeaderString.indexOf(pattern) != -1) {
					isi.setShopAddress(content);
					continue;
				}

				// \u7535\u8BDD
				// dian hua
				pattern = "\u7535\u8BDD";
				if (divHeaderString.indexOf(pattern) != -1) {
					isi.setShopPhone(content);
					continue;
				}

			}
		}

		Element ulElement = doc.select("ul.left-3").first();
		if (null != ulElement) {
			Elements liElements = ulElement.select("li");
			for (Element liElement : liElements) {
				String divHeaderString = liElement.text().trim();
				Element contentElement = liElement.nextElementSibling();
				if (null == contentElement) {
					continue;
				}
				String content = contentElement.text().trim();
				String pattern = "";

				// \u5546\u6237\u540D
				// shang hu ming
				pattern = "\u5546\u6237\u540D";
				if (divHeaderString.indexOf(pattern) != -1) {
					isi.setShopName(content);
					continue;
				}

				// \u5730\u3000\u5740
				// di \t zhi
				pattern = "\u5730\u3000\u5740";
				if (divHeaderString.indexOf(pattern) != -1) {
					isi.setShopAddress(content);
					continue;
				}

				// \u7535\u3000\u8BDD
				// dian \t hua
				pattern = "\u7535\u3000\u8BDD";
				if (divHeaderString.indexOf(pattern) != -1) {
					isi.setShopPhone(content);
					continue;
				}

			}
		}

		if (null == isi.getShopPhone()) {
			Element ggulElement = doc.select("div.box_1").first();
			if (null != ggulElement) {
				liTags = ggulElement.select(">div>div");
			}

			if (liTags != null) {
				for (Element liTag : liTags) {
					Elements divElements = liTag.select(">div");

					if (!divElements.isEmpty()) {
						Element divTag = divElements.first();
						String divString = divTag.text();
						// \u7535 \u8BDD
						// dian hua
						String pattern = "\u7535\u8BDD";
						String dianhua = divString.substring(0, 1)
								+ divString.substring(divString.length() - 2,
										divString.length() - 1);

						if (pattern.equals(dianhua)) {

							if (divElements.size() > 1) {
								Element shopPhoneElement = divElements.get(1);
								String shopPhone = shopPhoneElement.text()
										.trim();
								isi.setShopPhone(shopPhone);
								continue;
							}
						}
					}
				}
			}
		}

		if (null == isi.getShopAddress()) {
			Element ggulElement = doc.select("div.box_1").first();
			if (null != ggulElement) {
				liTags = ggulElement.select(">div>div");
			}

			if (liTags == null) {
				ggulElement = doc.select("div.box_11").first();
			}

			if (null != ggulElement) {
				liTags = ggulElement.select(">div>div");
			}

			if (liTags != null) {
				for (Element liTag : liTags) {
					Elements divElements = liTag.select(">div");

					if (!divElements.isEmpty()) {
						Element divTag = divElements.first();
						String divString = divTag.text();
						// \u6240\u5728\u5730\u5740
						// suo zai di zhi
						String pattern = "\u6240\u5728\u5730\u5740";
						if (divString.indexOf(pattern) != -1) {

							String parentString = liTag.text();

							String shopAddress = parentString.replace(
									divString, "").trim();
							isi.setShopAddress(shopAddress);
							continue;
						}
					}
				}
			}
		}
	}

}
