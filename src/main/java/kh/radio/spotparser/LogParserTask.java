package kh.radio.spotparser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kh.radio.spotcollector.client.generated.Spot;
import kh.radio.spotcollector.client.generated.SpotCollectorEndpoint;
import kh.radio.spotcollector.client.generated.SpotCollectorEndpointService;
import kh.radio.spotparser.domain.ReceivedSpotHeader;
import kh.radio.spotparser.domain.TXMessage;
import kh.radio.spotparser.wsjt.LogFileReader;
import kh.radio.spotparser.wsjt.LogLineType;

import org.apache.log4j.Logger;

/**
 * Scheduled TimerTask to parse the log file for new spots.
 * 
 * @author kevin.hooke
 *
 */
public class LogParserTask extends TimerTask {

	private static final String LAST_LOG_PARSED = "lastLogParsed";

	/**
	 * key for lastLogParsed in Preferences for JUnit support
	 */
	static final String LAST_LOG_PARSED_TEST = "lastLogParsedTest";

	// persisted preferences, eg for time of last log parse
	private AppPreferences appPrefs;
	private LocalDateTime lastLogParsedDateTime;

	private String filePath;
	private LogFileReader reader;
	private String lastLogParsedPreferencesKey = LAST_LOG_PARSED;

	// log line type: new date reception starting header
	// LogLineType.NEW_DAY_HEADER_LINE
	private static final Pattern NEW_LINE_HEADER_PATTERN = Pattern
			.compile("\\d{4}-\\w{3}-\\d{2}");
	// 2014-Jul-07 19:40 14.076 MHz JT9+JT65
	private static final Pattern NEW_LINE_HEADER_ALL_FIELDS_PATTERN = Pattern
			.compile("(\\d{4}-\\w{3}-\\d{2})\\s+(\\d+:\\d+)\\s+(\\d+\\.\\d+\\s+)MHz");

	// log line type: decoded spot
	// LogLineType.DECODED_SPOT_LINE
	private static final Pattern DECODED_SPOT_PATTERN = Pattern
			.compile("\\d{4}[\\s]+-");
	// 1950 -21 0.2 1231 # CQ KN8J EM99
	private static final Pattern DECODED_SPOT_ALL_FIELDS_PATTERN = Pattern
			.compile("(\\d{4})\\s+([-]?\\d{1,2})\\s+([-]?\\d+\\.\\d)\\s+(\\d{1,4})\\s+#\\s+(\\w+)\\s+(\\w+)\\s+([-]?\\w+)");

	// log line type: my tx
	private static final Pattern TX_PATTERN = Pattern
			.compile("\\d{4}[\\s]+Transmitting");

	private static final Logger LOGGER = Logger.getLogger(LogParserTask.class);

	public LogParserTask() {
	}

	public LogParserTask(String filePath) throws Exception {
		this.filePath = filePath;
		this.reader = new LogFileReader(filePath);
		this.initPreferences();
	}

	@Override
	public void run() {
		LocalDateTime start = LocalDateTime.now(ZoneId.of("Z"));
		LOGGER.info("Starting file check at: " + start);
		LOGGER.info("Last log line parsed: " + this.lastLogParsedDateTime);
		try {
			this.parseAllLines();
			storeLastLogLineProcessedTime();
		} catch (IOException ioe) {
			LOGGER.error("ERRROR! Failed to read log file!", ioe);
		}

	}

	/**
	 * Stores last log line processed time to Preferences store
	 */
	private void storeLastLogLineProcessedTime() {

		LOGGER.info("Saving lastLogParsedDateTime to preferences:"
				+ this.lastLogParsedDateTime);
		long millis = DateTimeUtils
				.dateTimeToMillisUTC(this.lastLogParsedDateTime);
		this.appPrefs.setLastLogProcessedMillisUTC(millis);
		try {
			this.saveAppPrefs();
		} catch (BackingStoreException e) {
			System.out.println("ERROR: failed to write preferences store!");
			e.printStackTrace();
		}
	}

	public void resetlastLogParsedDateTimeStoredPreference()
			throws BackingStoreException {
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		prefs.clear();
		prefs.flush();
	}

	private void saveAppPrefs() throws BackingStoreException {
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		prefs.putLong(this.lastLogParsedPreferencesKey,
				this.appPrefs.getLastLogProcessedMillisUTC());
		prefs.flush();
	}

