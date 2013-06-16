package pp.corleone.service.iautos.seller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pp.corleone.dao.DaoUtil;
import pp.corleone.dao.iautos.IautosSellerInfoDao;
import pp.corleone.domain.iautos.FetcherConstants;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosSellerInfo;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.iautos.IautosConstant;

public class IautosSellerCallback extends Callback {

	private IautosSellerInfoDao sellerDao;

	public IautosSellerCallback() {
		Session session = this.getSession();
		this.setSellerDao(new IautosSellerInfoDao(session));
	}

	public IautosSellerInfoDao getSellerDao() {
		return sellerDao;
	}

	public void setSellerDao(IautosSellerInfoDao sellerDao) {
		this.sellerDao = sellerDao;
	}

	public Session getSession() {
		return DaoUtil.getCurrentSession();
	}

	private String relaceBlank(String text, String... strings) {
		// System.out.println(text);
		for (String string : strings) {
			// System.out.println(text + ":" + string+"before ");
			text = StringUtils.replace(text, string, "");
			// System.out.println(text + ":" + string + "after");
		}
		return text;
	}

	@Override
	public Map<String, Collection<?>> call() throws Exception {

		Map<String, Collection<?>> fetched = new HashMap<String, Collection<?>>();
		Collection<Fetcher> fetchers = new ArrayList<Fetcher>();
		fetched.put(FetcherConstants.Fetcher, fetchers);
		Document doc = this.getResponseWrapper().getDoc();

		IautosCarInfo ici = (IautosCarInfo) this.getResponseWrapper()
				.getContext().get(IautosConstant.CAR_INFO);

		Transaction tx = this.getSession().beginTransaction();

		this.getLogger().info("fetch " + this.getResponseWrapper().getUrl());

		try {
			IautosSellerInfo existIsi = null;
			if (null != ici) {
				existIsi = this.getSellerDao()
						.getBySeqID(ici.getIautosSellerInfo().getSeqID(),
								this.getSession());
			} else {
				IautosSellerInfo isi = (IautosSellerInfo) this
						.getResponseWrapper().getContext()
						.get(IautosConstant.SELLER_INFO);
				if (null != isi) {
					existIsi = this.getSellerDao().getBySeqID(isi.getSeqID(),
							this.getSession());
				}
			}

			if (null != existIsi) {
				this.fillSeller(doc, existIsi);
			} else {
				getLogger().error(" seller is null .");
			}

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}

		return fetched;

	}

	private void fillSeller(Element doc, IautosSellerInfo isi) {
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
			}
		}

		if (null == liTags || liTags.size() == 0) {
			Element ggulElement = doc.select("div.box_1").first();
			if (null != ggulElement) {
				liTags = ggulElement.select(">div>p");
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
						String shopName = this.relaceBlank(parentText, ":",
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
						String shopName = this.relaceBlank(parentText, ":",
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
						String shopName = this.relaceBlank(parentText, ":",
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
						String shopName = this.relaceBlank(parentText, ":",
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
						String shopAddress = this.relaceBlank(parentText, ":",
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
						String shopAddress = this.relaceBlank(parentText, ":",
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
						String shopAddress = this.relaceBlank(parentText, ":",
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
						String shopPhone = this.relaceBlank(parentText, ":",
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
						String shopPhone = this.relaceBlank(parentText, ":",
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
						String shopPhone = this.relaceBlank(parentText, ":",
								"\uFF1A", matcher.group(0)).trim();
						isi.setShopPhone(shopPhone);
						continue;
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

	}
}
