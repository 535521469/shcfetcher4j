package pp.corleone.dao.iautos;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosCarInfo.IautosStatusCode;

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

	@Deprecated
	public IautosCarInfo getByCarUrl(String sourceUrl, Session session) {
		Query query = session
				.createQuery("from IautosCarInfo where sourceUrl=:sourceUrl");
		query.setString("sourceUrl", sourceUrl);
		IautosCarInfo carInfo = (IautosCarInfo) query.uniqueResult();
		return carInfo;
	}

	@SuppressWarnings("unchecked")
	public List<IautosCarInfo> listByCarUrl(String sourceUrl, Session session) {
		Query query = session
				.createQuery("from IautosCarInfo where sourceUrl=:sourceUrl");
		query.setString("sourceUrl", sourceUrl);
		List<IautosCarInfo> carInfos = (List<IautosCarInfo>) query.list();
		return carInfos;
	}

	@SuppressWarnings("unchecked")
	public List<IautosCarInfo> listByStatusCode(IautosStatusCode statusCode,
			Session session) {
		Query query = session
				.createQuery("from IautosCarInfo where statusType=:statusType");
		query.setInteger("statusType", statusCode.getCode());
		List<IautosCarInfo> carInfos = (List<IautosCarInfo>) query.list();
		return carInfos;
	}

	@SuppressWarnings("unchecked")
	public List<IautosCarInfo> listByStatusCodeAndLastActiveDateTime(
			IautosStatusCode statusCode, Date lastActiveDate, Session session) {
		Query query = session
				.createQuery("from IautosCarInfo where statusType=:statusType and lastActiveDate<=:lastActiveDate");
		query.setInteger("statusType", statusCode.getCode());
		query.setDate("lastActiveDate", lastActiveDate);
		List<IautosCarInfo> carInfos = (List<IautosCarInfo>) query.list();
		return carInfos;
	}

	public IautosCarInfo getByCarUrlAndDeclareDate(String sourceUrl,
			Date declareDate, Session session) {
		Query query = session
				.createQuery("from IautosCarInfo where sourceUrl=:sourceUrl and declareDate = :declareDate");
		query.setString("sourceUrl", sourceUrl);
		query.setDate("declareDate", declareDate);
		IautosCarInfo carInfo = (IautosCarInfo) query.uniqueResult();
		return carInfo;
	}

	@Deprecated
	public IautosCarInfo getByCarUrl(String sourceUrl) {
		Query query = this.getSession().createQuery(
				"from IautosCarInfo where sourceUrl=:sourceUrl");
		query.setString("sourceUrl", sourceUrl);
		IautosCarInfo carInfo = (IautosCarInfo) query.uniqueResult();
		return carInfo;
	}

	public void addCarInfo(IautosCarInfo ici, Session session) {
		session.save(ici);
	}

	public void addCarInfo(IautosCarInfo ici) {
		this.getSession().save(ici);
	}

	public IautosCarInfo getBySeqID(String seqid, Session session) {
		return (IautosCarInfo) session.get(IautosCarInfo.class, seqid);
	}

}
