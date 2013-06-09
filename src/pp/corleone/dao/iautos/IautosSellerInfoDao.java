package pp.corleone.dao.iautos;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import pp.corleone.domain.iautos.IautosSellerInfo;

public class IautosSellerInfoDao {

	private Session session;

	public IautosSellerInfoDao(Session session) {
		this.setSession(session);
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public IautosSellerInfo getByShopUrl(String shopUrl) {
		Query query = this.session
				.createQuery("from IautosSellerInfo where shopUrl=:shopUrl");
		query.setString("shopUrl", shopUrl);
		IautosSellerInfo shop = (IautosSellerInfo) query.uniqueResult();
		return shop;
	}

	public void addShopInfo(IautosSellerInfo isi) {
		this.getSession().save(isi);
	}

	public IautosSellerInfo getByShopUrl(String shopUrl, Session session) {
		Query query = session
				.createQuery("from IautosSellerInfo where shopUrl=:shopUrl");
		query.setString("shopUrl", shopUrl);
		IautosSellerInfo shop = (IautosSellerInfo) query.uniqueResult();
		return shop;
	}

	@SuppressWarnings("unchecked")
	public List<IautosSellerInfo> getUnfetchedSellerInfos(Session session) {
		List<IautosSellerInfo> isis = new ArrayList<IautosSellerInfo>();
		Query query = session.createQuery("from IautosSellerInfo where "
				+ " shopName is null" + " and shopAddress is null"
				+ " and shopPhone is null");
		isis = (List<IautosSellerInfo>) query.list();
		return isis;
	}

	public void addShopInfo(IautosSellerInfo isi, Session session) {
		session.save(isi);
	}

	public IautosSellerInfo getBySeqID(String seqid, Session session) {
		if (null == seqid) {
			throw new IllegalArgumentException(
					" get by seller seqid , seqid is null ");
		}
		return (IautosSellerInfo) session.get(IautosSellerInfo.class, seqid);
	}

}
