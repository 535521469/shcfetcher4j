package pp.corleone.service;

import java.util.concurrent.Callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Fetcher implements Callable<ResponseWrapper> {

	protected final Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

	private RequestWrapper requestWrapper;

	public Fetcher(RequestWrapper requestWrapper) {
		this.setRequestWrapper(requestWrapper);
	}

	public RequestWrapper getRequestWrapper() {
		return requestWrapper;
	}

	public void setRequestWrapper(RequestWrapper requestWrapper) {
		this.requestWrapper = requestWrapper;
	}

	@Override
	public ResponseWrapper call() throws Exception {
		String url = this.getRequestWrapper().getUrl();
		ResponseWrapper rw = null;

		try {

			// throw new SocketTimeoutException();

			Document doc = Jsoup.connect(url).get();
			return new ResponseWrapper(doc, this.requestWrapper);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rw;
	}

}
