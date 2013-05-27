package pp.corleone.service;

import org.jsoup.nodes.Document;

public class ResponseWrapper {

	private Document doc;
	private RequestWrapper referRequestWrapper;

	public RequestWrapper getReferRequestWrapper() {
		return referRequestWrapper;
	}

	public void setReferRequestWrapper(RequestWrapper referRequestWrapper) {
		this.referRequestWrapper = referRequestWrapper;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public ResponseWrapper() {
	}

	public ResponseWrapper(Document doc, RequestWrapper referRequestWrapper) {
		this.setDoc(doc);
		this.setReferRequestWrapper(referRequestWrapper);
	}
}
