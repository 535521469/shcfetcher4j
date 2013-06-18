package pp.corleone.service;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import pp.corleone.ConfigManager;
import pp.corleone.domain.iautos.IautosCarInfo;
import pp.corleone.service.iautos.IautosConstant;

public class StatusRequestWrapper implements Delayed {

	private Fetcher fetcher;

	public Fetcher getFetcher() {
		return fetcher;
	}

	public void setFetcher(Fetcher fetcher) {
		this.fetcher = fetcher;
	}

	public StatusRequestWrapper() {
	}

	public StatusRequestWrapper(Fetcher fetcher) {
		this.fetcher = fetcher;
	}

	@Override
	public int compareTo(Delayed o) {

		if (o instanceof StatusRequestWrapper) {
			IautosCarInfo cmp_ici = (IautosCarInfo) (((StatusRequestWrapper) o)
					.getFetcher().getRequestWrapper().getContext()
					.get(IautosConstant.CAR_INFO));

			IautosCarInfo this_ici = (IautosCarInfo) this.getFetcher()
					.getRequestWrapper().getContext()
					.get(IautosConstant.CAR_INFO);
			long t = this_ici.getLastActiveDate().getTime()
					- cmp_ici.getLastActiveDate().getTime();

			if (0 == t) {
				return 0;
			}

			return t > 0 ? 1 : -1;

		} else {
			throw new IllegalArgumentException(
					"must instance of StatusRequestWrapper , " + o.getClass());
		}

	}

	@Override
	public long getDelay(TimeUnit unit) {
		IautosCarInfo this_ici = (IautosCarInfo) this.getFetcher()
				.getRequestWrapper().getContext().get(IautosConstant.CAR_INFO);

		long lastActiveDateTime = this_ici.getLastActiveDate().getTime();

		long now = new Date().getTime();

		long statusDelay = ConfigManager.getInstance().getConfigItem(
				IautosConstant.STATUS_DELAY, 28800);
		int statusAheadOfTime = ConfigManager.getInstance().getConfigItem(
				IautosConstant.AHEAD_OF_TIME, 600);

		long seconds = TimeUnit.SECONDS.convert((lastActiveDateTime - now),
				TimeUnit.MILLISECONDS) + statusDelay - statusAheadOfTime;

		return seconds;

	}
}
