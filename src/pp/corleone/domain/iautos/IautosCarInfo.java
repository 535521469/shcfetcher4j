package pp.corleone.domain.iautos;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IautosCarInfo) {
			IautosCarInfo ici = (IautosCarInfo) obj;
			if (null != ici.getSeqID() && null != this.getSeqID()) {
				return ici.getSeqID().equals(this.getSeqID());
			}
		}
		return false;
	};

	public enum IautosStatusCode {
		STATUS_TYPE_FOR_SALE(1, "\u5F85\u552E"), STATUS_TYPE_SOLD(4,
				"\u5DF2\u552E"), STATUS_TYPE_OVERDUE(8, "\u903E\u671F"), STATUS_TYPE_CANCEL(
				16, "\u64A4\u9500");

		private int code;
		private String desc;

		public int getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		public static IautosStatusCode getByDesc(String desc) {
			if (null == desc) {
				throw new IllegalArgumentException(
						"IautosStatusCode desc is null");
			}
			for (IautosStatusCode iautosStatusCode : IautosStatusCode.values()) {
				if (iautosStatusCode.getDesc().equals(desc)) {
					return iautosStatusCode;
				}
			}
			throw new IllegalArgumentException(desc
					+ " match non IautosStatusCode");
		}

		public static IautosStatusCode getByCode(int code) {
			for (IautosStatusCode iautosStatusCode : IautosStatusCode.values()) {
				if (iautosStatusCode.getCode() == code) {
					return iautosStatusCode;
				}
			}
			throw new IllegalArgumentException(code
					+ " match non IautosStatusCode");
		}

		private IautosStatusCode(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

	}

	@Override
	public String toString() {

		return new ToStringBuilder(this).append("url", this.getSourceType())
				.append("contacter phone", this.getContacterPhone())
				.append("contacter", this.getContacter()).toString();
	}

}
