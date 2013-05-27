package pp.corleone.service.iautos.list;

import java.io.IOException;
import java.util.Collection;

import org.jsoup.Jsoup;

import pp.corleone.service.Callback;
import pp.corleone.service.Fetchable;
import pp.corleone.service.RequestWrapper;
import pp.corleone.service.ResponseWrapper;

public class IautosListFetchable extends Fetchable<Collection<RequestWrapper>> {

	public IautosListFetchable(RequestWrapper reqw,
			Callback<Collection<RequestWrapper>> callback) {
		super(reqw, callback);
	}

	@Override
	public Collection<RequestWrapper> call() throws Exception {
		ResponseWrapper resw = this.fetch();
		this.getCallback().setResw(resw);
		Collection<RequestWrapper> urls = this.getCallback().call();
		return urls;
	}

	@Override
	protected boolean isReady(ResponseWrapper resw) {
		return true;
	}

	@Override
	protected ResponseWrapper fetch() {
		try {
			return new ResponseWrapper(Jsoup.connect(this.getReqw().getUrl())
					.get(), this.getReqw());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
