package kh.radio.spotparser;

import java.util.Calendar;
import java.util.Timer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Standalone log parser app. Runs on a timer to run at 10 seconds past each
 * minute. This is intended to watch and monitor spots in the live WSJT log
 * file, but only run during the period where WSJT is receiving, so the parsing
 * of latest log file entries is complete by the time WSJT writes to the file
 * again on the next decode (at apparently 50 seconds past the minute.
 * 
 * @author kevin.hooke
 *
 */
public class CallsignSpotLogFileParserApp {

	private Timer timer;
	private LogParserTask logParserTask;
	private boolean localEndpoint;

	public static void main(String[] args) {

		CallsignSpotLogFileParserApp parser = new CallsignSpotLogFileParserApp();
		parser.handleArgs(args);
		parser.init();
	}

	private void handleArgs(String[] args) {
		if(args != null){
			if(args.length > 0) {
				if(args[0].equals("resetlog")){
					System.out.println("Clearing last log line parsed ... resetting to initial state ...");
		 			this.resetLastLogParsed();
		 			System.out.println(".. done, app is reset");
					System.exit(0);
				}
				
				if(args[0].equals("localtest")){
					this.localEndpoint = true;
				}
				
			}
		}
		
	}

	private void resetLastLogParsed() {
		try {
			new LogParserTask().resetlastLogParsedDateTimeStoredPreference();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

	}

	private void init() {

		try {
			this.timer = new Timer();
			this.logParserTask = new LogParserTask(
					"/Users/kev/develop/AmateurRadioCallsignSpotHistory/CallsignSpotParser/CallsignSpotParser/src/test/resources/ALL_pt1.TXT",
					this.localEndpoint);

			this.timer.schedule(this.logParserTask, this.getMillisToFirstRun(),
					60 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private long getMillisToFirstRun() {
		long secs;
		Calendar c = Calendar.getInstance();
		int secsNow = c.get(Calendar.SECOND);
		secs = 60 - secsNow;

		System.out.println("Secs before starting first run next run: " + secs);

		return (secs + 10) * 1000;
	}

}
