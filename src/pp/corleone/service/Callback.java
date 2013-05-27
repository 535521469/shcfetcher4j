package pp.corleone.service;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Callback<V> implements Callable<V> {
	protected final Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}
	private ResponseWrapper resw;

	public ResponseWrapper getResw() {
		return resw;
	}

	public void setResw(ResponseWrapper resw) {
		this.resw = resw;
	}

	public Callback() {
	}
	
	
}