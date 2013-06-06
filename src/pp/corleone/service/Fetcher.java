package pp.corleone.service;

import java.io.IOException;
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

	public boolean isIgnore() throws InterruptedException {
		return false;
	}

	@Override
	public ResponseWrapper call() throws InterruptedException {

		ResponseWrapper rw = null;
		if (!this.isIgnore()) {

			String url = this.getRequestWrapper().getUrl();
			getLogger().debug("crawl " + url);
			Document doc;
			try {
				doc = Jsoup.connect(url).get();
				rw = new ResponseWrapper(doc, this.requestWrapper);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return rw;
	}
}
