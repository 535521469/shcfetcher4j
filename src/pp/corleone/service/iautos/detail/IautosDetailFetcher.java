package pp.corleone.service.iautos.detail;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import pp.corleone.dao.DaoUtil;
import pp.corleone.dao.iautos.IautosCarInfoDao;
import pp.corleone.dao.iautos.IautosSellerInfoDao;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosCarInfo.IautosStatusCode;
import pp.corleone.service.Fetcher;
import pp.corleone.service.RequestWrapper;
import pp.corleone.service.iautos.IautosConstant;

public class IautosDetailFetcher extends Fetcher {

	private IautosSellerInfoDao sellerDao;
	private IautosCarInfoDao carDao;

	public Session getSession() {
		return DaoUtil.getCurrentSession();
	}

	public IautosSellerInfoDao getSellerDao() {
		return sellerDao;
	}

	public IautosCarInfoDao getCarDao() {
		return carDao;
	}

	public void setCarDao(IautosCarInfoDao carDao) {
		this.carDao = carDao;
	}

	public void setSellerDao(IautosSellerInfoDao sellerDao) {
		this.sellerDao = sellerDao;
	}

	public IautosDetailFetcher(RequestWrapper requestWrapper) {
		super(requestWrapper);
		Session session = this.getSession();
		this.setSellerDao(new IautosSellerInfoDao(session));
		this.setCarDao(new IautosCarInfoDao(session));
	}

//	@Override
//	public boolean isIgnore() {
//		boolean ignore = true;
//
//		IautosCarInfo carInfo = (IautosCarInfo) this.getRequestWrapper()
//				.getContext().get(IautosConstant.CAR_INFO);
//
//		if (null == carInfo.getDeclareDate()) {
//			return ignore;
//		}
//
//		Transaction tx = getSession().beginTransaction();
//
//		List<IautosCarInfo> carInfos = null;
//		try {
//			carInfos = this.getCarDao().listByCarUrlAndDeclareDate(
//					carInfo.getCarSourceUrl(), carInfo.getDeclareDate(),
//					this.getSession());
//			tx.commit();
//		} catch (Exception e) {
//			tx.rollback();
//			e.printStackTrace();
//		}
//
//		if (carInfos.size() == 0) {
//			// there's no record
//			return false;
//		} else {
//			for (IautosCarInfo ici : carInfos) {
//				if (null != ici.getDeclareDate()
//						&& null != carInfo.getDeclareDate()
//						&& ici.getDeclareDate()
//								.equals(carInfo.getDeclareDate())) {
//					// exist carinfo's declare date is not null and is equals
//					// with the car's declare date
//					this.getLogger().debug(
//							"car is exist :" + ici.getSeqID()
//									+ ici.getDeclareDate() + "" + " "
//									+ ici.getCarSourceUrl());
//					break;
//				}
//
//				if (ici.getStatusType() != null
//						&& IautosStatusCode.STATUS_TYPE_FOR_SALE.getCode() != ici
//								.getStatusType()) {
//					// the car life cycle is over
//					getLogger().debug(
//							"car life cycle is over :" + ici.getStatusType()
//									+ this.getRequestWrapper().getUrl());
//					break;
//				}
//			}
//		}
//
//		return ignore;
//	}
}
