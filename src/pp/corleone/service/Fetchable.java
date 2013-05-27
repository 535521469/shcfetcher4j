package pp.corleone.service;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Fetchable<V> implements Callable<V> {
	protected final Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}
	private Callback<V> callback;

	private RequestWrapper reqw;

	public RequestWrapper getReqw() {
		return reqw;
	}

	public void setReqw(RequestWrapper reqw) {
		this.reqw = reqw;
	}

	public Callback<V> getCallback() {
		return callback;
	}

	public void setCallback(Callback<V> callback) {
		this.callback = callback;
	}

	public Fetchable(RequestWrapper reqw, Callback<V> callback) {
		this.setReqw(reqw);
		this.setCallback(callback);
	}

	protected abstract boolean isReady(ResponseWrapper resw);

	protected abstract ResponseWrapper fetch();

}