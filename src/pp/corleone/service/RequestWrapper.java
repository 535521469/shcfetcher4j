package pp.corleone.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestWrapper {

	private Callback callback;

	private String url;
	private Map<String, Object> meta = new HashMap<String, Object>();
	private Map<String, Object> context = new HashMap<String, Object>();

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public Map<String, Object> getMeta() {
		return meta;
	}

	public void setMeta(Map<String, Object> meta) {
		this.meta = meta;
	}

	public Callback getCallback() {
		return callback;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public RequestWrapper(String url) {
		this.setUrl(url);
	}

	public RequestWrapper(String url, Callback callback) {
		this(url);
		this.setCallback(callback);
	}

	public RequestWrapper(String url, Callback callback,
			RequestWrapper referRequestWarpper) {
		this(url, callback);
		this.getReferRequestWrappers().add(referRequestWarpper);
	}

	private List<RequestWrapper> referRequestWrappers = new ArrayList<RequestWrapper>();

	public List<RequestWrapper> getReferRequestWrappers() {
		return referRequestWrappers;
	}

	public void setReferRequestWrappers(
			List<RequestWrapper> referRequestWrappers) {
		this.referRequestWrappers = referRequestWrappers;
	}

}
