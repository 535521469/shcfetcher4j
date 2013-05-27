package pp.corleone.service.iautos.list;

import java.util.ArrayList;
import java.util.Collection;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pp.corleone.service.Callback;
import pp.corleone.service.RequestWrapper;
import pp.corleone.service.ResponseWrapper;

public class IautosListCallback extends Callback<Collection<RequestWrapper>> {

	private String cityName;

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Override
	public Collection<RequestWrapper> call() throws Exception {
		Collection<RequestWrapper> reqws = new ArrayList<RequestWrapper>();
		ResponseWrapper resw = this.getResw();
		Document doc = resw.getDoc();

		Elements carLiTags = doc.select("div.carShow>ul>li");
		for (Element carLiTag : carLiTags) {
			Element aTag = carLiTag.select("h4>a").first();
			String detailUrl = aTag.attr("href").intern();
			getLogger().debug("detail url :" + detailUrl);
			reqws.add(new RequestWrapper(detailUrl));
		}
		getLogger().info("total get detail urls:" + reqws.size());
		return reqws;
	}
}
