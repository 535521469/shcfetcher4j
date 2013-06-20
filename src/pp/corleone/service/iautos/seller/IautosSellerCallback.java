package pp.corleone.service.iautos.seller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.nodes.Document;

import pp.corleone.dao.DaoUtil;
import pp.corleone.dao.iautos.IautosSellerInfoDao;
import pp.corleone.domain.iautos.FetcherConstants;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.domain.iautos.IautosSellerInfo;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.iautos.IautosConstant;

public class IautosSellerCallback extends Callback {

	private IautosSellerInfoDao sellerDao;

	public IautosSellerCallback() {
		Session session = this.getSession();
		this.setSellerDao(new IautosSellerInfoDao(session));
	}

	public IautosSellerInfoDao getSellerDao() {
		return sellerDao;
	}

	public void setSellerDao(IautosSellerInfoDao sellerDao) {
		this.sellerDao = sellerDao;
	}

	public Session getSession() {
		return DaoUtil.getCurrentSession();
	}

	@Override
	public Map<String, Collection<?>> call() throws Exception {

		Map<String, Collection<?>> fetched = new HashMap<String, Collection<?>>();
		Collection<Fetcher> fetchers = new ArrayList<Fetcher>();
		fetched.put(FetcherConstants.Fetcher, fetchers);
		Document doc = this.getResponseWrapper().getDoc();

		IautosCarInfo ici = (IautosCarInfo) this.getResponseWrapper()
				.getContext().get(IautosConstant.CAR_INFO);

		Transaction tx = this.getSession().beginTransaction();

		this.getLogger().info("fetch " + this.getResponseWrapper().getUrl());

		try {
			IautosSellerInfo existIsi = null;
			if (null != ici) {
				existIsi = this.getSellerDao()
						.getBySeqID(ici.getIautosSellerInfo().getSeqID(),
								this.getSession());
			} else {
				IautosSellerInfo isi = (IautosSellerInfo) this
						.getResponseWrapper().getContext()
						.get(IautosConstant.SELLER_INFO);
				if (null != isi) {
					existIsi = this.getSellerDao().getBySeqID(isi.getSeqID(),
							this.getSession());
				}
			}

			if (null != existIsi) {
				IautosSellerExtractUtil.fillSeller(doc, existIsi);
			} else {
				getLogger().error(" seller is null .");
			}

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}

		return fetched;

	}

}
