package pp.corleone.service.iautos;

public class IautosConstant {

	public final static String homePage = "http://www.iautos.cn/";
	public final static String searchPage = "http://so.iautos.cn/";

	public final static String CITIES = "cities";

	public final static String ONGOING_FLAG = "ongoing_flag";

	public final static String STATUS_CHECK_FLAG = "status_check_flag";
	public final static String ONGOING_CYCLE_DELAY = "ongoing_cycle_delay";

	public final static String FETCHER_IDLE_SLEEP = "fetcher_idle_sleep";
	public final static String STATUS_CARRIER_IDLE_SLEEP = "status_carrIer_idle_sleep";

	public final static String STATUS_DELAY = "status_delay";
	public final static String STATUS_CHECK_RANGE = "status_check_range";
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
