package pp.corleone.service.iautos;

import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import pp.corleone.service.Callback;
import pp.corleone.service.Fetcher;
import pp.corleone.service.StatusRequestWrapper;

public class IautosResource {

	public static ThreadPoolExecutor fetcherPool = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 2);

	public static ThreadPoolExecutor extracterPool = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

	public static BlockingQueue<Fetcher> fetchQueue = new PriorityBlockingQueue<Fetcher>(
			512, new Comparator<Fetcher>() {

				@Override
				public int compare(Fetcher f1, Fetcher f2) {
					return f1.getRequestWrapper().getPriority()
							- f2.getRequestWrapper().getPriority();
				}
			});

	// public static BlockingQueue<Callback> extractQueue = new
	// PriorityBlockingQueue<Callback>(
	// 512, new Comparator<Callback>() {
	// @Override
	// public int compare(Callback f1, Callback f2) {
	// return f1.getResponseWrapper().getReferRequestWrapper()
	// .getPriority()
	// - f2.getResponseWrapper().getReferRequestWrapper()
	// .getPriority();
	// }
	// });

	public static BlockingQueue<Callback> extractQueue = new ArrayBlockingQueue<Callback>(
			50);

	public static BlockingQueue<StatusRequestWrapper> statusQueue = new DelayQueue<StatusRequestWrapper>();

}
