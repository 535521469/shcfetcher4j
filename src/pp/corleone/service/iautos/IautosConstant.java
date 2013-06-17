package pp.corleone.service.iautos;

public class IautosConstant {

	public final static String homePage = "http://www.iautos.cn/";
	public final static String searchPage = "http://so.iautos.cn/";
	public final static String CITIES = "cities";

	public final static String STATUS_DELAY = "status_delay";
	public final static String STATUS_SPLIT_PARTS = "status_split_parts";
	public final static String AHEAD_OF_TIME = "ahead_of_time";

	public final static String LIST = "LIST";
	public final static String DETAIL = "DETAIL";
	public final static String CAR_INFO = "CARINFO";
	public final static String SELLER_INFO = "SELLER_INFO";

	public static String buildShopUrl(String url) {
		return url + "pas1ds9vepcatcpbnscac/";
		// return url + "pas1ds10vepcatcpbnscac/";
	}

	public static String buildPersonalUrl(String url) {
		return url + "pas2ds9vepcatcpbnscac/";
		// return url + "pas2ds10vepcatcpbnscac/";
	}

}
