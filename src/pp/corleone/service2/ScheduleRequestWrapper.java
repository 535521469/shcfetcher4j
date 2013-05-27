package pp.corleone.service2;

import java.util.Collection;
import java.util.concurrent.Callable;

public class ScheduleRequestWrapper implements Callable<Collection<?>> {

	private RequestWrapper requestWrapper;
	private Callable<Collection<?>> callback;

	public RequestWrapper getRequestWrapper() {
		return requestWrapper;
	}

	public void setRequestWrapper(RequestWrapper requestWrapper) {
		this.requestWrapper = requestWrapper;
	}

	public Callable<Collection<?>> getCallback() {
		return callback;
	}

	public void setCallback(Callable<Collection<?>> callback) {
		this.callback = callback;
	}

	public ScheduleRequestWrapper(RequestWrapper requestWrapper,
			Callable<Collection<?>> callback) {
		this.setRequestWrapper(requestWrapper);
		this.setCallback(callback);
	}

	@Override
	public Collection<?> call() throws Exception {
		return this.callback.call();
	}

}
