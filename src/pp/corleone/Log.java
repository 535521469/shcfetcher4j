package pp.corleone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

	private static final Logger getLogger() {
		try {
			return LoggerFactory.getLogger(Class.forName(new Throwable()
					.getStackTrace()[2].getClassName()));
		} catch (ClassNotFoundException e) {

		}
		return null;
	}

	public static final void info(String msg) {
		getLogger().info(msg);
	}

	public static final void error(String msg) {
		getLogger().error(msg);
	}

	public static final void error(String msg, Throwable throwable) {
		getLogger().error(msg, throwable);
	}

	public static final void debug(String msg) {
		getLogger().debug(msg);
	}

	public static final void warn(String msg) {
		getLogger().warn(msg);
	}

}
