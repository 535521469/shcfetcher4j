package pp.corleone.service.iautos.city;

import java.io.UnsupportedEncodingException;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IautosCityService {

	public final static String logicMapKey = "List";

	protected final Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

	private Properties prop;

	public Properties getProp() {
		return prop;
	}

	public void setProp(Properties prop) {
		this.prop = prop;
	}

	private AbstractQueue<String> listQueue;

	public AbstractQueue<String> getListQueue() {
		return listQueue;
	}

	public void setListQueue(AbstractQueue<String> listQueue) {
		this.listQueue = listQueue;
	}

	private int poolSize;

	public int getPoolSize() {
		return poolSize;
	}

	private void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	private ArrayBlockingQueue<String> cityUrls;

	public ArrayBlockingQueue<String> getCityUrls() {
		return cityUrls;
	}

	public void setCityUrls(ArrayBlockingQueue<String> cityUrls) {
		this.cityUrls = cityUrls;
	}

	public IautosCityService(int poolSize) {
		this.setPoolSize(poolSize);
	}

	public void fetch() {
		ScheduledExecutorService es = (ScheduledExecutorService) Executors
				.newScheduledThreadPool(this.getPoolSize());

		String city = null;
		try {
			city = new String(this.getProp().getProperty("cities").intern()
					.getBytes("ISO-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Set<String> cities = new HashSet<String>(Arrays.asList(city.split(",")));

		es.scheduleAtFixedRate(
				new IautosCityFetcher(cities, this.getListQueue()), 0, 600,
				TimeUnit.SECONDS);

	}
}
