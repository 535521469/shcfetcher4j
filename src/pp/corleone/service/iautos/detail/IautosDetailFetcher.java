package pp.corleone.service.iautos.detail;

import org.hibernate.Session;

import pp.corleone.dao.DaoUtil;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;

public class IautosDetailFetcher extends Fetcher {

	private Session getSession() {
		return DaoUtil.getCurrentSession();
	}

	public IautosDetailFetcher(RequestWrapper requestWrapper) {
		super(requestWrapper);
	}

	@Override
	public boolean isIgnore() {
		boolean ignore = true;

		this.getRequestWrapper().getContext().get("");

		return ignore;
	}

}
