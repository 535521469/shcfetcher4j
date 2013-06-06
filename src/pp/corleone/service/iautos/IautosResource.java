package pp.corleone.service.iautos;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;

public class IautosResource {

	public static Properties prop = new Properties();
	public static ThreadPoolExecutor threadPool = null;

	static {
		InputStream is = IautosResource.class.getClassLoader()
				.getResourceAsStream("config.property");

		LoggerFactory.getLogger(IautosResource.class).info(
				"set default thread pool size :"
						+ Runtime.getRuntime().availableProcessors());

		// threadPool = (ThreadPoolExecutor)
		// Executors.newFixedThreadPool(Runtime
		// .getRuntime().availableProcessors());

		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BlockingQueue<Fetcher> fetchQueue = new LinkedBlockingQueue<Fetcher>();
	public static BlockingQueue<Callback> extractQueue = new LinkedBlockingQueue<Callback>();

}
