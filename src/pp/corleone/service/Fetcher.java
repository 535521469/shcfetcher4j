package pp.corleone.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Fetcher<T> implements Runnable {

	public Fetcher(Fetchable<T> fetcherable) {
		this.setFetcherable(fetcherable);
	}

	public Fetcher() {
	}

	protected final Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

	private Fetchable<T> fetcherable;

	public Fetchable<T> getFetcherable() {
		return fetcherable;
	}

	public void setFetcherable(Fetchable<T> fetcherable) {
		this.fetcherable = fetcherable;
	}

	protected abstract boolean validSource(RequestWrapper reqw);

	protected abstract boolean shouldVisit(String url);

}
