package pp.corleone.domain.iautos;

import java.io.Serializable;

import pp.corleone.domain.CarInfo;

public class IautosCarInfo extends CarInfo implements Cloneable, Serializable {

	private static final long serialVersionUID = -4395908881660747661L;

	public static final String SOURCE_TYPE = "iautos";

	public static final int STATUS_TYPE_FOR_SALE = 1;
	public static final int STATUS_TYPE_SOLD = 4;
	public static final int STATUS_TYPE_OVERDUE = 8;

	private IautosSellerInfo iautosSellerInfo;

	private String shopUrl;

	public String getShopUrl() {
		return shopUrl;
	}

	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}

	public IautosCarInfo() {
		this.setSourceType();
	}

	public IautosSellerInfo getIautosSellerInfo() {
		return iautosSellerInfo;
	}

	public void setIautosSellerInfo(IautosSellerInfo iautosSellerInfo) {
		this.iautosSellerInfo = iautosSellerInfo;
	}

	public void setSourceType() {
		this.sourceType = SOURCE_TYPE;
	}

	public String getSourceType() {
		return SOURCE_TYPE;
	}

	@Override
	public IautosCarInfo clone() throws CloneNotSupportedException {
		return (IautosCarInfo) super.clone();
	}

}
