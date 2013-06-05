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

	public boolean isIgnore() {
		return true;
	}

	@Override
	public ResponseWrapper call() throws Exception {

		if (this.isIgnore()) {

			String url = this.getRequestWrapper().getUrl();
			ResponseWrapper rw = null;
			try {
				getLogger().debug("crawl " + url);
				Document doc = Jsoup.connect(url).get();
				return new ResponseWrapper(doc, this.requestWrapper);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return rw;
		}
		throw new InterruptedException();
	}
}
