package pp.corleone.service.iautos;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractQueue;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

import pp.corleone.service.iautos.city.IautosCityService;
import pp.corleone.service.iautos.list.IautosListService;

public class IautosMainService {

	public static Properties prop = new Properties();

	static {
		InputStream is = IautosMainService.class.getClassLoader()
				.getResourceAsStream("config.property");

		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ConcurrentSkipListMap<String, AbstractQueue<?>> logicMap = new ConcurrentSkipListMap<String, AbstractQueue<?>>();

	public ConcurrentSkipListMap<String, AbstractQueue<?>> getLogicMap() {
		return logicMap;
	}

	public void setLogicMap(
			ConcurrentSkipListMap<String, AbstractQueue<?>> logicMap) {
		this.logicMap = logicMap;
		
	}

	public static void main(String[] args) throws InterruptedException {
		IautosMainService ms = new IautosMainService();
		IautosCityService cs = new IautosCityService(1);
		cs.setProp(prop);
		ConcurrentLinkedQueue<String> listUrls = new ConcurrentLinkedQueue<String>();
		cs.setListQueue(listUrls);
		ms.getLogicMap().put(IautosCityService.logicMapKey, listUrls);
		cs.fetch();
		
		IautosListService ls = new IautosListService(1);
		ls.setProp(prop);
		ls.setListQueue(listUrls);
		ls.fetch();
	}
}
