package pp.corleone.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestWrapper {

	private String url;
	private Callback callback;

	private Map<String, Object> meta = new HashMap<String, Object>();
	private Map<String, Object> context = new HashMap<String, Object>();

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RequestWrapper(String url) {
		this.setUrl(url);
	}

	public RequestWrapper(String url, Callback callback) {
		this.setCallback(callback);
		this.setUrl(url);
	}

	private List<RequestWrapper> referRequestWrappers;

	public List<RequestWrapper> getReferRequestWrappers() {
		return referRequestWrappers;
	}

	public void setReferRequestWrappers(
			List<RequestWrapper> referRequestWrappers) {
		this.referRequestWrappers = referRequestWrappers;
	}

}
