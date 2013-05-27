package pp.corleone.service.iautos.list;

import java.util.AbstractQueue;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IautosListService {

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

	public IautosListService(int poolSize) {
		this.setPoolSize(poolSize);
	}

	public void fetch() throws InterruptedException {
		ExecutorService es = Executors.newFixedThreadPool(this.getPoolSize());

		// for (int i = 0; i < this.getPoolSize(); i++) {
		// es.submit(new IautosListFetcher(this.getListQueue().poll(), this
		// .getListQueue()));
		// es.wait();
		// }
		while (true) {
			while (!es.isShutdown() && this.getListQueue().size() > 0) {
				es.submit(new IautosListFetcher(this.getListQueue().poll(),
						this.getListQueue()));
				// Thread.sleep(2000);
				getLogger().info(
						"sleep 2s to continue add task "
								+ this.getListQueue().size());
			}
			Thread.sleep(3000);
			getLogger().info("sleep 3s for list url");
		}

	}
}
