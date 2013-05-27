package pp.corleone.service.iautos.city;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pp.corleone.domain.iautos.IautosConstant;
import pp.corleone.service.Callback;
import pp.corleone.service.ResponseWrapper;

public class IautosCityCallback extends Callback<Collection<String>> {

	public IautosCityCallback(Set<String> cities) {
		this.setCities(cities);
	}

	private Set<String> cities;

	public Set<String> getCities() {
		return cities;
	}

	private void setCities(Set<String> cities) {
		this.cities = cities;
	}

	@Override
	public Collection<String> call() throws Exception {

		Collection<String> urls = new ArrayList<String>();

		ResponseWrapper resw = this.getResw();

		Document doc = resw.getDoc();

		Elements provinceATags = doc.select("div.province>a[href]");
		for (Element provinceATag : provinceATags) {
			String provinceName = provinceATag.text()
					.replace("：".intern(), "".intern()).intern();
			if (this.getCities().contains(provinceName)) {
				String provinceUrl = IautosConstant.searchPage
						+ provinceATag.attr("href".intern());
				String perUrl = IautosConstant.buildPersonalUrl(provinceUrl);
				urls.add(perUrl);// 个人降序
				this.getLogger().info(
						"append province personal " + provinceName + ":"
								+ perUrl);
				String shopUrl = IautosConstant.buildShopUrl(provinceUrl);
				urls.add(shopUrl);// 商户降序
				this.getLogger()
						.info("append province shop " + provinceName + ":"
								+ shopUrl);
			}
		}

		Elements cityATags = doc.select("div.city>div>p>a[href]".intern());
		for (Element cityATag : cityATags) {
			String cityName = cityATag.text()
					.replace("：".intern(), "".intern()).intern();
			if (this.getCities().contains(cityName)) {
				String cityUrl = IautosConstant.searchPage
						+ cityATag.attr("href".intern()).replace("city-", "");
				// urls.add(cityUrl + "pas1ds9vepcatcpbnscac/");// 商户降序
				// urls.add(cityUrl + "pas2ds9vepcatcpbnscac/");// 个人降序
				// this.getLogger()
				// .info("append city " + cityName + ":" + cityUrl);

				String perUrl = IautosConstant.buildPersonalUrl(cityUrl);
				urls.add(perUrl);// 个人降序
				this.getLogger().info(
						"append city personal " + cityName + ":" + perUrl);
				String shopUrl = IautosConstant.buildShopUrl(cityUrl);
				urls.add(shopUrl);// 商户降序
				this.getLogger().info(
						"append city shop " + cityName + ":" + shopUrl);

			}
		}

		this.getLogger().info("total append " + urls.size() + " cities");
		return urls;
	}
}