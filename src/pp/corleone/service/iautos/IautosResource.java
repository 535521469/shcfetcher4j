package pp.corleone.service.iautos;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;

public class IautosResource {

	public static Properties prop = new Properties();

	static {
		InputStream is = IautosResource.class.getClassLoader()
				.getResourceAsStream("config.property");
		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(8);

	public static BlockingQueue<Fetcher> fetchQueue = new LinkedBlockingQueue<Fetcher>();
	public static BlockingQueue<Callback> extractQueue = new LinkedBlockingQueue<Callback>();

}
