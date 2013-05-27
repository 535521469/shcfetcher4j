package pp.corleone.service2.city;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pp.corleone.domain.iautos.IautosConstant;
import pp.corleone.service2.Callback;
import pp.corleone.service2.RequestWrapper;
import pp.corleone.service2.ResponseWrapper;
import pp.corleone.service2.ScheduleRequestWrapper;
import pp.corleone.service2.list.IautosListCallback;

public class IautosCityCallback<E, T extends Collection<E>> extends Callback
		implements Callable<T> {

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
	public T call() throws Exception {

		Collection<ScheduleRequestWrapper> urls = new ArrayList<ScheduleRequestWrapper>();

		ResponseWrapper resw = this.getResponseWrapper();

		Document doc = resw.getDoc();

		Elements provinceATags = doc.select("div.province>a[href]");
		for (Element provinceATag : provinceATags) {
			String provinceName = provinceATag.text()
					.replace("：".intern(), "".intern()).intern();
			if (this.getCities().contains(provinceName)) {
				String provinceUrl = IautosConstant.searchPage
						+ provinceATag.attr("href".intern());
				String perUrl = IautosConstant.buildPersonalUrl(provinceUrl);

				ScheduleRequestWrapper per = new ScheduleRequestWrapper(
						new RequestWrapper(perUrl), new IautosListCallback(
								provinceName));

				urls.add(per);// 个人降序
				this.getLogger().info(
						"append province personal " + provinceName + ":"
								+ perUrl);

				String shopUrl = IautosConstant.buildShopUrl(provinceUrl);
				urls.add(per);// 商户降序
				this.getLogger().info(
						"append province shop " + provinceName + ":" + shopUrl);
			}
		}

		// Elements cityATags = doc.select("div.city>div>p>a[href]".intern());
		// for (Element cityATag : cityATags) {
		// String cityName = cityATag.text()
		// .replace("：".intern(), "".intern()).intern();
		// if (this.getCities().contains(cityName)) {
		// String cityUrl = IautosConstant.searchPage
		// + cityATag.attr("href".intern()).replace("city-", "");
		// // urls.add(cityUrl + "pas1ds9vepcatcpbnscac/");// 商户降序
		// // urls.add(cityUrl + "pas2ds9vepcatcpbnscac/");// 个人降序
		// // this.getLogger()
		// // .info("append city " + cityName + ":" + cityUrl);
		//
		// String perUrl = IautosConstant.buildPersonalUrl(cityUrl);
		// urls.add(perUrl);// 个人降序
		// this.getLogger().info(
		// "append city personal " + cityName + ":" + perUrl);
		// String shopUrl = IautosConstant.buildShopUrl(cityUrl);
		// urls.add(shopUrl);// 商户降序
		// this.getLogger().info(
		// "append city shop " + cityName + ":" + shopUrl);
		// }
		// }

		this.getLogger().info("total append " + urls.size() + " cities");
		// return urls;
		return null;
	}
}
