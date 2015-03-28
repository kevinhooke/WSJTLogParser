package kh.radio.spotparser.wsjt;

import junit.framework.TestCase;
import kh.radio.spotcollector.client.generated.Spot;
import kh.radio.spotparser.LogParserTask;
import kh.radio.spotparser.domain.ReceivedSpotHeader;

import org.junit.Test;


public class LogfileReaderTest {
	
	private LogFileReader reader;
	
	@Test
	public void testReadFirstLine() throws Exception{
		//TODO: need to load a test file from classpath
		this.reader = new LogFileReader("/Users/kev/develop/AmateurRadioCallsignSpotHistory/CallsignSpotParser/CallsignSpotParser/src/test/resources/ALL.TXT");
		
		String line = this.reader.nextLine();
		TestCase.assertEquals("2014-Jul-07 19:40  14.076 MHz  JT9+JT65", line);
		System.out.println(line);
	}
	
	@Test
	public void testIdentifyFirstLine() throws Exception{
		this.reader = new LogFileReader("/Users/kev/develop/AmateurRadioCallsignSpotHistory/CallsignSpotParser/CallsignSpotParser/src/test/resources/ALL.TXT");
		
		String line = this.reader.nextLine();
		LogParserTask task = new LogParserTask("/Users/kev/develop/AmateurRadioCallsignSpotHistory/CallsignSpotParser/CallsignSpotParser/src/test/resources/ALL.TXT",
				true, "test");
		LogLineType type = task.identfyLineType(line);
		
		TestCase.assertEquals(LogLineType.NEW_DAY_HEADER_LINE, type);
	}
		
	@Test
	public void testHeaderFieldsRegex() throws Exception{
		LogParserTask task = new LogParserTask();
		ReceivedSpotHeader header = task.parseReceiveSettings("2014-Jul-07 19:40  14.076 MHz  JT9+JT65");
		
		TestCase.assertEquals("19:40", header.getTime());
		TestCase.assertEquals("2014-Jul-07", header.getDate());
	}
	
	/**
	 * Test CQ spot
	 */
	@Test
	public void testSpotFieldsRegex_CQ(){
		LogParserTask task = new LogParserTask();
		Spot spot = task.parseDecodedSpot("1950 -21  0.2 1231 # CQ KN8J EM99");
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("1950", spot.getTime());
		TestCase.assertEquals("-21", spot.getSignalreport());
		TestCase.assertEquals("1231", spot.getFrequencyOffset());
		TestCase.assertEquals("0.2", spot.getTimeDeviation());
		TestCase.assertEquals("CQ", spot.getWord1());
		TestCase.assertEquals("KN8J", spot.getWord2());
		TestCase.assertEquals("EM99", spot.getWord3());
	}

	/**
	 * Test CQ response
	 */
	@Test
	public void testSpotFieldsRegex_cqResponse(){
		LogParserTask task = new LogParserTask();
		Spot spot = task.parseDecodedSpot("1959 -16  1.5 1229 # KN8J KC2LM DM65");
		
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("1959", spot.getTime());
		TestCase.assertEquals("-16", spot.getSignalreport());
		TestCase.assertEquals("1229", spot.getFrequencyOffset());
		TestCase.assertEquals("1.5", spot.getTimeDeviation());
		TestCase.assertEquals("KN8J", spot.getWord1());
		TestCase.assertEquals("KC2LM", spot.getWord2());
		TestCase.assertEquals("DM65", spot.getWord3());
	}      

	/**
	 * Test CQ response: 2001 -18  1.5 1225 # KN8J KC2LM R-15
	 */
	@Test
	public void testSpotFieldsRegex_signalResponse(){
		LogParserTask task = new LogParserTask();
		Spot spot = task.parseDecodedSpot("1952 -20 -0.2 1231 # W9MDB KN8J -01");
		
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("1952", spot.getTime());
		TestCase.assertEquals("-20", spot.getSignalreport());
		TestCase.assertEquals("1231", spot.getFrequencyOffset());
		TestCase.assertEquals("-0.2", spot.getTimeDeviation());
		TestCase.assertEquals("W9MDB", spot.getWord1());
		TestCase.assertEquals("KN8J", spot.getWord2());
		TestCase.assertEquals("-01", spot.getWord3());
	}      

	
	
	
	@Test
	public void testTxFieldsRegex(){
		
	}
}
