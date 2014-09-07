package kh.radio.spotparser;

import java.util.Date;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kh.radio.spotparser.domain.DecodedSpot;
import kh.radio.spotparser.domain.ReceiveSettings;
import kh.radio.spotparser.domain.TXInfo;
import kh.radio.spotparser.wsjt.LogFileReader;
import kh.radio.spotparser.wsjt.LogLineType;

import org.apache.log4j.Logger;


public class LogParserTask extends TimerTask {

	private String filePath;
	private LogFileReader reader;
	
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
	}
	
	@Override
	public void run() {
		Date now = new Date();
		System.out.println("Starting file check at " + now);
		
		//org.apache.commons.io.input.ReversedLinesFileReader
	}
	
	public void parseAllLines(){
		int lineCount = 1;
		ReceiveSettings settings = null;
		
		LOGGER.info("readAllLines starting");
		String currentLine = this.reader.nextLine();
		
		while(currentLine != null){
			LOGGER.info("line "  + lineCount);
				
			DecodedSpot spot = null;
			TXInfo tx = null;
			
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
		
	}
	
	private TXInfo parseTx(String currentLine) {
		// TODO Auto-generated method stub
		return null;
	}


	public ReceiveSettings parseReceiveSettings(String currentLine) {
		ReceiveSettings receiveHeader = null;
		Matcher m = NEW_LINE_HEADER_ALL_FIELDS_PATTERN.matcher(currentLine);
		if(m.find()){
			LOGGER.info("parsing header fields");
			String date = m.group(1);
			String time = m.group(2);
			LOGGER.info("... date;[" + date + "] time:[" + time +  "]");
			receiveHeader = new ReceiveSettings(date, time);
			
		}
		else{
			LOGGER.error("could not parse header fields");
		}
		return receiveHeader;
	}

	public DecodedSpot parseDecodedSpot(String currentLine) {
		DecodedSpot spot = null;
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
			spot = new DecodedSpot(time, signalReport, timeDeviation, frequencyOffset, word1, word2, word3);
			
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
