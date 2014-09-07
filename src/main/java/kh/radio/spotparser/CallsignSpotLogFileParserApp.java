package kh.radio.spotparser;

import java.util.Date;
import java.util.Timer;

public class CallsignSpotLogFileParserApp {

	private Timer timer;
	private LogParserTask logParserTask;

	public static void main(String[] args) {

		new CallsignSpotLogFileParserApp().init();
	}

	private void init() {
		try {
			this.timer = new Timer();
			this.logParserTask = new LogParserTask("todo");
			Date firstRun = this.getFirstRunTime();

			this.timer.schedule(this.logParserTask, firstRun, 60 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Date getFirstRunTime() {
		// TODO Auto-generated method stub
		return null;
	}

}
