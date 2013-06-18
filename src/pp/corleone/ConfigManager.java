package pp.corleone;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager {

	private Properties m_props = null;

	private static ConfigManager m_instance = new ConfigManager();

	protected final static Logger getLogger() {
		return LoggerFactory.getLogger(ConfigManager.class);
	}

	private ConfigManager() {

		m_props = new Properties();

		try {
			m_props.load(this.getClass().getClassLoader()
					.getResourceAsStream("config.property"));
		} catch (FileNotFoundException e) {
			getLogger().error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			getLogger().error(e.getMessage());
			e.printStackTrace();
		}

	}

	public synchronized static ConfigManager getInstance() {
		return m_instance;
	}

	public final String getConfigItem(String key, String defaultVal) {
		return m_props.containsKey(key) ? m_props.getProperty(key) : defaultVal;
	}

	public final int getConfigItem(String key, int defaultVal) {
		return m_props.containsKey(key) ? Integer.valueOf(m_props
				.getProperty(key)) : defaultVal;
	}

}
