package pp.corleone.service2.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pp.corleone.service2.RequestWrapper;
import pp.corleone.service2.ResponseWrapper;
import pp.corleone.service2.Callback;
import pp.corleone.service2.ScheduleRequestWrapper;

//public class IautosListCallback extends Callback<Collection<ScheduleRequestWrapper>> {
public class IautosListCallback<E, T extends Collection<E>> extends Callback
		implements Callable<T> {

	public IautosListCallback(String cityName) {
		this.setCityName(cityName);
	}

	private String cityName;

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Override
	public T call() throws Exception {
		Collection reqws = new ArrayList();
		ResponseWrapper resw = this.getResponseWrapper();
		Document doc = resw.getDoc();

		Elements carLiTags = doc.select("div.carShow>ul>li");
		for (Element carLiTag : carLiTags) {
			Element aTag = carLiTag.select("h4>a").first();
			String detailUrl = aTag.attr("href").intern();
			getLogger().debug("detail url :" + detailUrl);
			reqws.add(new RequestWrapper(detailUrl));
		}
		getLogger().info("total get detail urls:" + reqws.size());
		// return reqws;
		return null;
	}
}
