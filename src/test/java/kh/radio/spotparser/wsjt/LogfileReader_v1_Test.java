package kh.radio.spotparser.wsjt;

import junit.framework.TestCase;
import kh.radio.spotcollector.client.generated.Spot;
import kh.radio.spotparser.LogParserTask;
import kh.radio.spotparser.domain.ReceivedSpotHeader;

import org.junit.Test;


public class LogfileReader_v1_Test {
	
	private LogFileReader reader;
	
	@Test
	public void testReadFirstLine_v1_2014Format() throws Exception{
		//TODO: need to load a test file from classpath
		this.reader = new LogFileReader("./src/test/resources/ALL-2014.TXT");
		
		String line = this.reader.nextLine();
		TestCase.assertEquals("2014-Jul-07 19:40  14.076 MHz  JT9+JT65", line);
		System.out.println(line);
	}

	/**
	 * new LogParserTask() with 2nd param = true requires local server to be up for a local
	 * integration test.
	 * 
	 * //TODO: why is this needed?
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIdentifyFirstLine_v1() throws Exception{
		this.reader = new LogFileReader("./src/test/resources/ALL-2014.TXT");
		
		String line = this.reader.nextLine();
		LogParserTask task = new LogParserTask("./src/test/resources/ALL-2014.TXT",
				true, "test", 0, 0, 0);
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
	 * Test CQ spot v1
	 */
	@Test
	public void testSpotFieldsRegex_CQ_v1(){
		LogParserTask task = new LogParserTask();
		task.initHeader("2014-Jul-07 19:40  14.076 MHz  JT9+JT65");
		Spot spot = task.parseDecodedSpot("1950 -21  0.2 1231 # CQ W1AW EM99");
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("1950", spot.getTime());
		TestCase.assertEquals("-21", spot.getSignalreport());
		TestCase.assertEquals("1231", spot.getFrequencyOffset());
		TestCase.assertEquals("0.2", spot.getTimeDeviation());
		TestCase.assertEquals("CQ", spot.getWord1());
		TestCase.assertEquals("W1AW", spot.getWord2());
		TestCase.assertEquals("EM99", spot.getWord3());
	}
	
	/**
	 * Test CQ response v1
	 */
	@Test
	public void testSpotFieldsRegex_cqResponse_v1(){
		LogParserTask task = new LogParserTask();
		task.initHeader("2014-Jul-07 19:40  14.076 MHz  JT9+JT65");
		Spot spot = task.parseDecodedSpot("1959 -16  1.5 1229 # W1AW N1ABC DM65");
		
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("1959", spot.getTime());
		TestCase.assertEquals("-16", spot.getSignalreport());
		TestCase.assertEquals("1229", spot.getFrequencyOffset());
		TestCase.assertEquals("1.5", spot.getTimeDeviation());
		TestCase.assertEquals("W1AW", spot.getWord1());
		TestCase.assertEquals("N1ABC", spot.getWord2());
		TestCase.assertEquals("DM65", spot.getWord3());
	}

	/**
	 * Test CQ response v1: 2001 -18  1.5 1225 # W1AW N1ABC R-15
	 */
	@Test
	public void testSpotFieldsRegex_signalResponse_v1(){
		LogParserTask task = new LogParserTask();
		task.initHeader("2014-Jul-07 19:40  14.076 MHz  JT9+JT65");
		Spot spot = task.parseDecodedSpot("1952 -20 -0.2 1231 # W1AW N1ABC -01");
		
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("1952", spot.getTime());
		TestCase.assertEquals("-20", spot.getSignalreport());
		TestCase.assertEquals("1231", spot.getFrequencyOffset());
		TestCase.assertEquals("-0.2", spot.getTimeDeviation());
		TestCase.assertEquals("W1AW", spot.getWord1());
		TestCase.assertEquals("N1ABC", spot.getWord2());
		TestCase.assertEquals("-01", spot.getWord3());
	}      

	@Test
	public void testKP4MD_2016_exampleSpotLine1() {
		LogParserTask task = new LogParserTask();
		task.initHeader("2016-Apr-20 02:26  7.076 MHz  JT9");
		Spot spot = task.parseDecodedSpot("0232 -10  0.9 1949 # AC8VV AC0TG DM79");
		
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("0232", spot.getTime());
		TestCase.assertEquals("-10", spot.getSignalreport());
		TestCase.assertEquals("1949", spot.getFrequencyOffset());
		TestCase.assertEquals("0.9", spot.getTimeDeviation());
		TestCase.assertEquals("AC8VV", spot.getWord1());
		TestCase.assertEquals("AC0TG", spot.getWord2());
		TestCase.assertEquals("DM79", spot.getWord3());
	}

	/**
	 * Tests log line from KP4MD 2016 ALL.TXT file: this line has a concatenated response in 2 cols
	 * instead of 3.
	 * 
	 */
	@Test
	public void testKP4MD_2016_exampleSpotLine2_withConcatResponse() {
		LogParserTask task = new LogParserTask();
		task.initHeader("2016-Apr-20 02:26  7.076 MHz  JT9");
		Spot spot = task.parseDecodedSpot("0232  -6  0.6 2124 # UR7ITU RRTU73");
		
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("0232", spot.getTime());
		TestCase.assertEquals("-6", spot.getSignalreport());
		TestCase.assertEquals("2124", spot.getFrequencyOffset());
		TestCase.assertEquals("0.6", spot.getTimeDeviation());
		TestCase.assertEquals("UR7ITU", spot.getWord1());
		TestCase.assertEquals("RRTU73", spot.getWord2());
		TestCase.assertEquals("", spot.getWord3());
	}

}
