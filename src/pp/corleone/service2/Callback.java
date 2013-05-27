package pp.corleone.service2;

import java.util.Collection;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Callback {
//public abstract class Callback<T extends Collection<?>> implements Callable<T> {

	private ResponseWrapper responseWrapper;

	public ResponseWrapper getResponseWrapper() {
		return responseWrapper;
	}

	public void setResponseWrapper(ResponseWrapper responseWrapper) {
		this.responseWrapper = responseWrapper;
	}

	protected final Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

}
