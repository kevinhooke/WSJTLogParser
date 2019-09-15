package kh.radio.spotparser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import kh.radio.spotcollector.client.generated.JMSException_Exception;
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
	 * Preference key for lastLogParsed - used for JUnit support so we don't overwrite
	 * an actual runtime value stored in preferences
	 */
	static final String LAST_LOG_PARSED_TEST = "lastLogParsedTest";

	// persisted preferences, eg for time of last log parse
	private AppPreferences appPrefs;
	private boolean localTestMode;
	private LocalDateTime lastLogParsedDateTime;
	private String filePath;
	private LogFileReader reader;
	private ReceivedSpotHeader spotHeader = null;
	private String lastLogParsedPreferencesKey = LAST_LOG_PARSED;
	private String spotterCallsign;

	// log line type: new date reception starting header
	// LogLineType.NEW_DAY_HEADER_LINE
	private static final Pattern NEW_LINE_HEADER_PATTERN = Pattern
			.compile("\\d{4}-\\w{3}-\\d{2}");
	// 2014-Jul-07 19:40 14.076 MHz JT9+JT65
	
	// log line type: new date reception starting header v2.x
	// LogLineType.NEW_DAY_HEADER_LINE_V2
	private static final Pattern NEW_LINE_HEADER_PATTERN_V2 = Pattern
			.compile("\\d{4}-\\d{2}-\\d{2}");
	// 2017-12-03 00:32  14.076 MHz  JT9
	
	private static final Pattern NEW_LINE_HEADER_ALL_FIELDS_PATTERN = Pattern
			.compile("(\\d{4}-\\w{3}-\\d{2})\\s+(\\d+:\\d+)\\s+(\\d+\\.\\d+)\\s+MHz");

	private static final Pattern NEW_LINE_HEADER_ALL_FIELDS_PATTERN_V2 = Pattern
			.compile("(\\d{4}-\\d{2}-\\d{2})\\s+(\\d+:\\d+)\\s+(\\d+\\.\\d+)\\s+MHz");
	// 2017-12-03 00:32  14.076 MHz  JT9
	
	// log line type: decoded spot
	// LogLineType.DECODED_SPOT_LINE
	private static final Pattern DECODED_SPOT_PATTERN = Pattern
			.compile("\\d{4,6}[\\s]+-");
	// v1: 1950 -21 0.2 1231 # CQ KN8J EM99
	// v2: 222845 -13  0.5  758 ~  K0BLT K4SBZ EM70
	//updated first \\d patter for 4 to 6 digits to handle v1 and v2 pattern
	private static final Pattern DECODED_SPOT_ALL_FIELDS_PATTERN = Pattern
			.compile("(\\d{4,6})\\s+([-]?\\d{1,2})\\s+([-]?\\d+\\.\\d)\\s+(\\d{1,4})\\s+[#|@|~]\\s+(\\w+)\\s+(\\w+)\\s+([-]?\\w+)");

	// log line type: my tx
	private static final Pattern TX_PATTERN = Pattern
			.compile("\\d{4}[\\s]+Transmitting");

	private static final Logger LOGGER = Logger.getLogger(LogParserTask.class);

	public LogParserTask() {
	}

	public LogParserTask(String filePath, boolean localTestMode, String spotterCallsign)
			throws Exception {
		this.filePath = filePath;
		this.reader = new LogFileReader(filePath);
		this.localTestMode = localTestMode;
		this.spotterCallsign = spotterCallsign;
		this.initPreferences();

		// TODO: need to load endpoint properties from property file here
	}

	/**
	 * TimerTask run() method: checks for and parses any new lines added since
	 * the last time we read the log file.
	 */
	@Override
	public void run() {
		LocalDateTime start = LocalDateTime.now(ZoneId.of("Z"));
		LOGGER.info("Starting file check at: " + start);
		LOGGER.info("... last log line parsed: " + this.lastLogParsedDateTime);
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
	void storeLastLogLineProcessedTime() {

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
	 * Parses log file lines. Checks to find last line not already parsed and
	 * starts processin from that point.
	 * 
	 * @return number of lines actually parsed (new lines, excludes lines
	 *         previously parsed)
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
			// read the fist line and start reading from the start of the file
			currentLine = this.reader.nextLine();

		}

		LogLineType logLineType = null;
		Spot spot = null;

		while (currentLine != null) {
			LOGGER.info("line " + lineCount);

			TXMessage tx = null;

			logLineType = this.identfyLineType(currentLine);

			switch (logLineType) {
			case NEW_DAY_HEADER_LINE:
			case NEW_DAY_HEADER_LINE_V2:
			{
				this.spotHeader = parseReceiveSettings(currentLine);
				break;
			}

			case DECODED_SPOT_LINE: 
			{
				spot = parseDecodedSpot(currentLine);
				if(spot == null){
					LOGGER.error("Could not parse spot at line: " + lineCount);
				}
				else{
					spot.setRxFrequency(this.spotHeader.getRxFrequency());
					try {
						spot.setSpotReceivedTimestamp(
								DateTimeUtils.createXMLGregorianFromLocalDateTime(
										this.spotHeader.getDate(),
										spot.getTime()));
	
						spot.setSpotter(this.getSpotterCallsign());
						// add spot to list ready to be uploaded
						spots.add(spot);
	
					} catch (DatatypeConfigurationException e) {
						LOGGER.fatal("Failed to parse spot date and time to create Spot!", e);
					}
					
					// update timestamp for last spot parsed
					this.updateLastLogLineParsedTime(spot.getTime());
	
					break;
				}
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

		if (logLineType != null) {
			// update last log line processed datetime
			
			boolean v1DateFormat = false;
			if(Pattern.compile("\\d{4}-\\w{3}-\\d{2}").matcher(this.spotHeader.getDate()).find()) {
				v1DateFormat = true;
			}
			
			//TODO: refactor this block
			
			if (logLineType.equals(LogLineType.NEW_DAY_HEADER_LINE)) {
				//TODO: the strip in this part is the only part that's different
				// strip ":" from time in new day header line in log
				if(v1DateFormat) {
					this.lastLogParsedDateTime = DateTimeUtils.parseDateAndTime(
							this.spotHeader.getDate(), this.spotHeader.getTime()
									.replace(":", ""));
				}
				else {
					this.lastLogParsedDateTime = DateTimeUtils.parseDateAndTimeV2(
							this.spotHeader.getDate(), this.spotHeader.getTime()
									.replace(":", ""));
					
				}
			} else if (logLineType.equals(LogLineType.DECODED_SPOT_LINE)) {
				if(v1DateFormat) {
					this.lastLogParsedDateTime = DateTimeUtils.parseDateAndTime(
							this.spotHeader.getDate(), spot.getTime());
				}
				else {
					this.lastLogParsedDateTime = DateTimeUtils.parseDateAndTimeV2(
							this.spotHeader.getDate(), spot.getTime());
				}
			}

			// send parsed spots to endpoint for storage
			if (spots != null && spots.size() > 0) {
				SpotCollectorEndpoint endpoint = null;

				if (this.localTestMode) {
					SpotCollectorEndpointService service = new SpotCollectorEndpointService(new URL(
							"http://localhost:8080/SpotCollectorEndpoint?wsdl"),
					new QName(
							"http://endpoint.spotcollector.callsign.kh/",
							"SpotCollectorEndpointService"));
					endpoint = service.getSpotCollectorEndpointPort();

				} else {
					//TODO: change this to be property driven
					SpotCollectorEndpointService service = new SpotCollectorEndpointService(
							new URL(
									"http://callsignviz2-kjh.rhcloud.com/SpotCollectorEndpoint?wsdl"),
							new QName(
									"http://endpoint.spotcollector.callsign.kh/",
									"SpotCollectorEndpointService"));
					endpoint = service.getSpotCollectorEndpointPort();

					BindingProvider bindingProvider = (BindingProvider) endpoint;
					bindingProvider
							.getRequestContext()
							.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
									"http://callsignviz2-kjh.rhcloud.com/SpotCollectorEndpoint");
				}
				try{
					endpoint.storeSpots(spots);
				}
				catch(JMSException_Exception e){
					LOGGER.error("Failed to send spot data to endpoint!", e);
					
				}
			} else {
				LOGGER.info("No new spot data to send this time period...");
			}
		} else {
			LOGGER.info("No new spot data detected in log since last read, sleeping until next check");
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
		Spot spot = null;
		String currentLine = this.reader.nextLine();

		while (currentLine != null) {
			LogLineType type = this.identfyLineType(currentLine);

			// check if header line has been parsed yet
			if (type.equals(LogLineType.NEW_DAY_HEADER_LINE)) {
				this.spotHeader = parseReceiveSettings(currentLine);
				LocalDateTime currentHeaderDateTime = DateTimeUtils
						.parseDateAndTime(this.spotHeader.getDate(),
								this.spotHeader.getTime().replace(":", ""));
				if (lineHasNotBeenParsed(currentHeaderDateTime)) {
					break;
				}
			}
			else if (type.equals(LogLineType.NEW_DAY_HEADER_LINE_V2)) {
				this.spotHeader = parseReceiveSettings(currentLine);
				//append 00 on time here to make it match the same format as the spot lines
				LocalDateTime currentHeaderDateTime = DateTimeUtils
						.parseDateAndTimeV2(this.spotHeader.getDate(),
								this.spotHeader.getTime().replace(":", "").concat("00"));
				if (lineHasNotBeenParsed(currentHeaderDateTime)) {
					break;
				}
			}
			else if (type.equals(LogLineType.DECODED_SPOT_LINE)) {
				// check if individual spot line has been parsed yet
				spot = parseDecodedSpot(currentLine);

				LocalDateTime currentSpotDateTime = DateTimeUtils
						.parseDateAndTime(this.spotHeader.getDate(),
								spot.getTime());
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
			LOGGER.debug("...parsing header fields v1");
			String date = m.group(1);
			String time = m.group(2);
			String freq = m.group(3);
			LOGGER.debug("... date:[" + date + "] time:[" + time + "]" + " rxFreq:[" + freq + "]");
			receiveHeader = new ReceivedSpotHeader(date, time, freq);

		}
		else {
			Matcher m2 = NEW_LINE_HEADER_ALL_FIELDS_PATTERN_V2.matcher(currentLine);
			if (m2.find()) {
				LOGGER.debug("...parsing header fields v2");
				String date = m2.group(1);
				String time = m2.group(2);
				String freq = m2.group(3);
				LOGGER.debug("... date:[" + date + "] time:[" + time + "]" + " rxFreq:[" + freq + "]");
				receiveHeader = new ReceivedSpotHeader(date, time, freq);

			}
			else {
				LOGGER.error("could not parse header fields");
			}
		}

		return receiveHeader;
	}

	public Spot parseDecodedSpot(String currentLine) {
		Spot spot = null;
		Matcher m = DECODED_SPOT_ALL_FIELDS_PATTERN.matcher(currentLine);
		if (m.find()) {
			LOGGER.info("...parsing spot fields");
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
			try{
				spot = new Spot(DateTimeUtils.createXMLGregorianFromLocalDateTime(this.spotHeader.getDate(), time), this.spotterCallsign, 
						time, signalReport, timeDeviation, frequencyOffset, word1, word2, word3);
			}
			catch(DatatypeConfigurationException e){
				
				LOGGER.fatal("Failed to parse date and time for creating Spot", e);
			}
		}
//		else { 
//			Matcher m2 = DECODED_SPOT_ALL_FIELDS_PATTERN_.matcher(currentLine);
//		
//			if (m2.find()) {
//				LOGGER.info("...parsing spot fields");
//				// 1950 -21 0.2 1231 # CQ KN8J EM99
//				String time = m2.group(1);
//				String signalReport = m2.group(2);
//				String timeDeviation = m2.group(3);
//				String frequencyOffset = m2.group(4);
//				String word1 = m2.group(5);
//				String word2 = m2.group(6);
//				String word3 = m2.group(7);
//				LOGGER.info("... time:[" + time + "] signal:[" + signalReport
//						+ "] timeDev:[" + timeDeviation + "] freqoffset:["
//						+ frequencyOffset + "] word1:[" + word1 + "] word2:["
//						+ word2 + "] word3:[" + word3 + "]");
//				try{
//					spot = new Spot(DateTimeUtils.createXMLGregorianFromLocalDateTime(this.spotHeader.getDate(), time), this.spotterCallsign, time, signalReport, timeDeviation, frequencyOffset,
//							word1, word2, word3);
//				}
//				catch(DatatypeConfigurationException e){
//					
//					LOGGER.fatal("Failed to parse date and time for creating Spot", e);
//				}
//			}
//			else {
//				LOGGER.error("could not parse header fields");
//			}
//		}
		return spot;
	}

	public LogLineType identfyLineType(String line) {
		LogLineType type;

		Matcher headerMatcher = NEW_LINE_HEADER_PATTERN.matcher(line);
		if (headerMatcher.find()) {
			type = LogLineType.NEW_DAY_HEADER_LINE;
			LOGGER.info("log file line read, type: header line");
		} else {
			Matcher headerMatcherV2 = NEW_LINE_HEADER_PATTERN_V2.matcher(line);
			if (headerMatcherV2.find()) {
				type = LogLineType.NEW_DAY_HEADER_LINE_V2;
				LOGGER.info("log file line read, type: header line v2");
			} else {
				Matcher rxMatcher = DECODED_SPOT_PATTERN.matcher(line);
				if (rxMatcher.find()) {
					type = LogLineType.DECODED_SPOT_LINE;
					LOGGER.info("log file line read, type: rx spot line");
				} else {
					Matcher txMatcher = TX_PATTERN.matcher(line);
					if (txMatcher.find()) {
						type = LogLineType.TX_LINE;
						LOGGER.info("log file line read, type: tx line");
					} else {
						type = LogLineType.UNKNOWN_LINE;
						LOGGER.error("log file line read, type:  unknown line type");
					}
				}
			}
		}
		return type;
	}

	/**
	 * Initializes the current spotHeader with date info for current section of file
	 * @param headerLine
	 */
	public void initHeader(String headerLine){
		this.spotHeader = parseReceiveSettings(headerLine);
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

	public LocalDateTime getLastLogParsedDateTime() {
		return lastLogParsedDateTime;
	}

	void setLastLogParsedDateTime(LocalDateTime dateTime) {
		this.lastLogParsedDateTime = dateTime;
	}

	public String getSpotterCallsign() {
		return spotterCallsign;
	}

	public void setSpotterCallsign(String spotterCallsign) {
		this.spotterCallsign = spotterCallsign;
	}

}
