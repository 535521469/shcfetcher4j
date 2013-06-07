package pp.corleone.domain.iautos;

import java.io.Serializable;

import pp.corleone.domain.CarInfo;

public class IautosCarInfo extends CarInfo implements Cloneable, Serializable {

	private static final long serialVersionUID = -4395908881660747661L;

	public static final String SOURCE_TYPE = "iautos";

	// public static final int STATUS_TYPE_FOR_SALE = 1;
	// public static final int STATUS_TYPE_SOLD = 4;
	// public static final int STATUS_TYPE_OVERDUE = 8;

	private IautosSellerInfo iautosSellerInfo;

	private String shopUrl;
	private String licenseDate; // register date
	private String brand; // suo shu pin pai
	private String manufacturer; // sheng chan chang shang ,
	private String parkAddress; // the place where the car parks

	public String getParkAddress() {
		return parkAddress;
	}

	public void setParkAddress(String parkAddress) {
		this.parkAddress = parkAddress;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

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

	public String getLicenseDate() {
		return licenseDate;
	}

	public void setLicenseDate(String licenseDate) {
		this.licenseDate = licenseDate;
	}

	@Override
	public IautosCarInfo clone() throws CloneNotSupportedException {
		return (IautosCarInfo) super.clone();
	}

	public enum IautosStatusCode {
		STATUS_TYPE_FOR_SALE(1, "\u5F85\u552E"), STATUS_TYPE_SOLD(4,
				"\u5DF2\u552E"), STATUS_TYPE_OVERDUE(8, "\u903E\u671F");

		private int code;
		private String desc;

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		private IautosStatusCode(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

	}

}
