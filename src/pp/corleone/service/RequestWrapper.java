package pp.corleone.service;

import java.util.List;

public class RequestWrapper {

	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RequestWrapper(String url) {
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