	public void initPreferences() {

		Preferences prefs = Preferences.userNodeForPackage(getClass());
		long lastLogParsedMillis = prefs.getLong(
				this.lastLogParsedPreferencesKey, 0);
		this.lastLogParsedDateTime = LocalDateTime.ofInstant(
				Instant.ofEpochMilli(lastLogParsedMillis), ZoneId.of("Z"));

		LOGGER.info("Preferences: lastLogParsedDateTime: "
				+ lastLogParsedDateTime);


		AppPreferences tempPrefs = new AppPreferences();
		tempPrefs.setLastLogProcessedMillisUTC(lastLogParsedMillis);
		this.appPrefs = tempPrefs;

	}

	/**
	 * Parses log file lines. Checks to find last line not already parsed 
	 * and starts processin from that point.
	 * 
	 * @return number of lines actually parsed (new lines, excludes lines previously parsed)
	 * @throws IOException
	 */
	public int parseAllLines() throws IOException {
		int lineCount = 1;

		List<Spot> spots = new ArrayList<>();

		LOGGER.info("parseAllLines starting");

		String currentLine = null;
		this.reader.resetToStartOfFile();

		// if we have already parsed part of this log, we need to skip forward
		// to find new lines
		if (this.lastLogParsedDateTime != null) {
			currentLine = findNextLineNotYetParsed();
		} else {
			// read the fist line and start readin from the start of the file
			currentLine = this.reader.nextLine();

		}

		LogLineType logLineType = null;
		ReceivedSpotHeader settings = null;
		Spot spot = null;

		while (currentLine != null) {
			LOGGER.info("line " + lineCount);

			TXMessage tx = null;

			logLineType = this.identfyLineType(currentLine);

			switch (logLineType) {
			case NEW_DAY_HEADER_LINE: {
				settings = parseReceiveSettings(currentLine);
				break;
			}

			case DECODED_SPOT_LINE: {
				spot = parseDecodedSpot(currentLine);

				// update timestamp for last spot parsed
				this.updateLastLogLineParsedTime(spot.getTime());

				// add spot to list ready to be uploaded
				spots.add(spot);
				break;
			}

			case TX_LINE: {
				tx = parseTx(currentLine);
				break;
			}

			case UNKNOWN_LINE: {
				LOGGER.error("Skipped line, could not identify");
				break;
			}
			}
			// read next line
			lineCount++;
			currentLine = this.reader.nextLine();
		}

		//TODO: on second timed run, logLineType is null if no new lines added
		
		// update last log line processed datetime
		if (logLineType.equals(LogLineType.NEW_DAY_HEADER_LINE)) {
			// strip ":" from time in new day header line in log
			this.lastLogParsedDateTime = DateTimeUtils.parseDateAndTime(
					settings.getDate(), settings.getTime().replace(":", ""));
		} else if (logLineType.equals(LogLineType.DECODED_SPOT_LINE)) {
			this.lastLogParsedDateTime = DateTimeUtils.parseDateAndTime(
					settings.getDate(), spot.getTime());
		}

		// send parsed spots to endpoint for storage
		if (spots != null && spots.size() > 0) {
			SpotCollectorEndpointService service = new SpotCollectorEndpointService();
			SpotCollectorEndpoint endpoint = service
					.getSpotCollectorEndpointPort();
			endpoint.storeSpots(spots);
		} else {
			LOGGER.info("No new spot data to send this time period...");
		}
		return spots.size();
	}

	/**
	 * Updates timestamp for last log line parsed. Updates only the time portion
	 * since the date was set on the last header line parsed.
	 * 
	 * @param time
	 */

	private void updateLastLogLineParsedTime(String time) {
		this.lastLogParsedDateTime = DateTimeUtils.updateTime(
				this.lastLogParsedDateTime, time);
	}

