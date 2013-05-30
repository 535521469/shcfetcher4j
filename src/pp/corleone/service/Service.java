package pp.corleone.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Service {

	protected final Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

	abstract public void fetch();

	abstract public void extract();

}
