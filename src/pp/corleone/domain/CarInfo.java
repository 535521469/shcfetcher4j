package pp.corleone.domain;

import java.util.Date;

import pp.corleone.domain.iautos.IautosCarInfo.IautosStatusCode;

public abstract class CarInfo {

	public static final int SELLER_TYPE_SHOP = 1;
	public static final int SELLER_TYPE_PERSON = 2;

	private String carSourceUrl;
	protected String sourceType; // 58 or iautos or something else
	private String locate;
	private String seqID;
	private String sellerID;
	private int sellerType; // shop or person
	private String title;
	private String contacter;
	private String contacterPhone;
	private Date declareDate;
	private Date fetchDate;
	private Date lastActiveDate;
	private Date offlineDate;
	private String cityName;
	private Integer statusType;
	private String price;
	private String color;
	private String roadHaul;
	private String displacement;
	private String gearbox;

	public Date getFetchDate() {
		return fetchDate;
	}

	public void setFetchDate(Date fetchDate) {
		this.fetchDate = fetchDate;
	}

	public Date getLastActiveDate() {
		return lastActiveDate;
	}

	public void setLastActiveDate(Date lastActiveDate) {
		this.lastActiveDate = lastActiveDate;
	}

	public Date getOfflineDate() {
		return offlineDate;
	}

	public void setOfflineDate(Date offlineDate) {
		this.offlineDate = offlineDate;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Integer getStatusType() {
		return statusType;
	}

	public void setStatusType(Integer statusType) {
		this.statusType = statusType;
	}

	public void setStatusType(IautosStatusCode statusType) {
		this.statusType = statusType.getCode();
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getRoadHaul() {
		return roadHaul;
	}

	public void setRoadHaul(String roadHaul) {
		this.roadHaul = roadHaul;
	}

	public String getDisplacement() {
		return displacement;
	}

	public void setDisplacement(String displacement) {
		this.displacement = displacement;
	}

	public String getGearbox() {
		return gearbox;
	}

	public void setGearbox(String gearbox) {
		this.gearbox = gearbox;
	}

	public String getContacter() {
		return contacter;
	}

	public void setContacter(String contacter) {
		this.contacter = contacter;
	}

	public String getContacterPhone() {
		return contacterPhone;
	}

	public void setContacterPhone(String contacterPhone) {
		this.contacterPhone = contacterPhone;
	}

	public int getSellerType() {
		return sellerType;
	}

	public void setSellerType(int sellerType) {
		this.sellerType = sellerType;
	}

	public String getCarSourceUrl() {
		return carSourceUrl;
	}

	public void setCarSourceUrl(String carSourceUrl) {
		this.carSourceUrl = carSourceUrl;
	}

	public String getLocate() {
		return locate;
	}

	public void setLocate(String locate) {
		this.locate = locate;
	}

	public String getSeqID() {
		return seqID;
	}

	public void setSeqID(String seqID) {
		this.seqID = seqID;
	}

	public String getSellerID() {
		return sellerID;
	}

	public void setSellerID(String sellerID) {
		this.sellerID = sellerID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDeclareDate() {
		return declareDate;
	}

	public void setDeclareDate(Date declareDate) {
		this.declareDate = declareDate;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

}
