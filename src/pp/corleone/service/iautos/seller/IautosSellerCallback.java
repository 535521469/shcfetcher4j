package pp.corleone.service.iautos.seller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

	private void fillShopName(Element doc, IautosSellerInfo isi) {

		if (null == isi.getShopName()) {
			Elements spanElements = doc.select("span");
			for (Element spanElement : spanElements) {
				String spanString = spanElement.text();
				spanString = StringUtils.replace(spanString, " ", "");
				// \u516C\u53F8\u540D\u79F0\uFF1A
				// gong si ming cheng
				if (spanString.indexOf("\u516C\u53F8\u540D\u79F0\uFF1A") != -1) {
					Element parentElement = spanElement.parent();
					String parentString = StringUtils.replace(
							parentElement.text(),
							"\u516C\u53F8\u540D\u79F0\uFF1A", "");
					parentString = StringUtils.replace(parentString, ":", "");
					parentString = StringUtils.replace(parentString, "\uFF1A",
							"");
					isi.setShopName(parentString.trim());
					break;
				}
				// \u5546\u6237\u540D\u79F0
				// shang hu ming cheng
				if (spanString.indexOf("\u5546\u6237\u540D\u79F0") != -1) {
					Element parentElement = spanElement.parent();
					String parentString = StringUtils.replace(
							parentElement.text(), "\u5546\u6237\u540D\u79F0",
							"");
					parentString = StringUtils.replace(parentString, ":", "");
					parentString = StringUtils.replace(parentString, "\uFF1A",
							"");
					isi.setShopName(parentString.trim());
					break;
				}

			}
		}

		if (null == isi.getShopName()) {
			Element shopShowElement = doc.select("div.shop_show").first();
			if (null != shopShowElement) {
				Elements pElements = shopShowElement.select("p");
				for (Element pElement : pElements) {
					Element spanElement = pElement.select("span").first();
					if (null != spanElement) {
						String spanTextString = spanElement.text();
						if ("\u516C\u53F8\u5168\u79F0\uFF1A"
								.equals(spanTextString)) {
							// \u516C\u53F8\u5168\u79F0\uFF1A
							// gong si quan cheng
							String shopName = StringUtils.replace(
									pElement.text(),
									"\u516C\u53F8\u5168\u79F0\uFF1A", "");
							isi.setShopName(shopName);
							break;
						}
					}
				}
			}
		}

		if (null == isi.getShopName()) {
			Element shxxElement = doc.select("div.shxx").first();
			if (null != shxxElement) {
				Elements liElements = shxxElement.select("ul>li");
				if (liElements.size() == 0) {
					liElements = shxxElement.select("div.ggul>div.ggli");
				}
				for (Element liElement : liElements) {
					String liTextString = liElement.text();
					if (liTextString.trim().indexOf(
							"\u516C\u53F8\u540D\u79F0\uFF1A") != -1) {
						// \u516C\u53F8\u540D\u79F0\uFF1A
						// gong si ming cheng
						Element shopName = liElement.select("span").first();
						isi.setShopName(shopName.text().trim());
						break;
					}
				}
			}
		}

		if (null == isi.getShopName()) {
			Element shopInfoElement = doc.select("div.shopInfo").first();
			if (null != shopInfoElement) {
				Elements dtElements = shopInfoElement.select("dt>div.dt");
				for (Element dtElement : dtElements) {
					String dtString = dtElement.text();
					if (dtString.trim().indexOf("\u5546\u6237\u540D\u79F0") != -1) {
						// \u5546\u6237\u540D\u79F0
						// shang hu ming cheng
						Element ddElement = dtElement.nextElementSibling();
						isi.setShopName(ddElement.text().trim());
						break;
					}
				}
			}
		}

		if (null == isi.getShopName()) {
			throw new IllegalArgumentException("Shop Name");
		}
	}

	private void fillShopPhone(Element doc, IautosSellerInfo isi) {

		if (null == isi.getShopPhone()) {
			Elements spanElements = doc.select("span");
			for (Element spanElement : spanElements) {
				String spanString = spanElement.text();
				spanString = StringUtils.replace(spanString, " ", "");
				// \u8054\u7CFB\u7535\u8BDD
				// lian xi dian hua
				if (spanString.indexOf("\u8054\u7CFB\u7535\u8BDD") != -1) {
					Element parentElement = spanElement.parent();
					String parentString = StringUtils.replace(
							parentElement.text(), "\u8054\u7CFB\u7535\u8BDD",
							"");
					parentString = StringUtils.replace(parentString, ":", "");
					parentString = StringUtils.replace(parentString, "\uFF1A",
							"");
					isi.setShopPhone(parentString.trim());
					break;
				}

				// \u7535\u8BDD
				// dian hua
				if (spanString.indexOf("\u7535\u8BDD") != -1) {
					Element parentElement = spanElement.parent();
					if (spanString.indexOf("400-666-1612") != -1) {
						continue;
					}
					String parentString = StringUtils.replace(
							parentElement.text(), "\u7535\u8BDD", "");
					parentString = StringUtils.replace(parentString, ":", "");
					parentString = StringUtils.replace(parentString, "\uFF1A",
							"");
					isi.setShopPhone(parentString.trim());
					break;
				}
			}
		}

		if (null == isi.getShopPhone()) {
			Element shopShowElement = doc.select("div.shop_show").first();
			if (null != shopShowElement) {
				Elements pElements = shopShowElement.select("p");
				for (Element pElement : pElements) {
					Element spanElement = pElement.select("span").first();
					if (null != spanElement) {
						String spanTextString = spanElement.text();
						if ("\u8054\u7CFB\u7535\u8BDD\uFF1A"
								.equals(spanTextString)) {
							// \u8054\u7CFB\u7535\u8BDD\uFF1A
							// lian xi dian hua
							String shopPhone = StringUtils.replace(
									pElement.text(),
									"\u8054\u7CFB\u7535\u8BDD\uFF1A", "");
							isi.setShopPhone(shopPhone);
							break;
						}
					}
				}
			}
		}

		if (null == isi.getShopPhone()) {
			Element shxxElement = doc.select("div.shxx").first();
			if (null != shxxElement) {
				Elements liElements = shxxElement.select("ul>li");
				for (Element liElement : liElements) {
					String liTextString = liElement.text();
					if (liTextString.trim().indexOf(
							"\u8054\u7CFB\u7535\u8BDD\uFF1A") != -1) {
						// \u8054\u7CFB\u7535\u8BDD\uFF1A
						// lian xi dian hua
						Element shopPhone = liElement.select("span").first();
						isi.setShopPhone(shopPhone.text().trim());
						break;
					}
				}
			}
		}

		if (null == isi.getShopPhone()) {
			Element shopInfoElement = doc.select("div.shopInfo").first();
			if (null != shopInfoElement) {
				Elements dtElements = shopInfoElement.select("dt>div.dt");
				for (Element dtElement : dtElements) {
					String dtString = dtElement.text();
					if (dtString.trim().indexOf("\u7535\u8BDD") != -1) {
						// \u7535\u8BDD : dian hua
						Element ddElement = dtElement.nextElementSibling();
						isi.setShopPhone(ddElement.text().trim());
						break;
					}
				}
			}
		}

		if (null == isi.getShopPhone()) {
			throw new IllegalArgumentException("Shop Phone");
		}
	}

	private void fillShopAddress(Element doc, IautosSellerInfo isi) {

		if (null == isi.getShopAddress()) {
			Elements spanElements = doc.select("span");
			for (Element spanElement : spanElements) {
				String spanString = spanElement.text();
				spanString = StringUtils.replace(spanString, " ", "");
				// \u516C\u53F8\u5730\u5740
				// gong si di zhi
				if (spanString.indexOf("\u516C\u53F8\u5730\u5740") != -1) {
					Element parentElement = spanElement.parent();
					String parentString = StringUtils.replace(
							parentElement.text(), "\u516C\u53F8\u5730\u5740",
							"");
					parentString = StringUtils.replace(parentString, ":", "");
					parentString = StringUtils.replace(parentString, "\uFF1A",
							"");
					isi.setShopAddress(parentString.trim());
					break;
				}

				// \u6240\u5728\u5730\u5740
				// suo zai di zhi
				if (spanString.indexOf("\u6240\u5728\u5730\u5740") != -1) {
					Element parentElement = spanElement.parent();
					String parentString = StringUtils.replace(
							parentElement.text(), "\u6240\u5728\u5730\u5740",
							"");
					parentString = StringUtils.replace(parentString, ":", "");
					parentString = StringUtils.replace(parentString, "\uFF1A",
							"");
					isi.setShopAddress(parentString.trim());
					break;
				}
			}
		}

		if (null == isi.getShopAddress()) {
			Element shopShowElement = doc.select("div.shop_show").first();
			if (null != shopShowElement) {
				Elements pElements = shopShowElement.select("p");
				for (Element pElement : pElements) {
					Element spanElement = pElement.select("span").first();
					if (null != spanElement) {
						String spanTextString = spanElement.text();
						if ("\u516C\u53F8\u5730\u5740\uFF1A"
								.equals(spanTextString)) {
							// \u516C\u53F8\u5730\u5740\uFF1A
							// gong si di zhi
							String shopAddress = StringUtils.replace(
									pElement.text(),
									"\u516C\u53F8\u5730\u5740\uFF1A", "");
							isi.setShopAddress(shopAddress);
							break;
						}
					}
				}
			}
		}

		if (null == isi.getShopAddress()) {
			Element shxxElement = doc.select("div.shxx").first();
			if (null != shxxElement) {
				Elements liElements = shxxElement.select("ul>li");
				for (Element liElement : liElements) {
					String liTextString = liElement.text();
					if (liTextString.trim().indexOf(
							"\u516C\u53F8\u5730\u5740\uFF1A") != -1) {
						// \u516C\u53F8\u5730\u5740\uFF1A
						// gong si di zhi
						Element shopAddress = liElement.select("span").first();
						isi.setShopAddress(shopAddress.text().trim());
						break;
					}
				}
			}
		}
		if (null == isi.getShopAddress()) {
			Element shopInfoElement = doc.select("div.shopInfo").first();
			if (null != shopInfoElement) {
				Elements dtElements = shopInfoElement.select("dt>div.dt");
				for (Element dtElement : dtElements) {
					String dtString = dtElement.text();
					if (dtString.trim().indexOf("\u6240\u5728\u5730\u5740") != -1) {
						// \u6240\u5728\u5730\u5740 : suo zai di zhi
						Element ddElement = dtElement.nextElementSibling();
						isi.setShopAddress(ddElement.text().trim());
						break;
					}
				}
			}
		}

		if (null == isi.getShopAddress()) {
			throw new IllegalArgumentException("Shop Address");
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

		Transaction tx = this.getSession().beginTransaction();

		System.out.println(this.getResponseWrapper().getUrl());

		try {
			IautosSellerInfo existIsi = this.getSellerDao().getBySeqID(
					ici.getIautosSellerInfo().getSeqID(), this.getSession());

			this.fillShopName(doc, existIsi);
			this.fillShopPhone(doc, existIsi);
			this.fillShopAddress(doc, existIsi);

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}

		return fetched;

	}

}
