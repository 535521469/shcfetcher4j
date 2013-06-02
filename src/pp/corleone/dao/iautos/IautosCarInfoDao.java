package pp.corleone.dao.iautos;

import org.hibernate.Query;
import org.hibernate.Session;

import pp.corleone.domain.iautos.IautosCarInfo;

public class IautosCarInfoDao {

	private Session session;

	public IautosCarInfoDao(Session session) {
		this.setSession(session);
	}

	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}

	public IautosCarInfo getByCarUrl(String sourceUrl, Session session) {
		Query query = session
				.createQuery("from IautosCarInfo where sourceUrl=:sourceUrl");
		query.setString("sourceUrl", sourceUrl);
		IautosCarInfo carInfo = (IautosCarInfo) query.uniqueResult();
		return carInfo;
	}

	public void addCarInfo(IautosCarInfo ici, Session session) {
		session.save(ici);
	}

}
