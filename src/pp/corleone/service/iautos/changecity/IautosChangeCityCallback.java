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
import pp.corleone.domain.iautos.IautosConstant;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;
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

	@Override
	public Map<String, Collection<?>> call() throws Exception {

		Collection<Fetcher> fetchers = new ArrayList<Fetcher>();

		Document doc = this.getResponseWrapper().getDoc();

		Elements provinceATags = doc.select("div.province>a[href]");

		IautosListCallback listCallback = new IautosListCallback();

		for (Element provinceATag : provinceATags) {
			String provinceName = provinceATag.text()
					.replace("：".intern(), "".intern()).intern();
			if (this.getCities().contains(provinceName)) {
				String provinceUrl = IautosConstant.searchPage
						+ provinceATag.attr("href".intern());
				String provincePerUrl = IautosConstant
						.buildPersonalUrl(provinceUrl);

				RequestWrapper provincePer = new RequestWrapper(provincePerUrl,
						listCallback);

				provincePer.getMeta().put(IautosListCallback.CONTEXT_KEY_CITY,
						provinceName);

				fetchers.add(new IautosListFetcher(provincePer));// 个人降序
				this.getLogger().info(
						"personal " + provinceName + ":" + provincePerUrl);

				String provinceShopUrl = IautosConstant
						.buildShopUrl(provinceUrl);
				RequestWrapper provinceShop = new RequestWrapper(
						provinceShopUrl, listCallback);
				provinceShop.getMeta().put(IautosListCallback.CONTEXT_KEY_CITY,
						provinceName);
				fetchers.add(new IautosListFetcher(provinceShop));// 商户降序
				this.getLogger().info(
						"shop " + provinceName + ":" + provinceShopUrl);
			}
		}

		Elements cityATags = doc.select("div.city>div>p>a[href]".intern());
		for (Element cityATag : cityATags) {
			String cityName = cityATag.text()
					.replace("：".intern(), "".intern()).intern();
			if (this.getCities().contains(cityName)) {
				String cityUrl = IautosConstant.searchPage
						+ cityATag.attr("href".intern()).replace("city-", "");

				String cityPerUrl = IautosConstant.buildPersonalUrl(cityUrl);

				RequestWrapper cityPer = new RequestWrapper(cityPerUrl,
						listCallback);
				cityPer.getMeta().put(IautosListCallback.CONTEXT_KEY_CITY,
						cityName);

				fetchers.add(new IautosListFetcher(cityPer));// 个人降序
				this.getLogger()
						.info("personal " + cityName + ":" + cityPerUrl);

				String cityShopUrl = IautosConstant.buildShopUrl(cityUrl);

				RequestWrapper cityShop = new RequestWrapper(cityPerUrl,
						listCallback);
				cityShop.getMeta().put(IautosListCallback.CONTEXT_KEY_CITY,
						cityName);

				fetchers.add(new IautosListFetcher(cityShop));// 商户降序
				this.getLogger().info("shop " + cityName + ":" + cityShopUrl);
			}
		}

		Map<String, Collection<?>> resultMap = new HashMap<String, Collection<?>>();
		resultMap.put(FetcherConstants.Fetcher, fetchers);

		this.getLogger().info("total append " + fetchers.size() + " cities");
		return resultMap;
	}
}