	/**
	 * Reads forward through the log to find the next new line that hasn't been
	 * parsed yet.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	private String findNextLineNotYetParsed() throws FileNotFoundException {
		ReceivedSpotHeader spotHeaderLineValues = null;
		Spot spot = null;
		String currentLine = this.reader.nextLine();

		// TODO: also need to step forward through spots for the current date
		// that follow the header

		while (currentLine != null) {
			LogLineType type = this.identfyLineType(currentLine);
			
			//check if header line has been parsed yet
			if (type.equals(LogLineType.NEW_DAY_HEADER_LINE)) {
				spotHeaderLineValues = parseReceiveSettings(currentLine);
				LocalDateTime currentHeaderDateTime = DateTimeUtils.parseDateAndTime(
						spotHeaderLineValues.getDate(), spotHeaderLineValues.getTime().replace(":", ""));
				if (lineHasNotBeenParsed(currentHeaderDateTime)) {
					break;
				}
			} else if (type.equals(LogLineType.DECODED_SPOT_LINE)) {
				//check if individual spot line has been parsed yet
				spot = parseDecodedSpot(currentLine);
				LocalDateTime currentSpotDateTime = DateTimeUtils.parseDateAndTime(
						spotHeaderLineValues.getDate(), spot.getTime());
				if (lineHasNotBeenParsed(currentSpotDateTime)) {
					break;
				}
			}

			// read next line
			currentLine = this.reader.nextLine();
		}

		return currentLine;
	}

	private boolean lineHasNotBeenParsed(LocalDateTime currentLineDateTime) {
		boolean result = false;

		LOGGER.debug("Checking if line already parsed, last line read marker: "
				+ this.lastLogParsedDateTime);
		LOGGER.debug("... comparing with current line: " + currentLineDateTime);

		if (this.lastLogParsedDateTime.isEqual(currentLineDateTime)) {
			LOGGER.debug("... skipping this line");
		} else {
			if (this.lastLogParsedDateTime.isBefore(currentLineDateTime)) {
				LOGGER.debug("...  this line not yet parsed");
				result = true;
			} else {
				LOGGER.debug("... skipping this line");
			}

		}

		return result;
	}

	
	private TXMessage parseTx(String currentLine) {
		// TODO not sure if we have a purpose to parse our own tx just yet
		return null;
	}

	public ReceivedSpotHeader parseReceiveSettings(String currentLine) {
		ReceivedSpotHeader receiveHeader = null;
		Matcher m = NEW_LINE_HEADER_ALL_FIELDS_PATTERN.matcher(currentLine);
		if (m.find()) {
			LOGGER.info("parsing header fields");
			String date = m.group(1);
			String time = m.group(2);
			LOGGER.info("... date;[" + date + "] time:[" + time + "]");
			receiveHeader = new ReceivedSpotHeader(date, time);

		} else {
			LOGGER.error("could not parse header fields");
		}
		return receiveHeader;
	}

	public Spot parseDecodedSpot(String currentLine) {
		Spot spot = null;
		Matcher m = DECODED_SPOT_ALL_FIELDS_PATTERN.matcher(currentLine);
		if (m.find()) {
			LOGGER.info("parsing spot fields");
			// 1950 -21 0.2 1231 # CQ KN8J EM99
			String time = m.group(1);
			String signalReport = m.group(2);
			String timeDeviation = m.group(3);
			String frequencyOffset = m.group(4);
			String word1 = m.group(5);
			String word2 = m.group(6);
			String word3 = m.group(7);
			LOGGER.info("... time:[" + time + "] signal:[" + signalReport
					+ "] timeDev:[" + timeDeviation + "] freqoffset:["
					+ frequencyOffset + "] word1:[" + word1 + "] word2:["
					+ word2 + "] word3:[" + word3 + "]");
			spot = new Spot(time, signalReport, timeDeviation, frequencyOffset,
					word1, word2, word3);

		} else {
			LOGGER.error("could not parse header fields");
		}
		return spot;
	}

	public LogLineType identfyLineType(String line) {
		LogLineType type;

		Matcher headerMatcher = NEW_LINE_HEADER_PATTERN.matcher(line);
		if (headerMatcher.find()) {
			type = LogLineType.NEW_DAY_HEADER_LINE;
			LOGGER.info("log file read: header line");
		} else {
			Matcher rxMatcher = DECODED_SPOT_PATTERN.matcher(line);
			if (rxMatcher.find()) {
				type = LogLineType.DECODED_SPOT_LINE;
				LOGGER.info("log file read: rx spot line");
			} else {
				Matcher txMatcher = TX_PATTERN.matcher(line);
				if (txMatcher.find()) {
					type = LogLineType.TX_LINE;
					LOGGER.info("log file read: tx line");
				} else {
					type = LogLineType.UNKNOWN_LINE;
					LOGGER.info("log file read: unknown");
				}
			}

		}
		return type;
	}

	public String getLastLogParsedPreferencesKey() {
		return lastLogParsedPreferencesKey;
	}

	public void setLastLogParsedPreferencesKey(
			String lastLogParsedPreferencesKey) {
		this.lastLogParsedPreferencesKey = lastLogParsedPreferencesKey;
	}

	public LogFileReader getReader() {
		return reader;
	}

	public void setReader(LogFileReader reader) {
		this.reader = reader;
	}

}
