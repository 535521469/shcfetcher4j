package pp.corleone.service.iautos.city;

import java.io.IOException;
import java.util.Collection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import pp.corleone.service.Callback;
import pp.corleone.service.Fetchable;
import pp.corleone.service.RequestWrapper;
import pp.corleone.service.ResponseWrapper;

public class IautosCityFetchable extends Fetchable<Collection<String>> {

	public IautosCityFetchable(RequestWrapper reqw,
			Callback<Collection<String>> callback) {
		super(reqw, callback);
	}

	@Override
	protected boolean isReady(ResponseWrapper resw) {
		Document doc = resw.getDoc();
		return doc.select("//div[class=province]").size() > 0;
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

	@Override
	public Collection<String> call() throws Exception {
		ResponseWrapper resw = this.fetch();
		this.getCallback().setResw(resw);
		Collection<String> urls = this.getCallback().call();
		return urls;
	}
}
