package pp.corleone.service.iautos.seller;

import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;

public class IautosSellerFetcher extends Fetcher {

	@Override
	public boolean isIgnore() throws InterruptedException {
		return this.getRequestWrapper().getUrl().indexOf("iautos") == -1;
	}

	public IautosSellerFetcher(RequestWrapper requestWrapper) {
		super(requestWrapper);
	}

}
