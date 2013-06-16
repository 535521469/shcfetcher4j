package pp.corleone.service.iautos.status;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import pp.corleone.dao.DaoUtil;
import pp.corleone.dao.iautos.IautosCarInfoDao;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosCarInfo.IautosStatusCode;
import pp.corleone.service.Callback;
import pp.corleone.service.iautos.IautosConstant;
import pp.corleone.service.iautos.detail.IautosDetailUtil;

public class IautosStatusCallback extends Callback {

	private IautosCarInfoDao carDao;

	public IautosStatusCallback() {
		Session session = this.getSession();
		this.setCarDao(new IautosCarInfoDao(session));
	}

	public Session getSession() {
		return DaoUtil.getCurrentSession();
	}

	public IautosCarInfoDao getCarDao() {
		return carDao;
	}

	public void setCarDao(IautosCarInfoDao carDao) {
		this.carDao = carDao;
	}

	@Override
	public Map<String, Collection<?>> call() throws Exception {

		Document doc = this.getResponseWrapper().getDoc();
		IautosCarInfo ici = (IautosCarInfo) this.getResponseWrapper()
				.getContext().get(IautosConstant.CAR_INFO);

		Element detailDivTag = doc.select("#car_detail").first();

		String priceLiteral = IautosDetailUtil.getPriceLiteral(detailDivTag);
		String statusLiteral = IautosDetailUtil.getStatusLiteral(detailDivTag);

		IautosStatusCode code = IautosDetailUtil.getStatusCode(priceLiteral,
				statusLiteral);

		Transaction tx = this.getSession().beginTransaction();

		try {
			IautosCarInfo exist_ici = this.getCarDao().getBySeqID(
					ici.getSeqID(), this.getSession());

			Date now = new Date();
			if (code.equals(IautosStatusCode.STATUS_TYPE_FOR_SALE)) {
				exist_ici.setLastActiveDate(now);
				this.getLogger()
						.info("for cycle check:" + exist_ici.getSeqID());
			} else {
				exist_ici.setStatusType(code);
				exist_ici.setOfflineDate(now);
				getLogger().info("offline:" + exist_ici.getSeqID());
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		}

		return null;
	}

}
