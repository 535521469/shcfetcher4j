package pp.corleone.service.iautos.changecity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pp.corleone.domain.iautos.FetcherConstants;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;
import pp.corleone.service.iautos.IautosConstant;
import pp.corleone.service.iautos.list.IautosListCallback;
import pp.corleone.service.iautos.list.IautosListFetcher;

public class IautosChangeCityCallback extends Callback {

	private Set<String> cities;

	public Set<String> getCities() {
		return cities;
	}

	public void setCities(Set<String> cities) {
		this.cities = cities;
	}

	public IautosChangeCityCallback(Set<String> cities) {
		this.setCities(cities);
	}

	private Fetcher buildFetcher(String locate, String url, int sellerType,
			Callback listCallback) {
		IautosCarInfo ici = new IautosCarInfo();
		ici.setSellerType(sellerType);
		ici.setLocate(locate);

		if (IautosCarInfo.SELLER_TYPE_PERSON == sellerType) {
			RequestWrapper per = new RequestWrapper(url, listCallback, this
					.getResponseWrapper().getReferRequestWrapper());
			per.getContext().put(IautosConstant.CAR_INFO, ici);
			return new IautosListFetcher(per);
		} else if (IautosCarInfo.SELLER_TYPE_SHOP == sellerType) {
			RequestWrapper shop = new RequestWrapper(url, listCallback, this
					.getResponseWrapper().getReferRequestWrapper());
			shop.getContext().put(IautosConstant.CAR_INFO, ici);
			return new IautosListFetcher(shop);
		}
		return null;
	}

	@Override
	public Map<String, Collection<?>> call() throws Exception {

		Collection<Fetcher> fetchers = new ArrayList<Fetcher>();

		Document doc = this.getResponseWrapper().getDoc();

		Elements provinceATags = doc.select("div.province>a[href]");

		IautosListCallback listCallback = new IautosListCallback();

		for (Element provinceATag : provinceATags) {
			// \uFF1A is colon

			String provinceName = provinceATag.text()
					.replace("\uFF1A".intern(), "".intern()).intern();
			if (this.getCities().contains(provinceName)) {

				if (this.getCities().contains(provinceName)) {
					// get province url
					String provinceUrl = IautosConstant.searchPage
							+ provinceATag.attr("href".intern());

					// get province personal url
					String provincePerUrl = IautosConstant
							.buildPersonalUrl(provinceUrl);

					Fetcher provincePer = buildFetcher(provinceName,
							provincePerUrl, IautosCarInfo.SELLER_TYPE_PERSON,
							listCallback);

					fetchers.add(provincePer);// person province
					this.getLogger().debug(
							"personal " + provinceName + ":" + provincePerUrl);

					// get province shop url
					String provinceShopUrl = IautosConstant
							.buildShopUrl(provinceUrl);

					Fetcher provinceShop = buildFetcher(provinceName,
							provinceShopUrl, IautosCarInfo.SELLER_TYPE_SHOP,
							listCallback);

					fetchers.add(provinceShop);// shop
												// province
					this.getLogger().debug(
							"shop " + provinceName + ":" + provinceShopUrl);
				}
			}
		}

		Elements cityATags = doc.select("div.city>div>p>a[href]".intern());
		for (Element cityATag : cityATags) {

			String cityName = cityATag.text()
					.replace("\uFF1A".intern(), "".intern()).intern();
			if (this.getCities().contains(cityName)) {
				// get city url
				String cityUrl = IautosConstant.searchPage
						+ cityATag.attr("href".intern()).replace("city-", "");
				// get city personal url
				String cityPerUrl = IautosConstant.buildPersonalUrl(cityUrl);

				Fetcher cityPer = this.buildFetcher(cityName, cityPerUrl,
						IautosCarInfo.SELLER_TYPE_PERSON, listCallback);
				fetchers.add(cityPer);// city person
				this.getLogger().debug(
						"personal " + cityName + ":" + cityPerUrl);

				String cityShopUrl = IautosConstant.buildShopUrl(cityUrl);
				
				Fetcher cityShop = this.buildFetcher(cityName, cityShopUrl,
						IautosCarInfo.SELLER_TYPE_SHOP, listCallback);
				fetchers.add(cityShop);// city shop
				this.getLogger().debug("shop " + cityName + ":" + cityShopUrl);
			}
		}

		Map<String, Collection<?>> resultMap = new HashMap<String, Collection<?>>();
		resultMap.put(FetcherConstants.Fetcher, fetchers);

		this.getLogger().info(
				"total fetcher " + fetchers.size() + " cities to crawl ");
		return resultMap;
	}
}
