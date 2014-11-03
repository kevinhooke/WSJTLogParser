package kh.radio.spotparser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class LogParserTask extends TimerTask {

	//persisted preferences, eg for time of last log parse
	private AppPreferences appPrefs;
	private String lastLogParsedDate;
	
	private String filePath;
	private LogFileReader reader;
	
	private static final Logger LOG = LogManager.getLogger("kh.radio.spotparser");
	
	//log line type: new date reception starting header
	//LogLineType.NEW_DAY_HEADER_LINE
	private static final Pattern NEW_LINE_HEADER_PATTERN = Pattern.compile("\\d{4}-\\w{3}-\\d{2}");
	//2014-Jul-07 19:40  14.076 MHz  JT9+JT65
	private static final Pattern NEW_LINE_HEADER_ALL_FIELDS_PATTERN = Pattern.compile("(\\d{4}-\\w{3}-\\d{2})\\s+(\\d+:\\d+)\\s+(\\d+\\.\\d+\\s+)MHz");
	
	
	//log line type: decoded spot
	//LogLineType.DECODED_SPOT_LINE
	private static final Pattern DECODED_SPOT_PATTERN = Pattern.compile("\\d{4}[\\s]+-");
	//1950 -21  0.2 1231 # CQ KN8J EM99 
	private static final Pattern DECODED_SPOT_ALL_FIELDS_PATTERN = Pattern.compile("(\\d{4})\\s+([-]?\\d{1,2})\\s+([-]?\\d+\\.\\d)\\s+(\\d{1,4})\\s+#\\s+(\\w+)\\s+(\\w+)\\s+([-]?\\w+)");

	
	//log line type: my tx
	private static final Pattern TX_PATTERN = Pattern.compile("\\d{4}[\\s]+Transmitting");
	private static final Logger LOGGER = Logger.getLogger(LogParserTask.class);
	
	public LogParserTask(){
		
	}
	
	public LogParserTask(String filePath) throws Exception{
		this.filePath = filePath;
		this.reader = new LogFileReader(filePath);
		this.appPrefs = this.getPreferences();
	}
	
	@Override
	public void run() {
		LocalDateTime now = LocalDateTime.now();
		System.out.println("Starting file check at " + now);
		System.out.println("Last log parsed at " + this.lastLogParsedDate);
		
		//TODO: need to only check new lines since last run - need to record timestamp of last spot and update with last when complete
		
		//org.apache.commons.io.input.ReversedLinesFileReader
		
		//TODO: for now - check all lines... but this needs to be refactored to not do this every time
		this.parseAllLines();
		
		//TODO: persist prefs to file		
		//this.lastLogParsedDate =
	}
	
	
	private AppPreferences getPreferences() {

		AppPreferences tempPrefs = new AppPreferences();
		
		Preferences prefs = Preferences.userNodeForPackage( getClass() );
		this.lastLogParsedDate = prefs.get("lastLogParsed", "");
		
		//TODO: finish this
		
		return tempPrefs;
		
	}
	
	
	public void parseAllLines(){
		int lineCount = 1;
		ReceivedSpotHeader settings = null;
		SpotCollectorEndpointService service = new SpotCollectorEndpointService();
		SpotCollectorEndpoint endpoint = service.getSpotCollectorEndpointPort();
		
		List<Spot> spots = new ArrayList<>();

		LOGGER.info("parseAllLines starting");
		String currentLine = this.reader.nextLine();
		
		while(currentLine != null){
			LOGGER.info("line "  + lineCount);
				
			Spot spot = null;
			TXMessage tx = null;
			
			LogLineType type = this.identfyLineType(currentLine);
			switch(type){
				case NEW_DAY_HEADER_LINE:
				{
					settings = parseReceiveSettings(currentLine);
					break;
				}
	
				case DECODED_SPOT_LINE:
				{
					spot = parseDecodedSpot(currentLine);
					
					//TODO: store timestamp for last spot parsed
					
					//add spot to list ready to be uploaded
					spots.add(spot);
					break;
				}
				
				case TX_LINE:
				{
					tx = parseTx(currentLine);
					break;
				}
			
				case UNKNOWN_LINE:
				{
					LOGGER.error("Skipped line, could not identify");
					break;
				}
			}
			//read next line
			lineCount++;
			currentLine = this.reader.nextLine();
		}
		
		//send parsed spots to endpoint for storage
		if(spots!= null && spots.size() > 0){
			endpoint.storeSpots(spots);
		}
		else{
			LOG.info("No new spot data to send this time period...");
		}
		
	}
	
	private TXMessage parseTx(String currentLine) {
		// TODO Auto-generated method stub
		return null;
	}


	public ReceivedSpotHeader parseReceiveSettings(String currentLine) {
		ReceivedSpotHeader receiveHeader = null;
		Matcher m = NEW_LINE_HEADER_ALL_FIELDS_PATTERN.matcher(currentLine);
		if(m.find()){
			LOGGER.info("parsing header fields");
			String date = m.group(1);
			String time = m.group(2);
			LOGGER.info("... date;[" + date + "] time:[" + time +  "]");
			receiveHeader = new ReceivedSpotHeader(date, time);
			
		}
		else{
			LOGGER.error("could not parse header fields");
		}
		return receiveHeader;
	}

	public Spot parseDecodedSpot(String currentLine) {
		Spot spot = null;
		Matcher m = DECODED_SPOT_ALL_FIELDS_PATTERN.matcher(currentLine);
		if(m.find()){
			LOGGER.info("parsing spot fields");
			//1950 -21  0.2 1231 # CQ KN8J EM99 
			String time = m.group(1);
			String signalReport = m.group(2);
			String timeDeviation = m.group(3);
			String frequencyOffset = m.group(4);
			String word1 = m.group(5);
			String word2 = m.group(6);
			String word3 = m.group(7);
			LOGGER.info("... time:[" + time + "] signal:[" + signalReport +  "] timeDev:[" + timeDeviation 
					+  "] freqoffset:[" + frequencyOffset +  "] word1:[" + word1 +  "] word2:[" + word2 + "] word3:[" + word3 + "]");
			spot = new Spot(time, signalReport, timeDeviation, frequencyOffset, word1, word2, word3);
			
		}
		else{
			LOGGER.error("could not parse header fields");
		}
		return spot;
	}
	
	
	
	public LogLineType identfyLineType(String line){
		LogLineType type;
		
		Matcher headerMatcher = NEW_LINE_HEADER_PATTERN.matcher(line);
		if(headerMatcher.find()){
			type = LogLineType.NEW_DAY_HEADER_LINE;
			LOGGER.info("log file read: header line");
		}
		else{
			Matcher rxMatcher = DECODED_SPOT_PATTERN.matcher(line);
			if(rxMatcher.find()){
				type = LogLineType.DECODED_SPOT_LINE;
				LOGGER.info("log file read: rx spot line");
			}
			else{
				Matcher txMatcher = TX_PATTERN.matcher(line);
				if(txMatcher.find()){
					type = LogLineType.TX_LINE;
					LOGGER.info("log file read: tx line");
				}
				else{
					type = LogLineType.UNKNOWN_LINE;
					LOGGER.info("log file read: unknown");
				}
			}
				
		}
		return type;
	}

}
