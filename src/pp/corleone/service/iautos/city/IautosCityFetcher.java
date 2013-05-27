package pp.corleone.service.iautos.city;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Set;

import pp.corleone.domain.iautos.IautosConstant;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetchable;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;

public class IautosCityFetcher extends Fetcher<Collection<String>> {

	public static final String url = (IautosConstant.homePage + "city/"
			.intern()).intern();

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

	public IautosCityFetcher(Set<String> cities, AbstractQueue<String> listUrls) {
		this.setCityNames(cities);
		this.setListUrls(listUrls);
	}

	@Override
	public void run() {

		Callback<Collection<String>> cityCallback = new IautosCityCallback(
				this.getCityNames());

		RequestWrapper reqw = new RequestWrapper(this.url);

		Fetchable<Collection<String>> cityFetchable = new IautosCityFetchable(
				reqw, cityCallback);

		try {
			Collection<String> urls = cityFetchable.call();
			this.getListUrls().addAll(urls);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.getLogger().info(
				"remain " + this.getListUrls().size() + " list page in queue");

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
