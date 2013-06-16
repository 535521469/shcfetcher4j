package pp.corleone.service.iautos;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.LoggerFactory;

import pp.corleone.ConfigManager;
import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.StatusRequestWrapper;

public class IautosResource {

	public static ConfigManager cm = null;
	public static ThreadPoolExecutor threadPool = null;

	static {

		LoggerFactory.getLogger(IautosResource.class).info(
				"set default thread pool size :"
						+ (Runtime.getRuntime().availableProcessors() + 1));

		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors() + 1);

		// threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
		
		cm = ConfigManager.getInstance();
	}

	public static BlockingQueue<Fetcher> fetchQueue = new PriorityBlockingQueue<Fetcher>(
			512, new Comparator<Fetcher>() {

				@Override
				public int compare(Fetcher f1, Fetcher f2) {
					return f1.getRequestWrapper().getPriority()
							- f2.getRequestWrapper().getPriority();
				}
			});
	public static BlockingQueue<Callback> extractQueue = new LinkedBlockingQueue<Callback>();

	public static BlockingQueue<StatusRequestWrapper> statusQueue = new DelayQueue<StatusRequestWrapper>();

}
