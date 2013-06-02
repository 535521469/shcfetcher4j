package pp.corleone.service.iautos.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pp.corleone.domain.iautos.FetcherConstants;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosConstant;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;
import pp.corleone.service.iautos.detail.IautosDetailCallback;
import pp.corleone.service.iautos.detail.IautosDetailFetcher;

public class IautosListCallback extends Callback {

	public final static String CONTEXT_KEY_CITY = "CITY";

	public String getCityName() {
		return (String) getResponseWrapper().getReferRequestWrapper().getMeta()
				.get(CONTEXT_KEY_CITY);
	}

	public IautosListCallback() {
	}

	@Override
	public Map<String, Collection<?>> call() throws Exception {

		Map<String, Collection<?>> fetched = new HashMap<String, Collection<?>>();
		Collection<Fetcher> fetchers = new ArrayList<Fetcher>();
		fetched.put(FetcherConstants.Fetcher, fetchers);

		Document doc = this.getResponseWrapper().getDoc();

		Elements liCars = doc.select("div.carShow>ul>li");

		IautosDetailCallback detailCallback = new IautosDetailCallback();

		IautosCarInfo ici = (IautosCarInfo) this.getResponseWrapper()
				.getReferRequestWrapper().getContext()
				.get(IautosConstant.CAR_INFO);

		for (Element liCar : liCars) {
			Element aCar = liCar.select("h4>a").first();
			String detailUrl = aCar.attr("href");

			RequestWrapper requestWrapper = new RequestWrapper(detailUrl,
					detailCallback, this.getResponseWrapper()
							.getReferRequestWrapper());
			requestWrapper.getContext().put(IautosConstant.CAR_INFO,
					ici.clone());
			Fetcher fetcher = new IautosDetailFetcher(requestWrapper);
			fetchers.add(fetcher);
//			break;
		}

		Elements div_page = doc.select("div.page");

		Elements nextPageATags = div_page.select("a[target]");
		if (null != nextPageATags && nextPageATags.size() > 0) {
			Element nextPageATag = nextPageATags.get(0);
			String href = nextPageATag.attr("href");
			String nextUrl = IautosConstant.searchPage + href;
			this.getLogger().info("get next page " + nextUrl);
			RequestWrapper requestWrapper = new RequestWrapper(nextUrl, this,
					this.getResponseWrapper().getReferRequestWrapper());
			requestWrapper.getContext().put(IautosConstant.CAR_INFO, ici);
			Fetcher fetcher = new IautosListFetcher(requestWrapper);
			fetchers.add(fetcher);
		} else {
			this.getLogger().info(this.getCityName() + " rearch last page ");
		}

		return fetched;

	}
}
