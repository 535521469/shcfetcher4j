package pp.corleone.service.iautos.list;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Set;

import pp.corleone.domain.iautos.IautosConstant;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetchable;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;

public class IautosListFetcher extends Fetcher<Collection<String>> {

	public String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private Set<String> cityNames;
	private AbstractQueue<String> listUrls;

	public AbstractQueue<String> getListUrls() {
		return listUrls;
	}

	public void setListUrls(AbstractQueue<String> listUrls) {
		this.listUrls = listUrls;
	}

	public Set<String> getCityNames() {
		return cityNames;
	}

	public void setCityNames(Set<String> cityNames) {
		this.cityNames = cityNames;
	}

	public IautosListFetcher(String url, AbstractQueue<String> listUrls) {
		this.setUrl(url);
		this.setListUrls(listUrls);
	}

	@Override
	public void run() {

		Callback<Collection<RequestWrapper>> listCallback = new IautosListCallback();
		RequestWrapper reqw = new RequestWrapper(this.url);
		Fetchable<Collection<RequestWrapper>> cityFetchable = new IautosListFetchable(
				reqw, listCallback);

		try {
			Collection<RequestWrapper> urls = cityFetchable.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getLogger()
				.info("remain " + this.getListUrls().size()
						+ " detail page in queue");
	}

	@Override
	protected boolean validSource(RequestWrapper reqw) {
		return true;
	}

	@Override
	protected boolean shouldVisit(String url) {
		return true;
	}

}
