package kh.radio.spotparser.wsjt;

import junit.framework.TestCase;
import kh.radio.spotcollector.client.generated.Spot;
import kh.radio.spotparser.LogParserTask;
import kh.radio.spotparser.domain.ReceivedSpotHeader;

import org.junit.Test;


public class LogfileReader_v210_Test {
	
	private LogFileReader reader;
	
	@Test
	public void testReadFirstLine_v210() throws Exception{
		//TODO: need to load a test file from classpath
		this.reader = new LogFileReader("./src/test/resources/ALL-2019-v2.1.0-test.TXT");
		
		String line = this.reader.nextLine();
		TestCase.assertEquals("190908_052515    14.074 Rx FT8    -18  0.3 1304 W1AW N1AW +03", line);
		System.out.println(line);
	}

	@Test
	public void testIdentifyFirstLine_v210() throws Exception{
		this.reader = new LogFileReader("./src/test/resources/ALL-2019-v2.1.0-test.TXT");
		
		String line = this.reader.nextLine();
		LogParserTask task = new LogParserTask("./src/test/resources/ALL-2019-v2.1.0-test.TXT",
				true, "test", 0, 0, 0);
		LogLineType type = task.identfyLineType(line);
		
		TestCase.assertEquals(LogLineType.DECODED_SPOT_LINE_v210, type);
	}
	
	@Test
	public void testHeaderFieldsRegex_v210() throws Exception{
		LogParserTask task = new LogParserTask();
		ReceivedSpotHeader header = task.parseReceiveSettings("190908_052515    14.074 Rx FT8    -18  0.3 1304 W1AW N1AW +03");
		
		TestCase.assertEquals("052515", header.getTime());
		TestCase.assertEquals("190908", header.getDate());
	}

	@Test
	public void testHeaderFieldsRegex_v210_responseLine() throws Exception{
		LogParserTask task = new LogParserTask();
		ReceivedSpotHeader header = task.parseReceiveSettings("190908_052545     7.074 Rx FT8    -17  0.5 1042 W1AW N1AW DN57");
		
		TestCase.assertEquals("052545", header.getTime());
		TestCase.assertEquals("190908", header.getDate());
	}
	
	/**
	 * Test CQ spot v2.1.0
	 */
	@Test
	public void testSpotFieldsRegex_CQ__v210(){
		LogParserTask task = new LogParserTask();
		//no call to task.initHeader() for v2.1.0 log files
		Spot spot = task.parseDecodedSpot("190908_052915     7.074 Rx FT8    -13  0.7 1304 CQ W1AW BP51");

		TestCase.assertNotNull(spot);
		TestCase.assertEquals("052915", spot.getTime());
		TestCase.assertEquals("-13", spot.getSignalreport());
		TestCase.assertEquals("1304", spot.getFrequencyOffset());
		TestCase.assertEquals("0.7", spot.getTimeDeviation());
		TestCase.assertEquals("CQ", spot.getWord1());
		TestCase.assertEquals("W1AW", spot.getWord2());
		TestCase.assertEquals("BP51", spot.getWord3());
	}
	
	/**
	 * Test CQ response v2.1.0 : 222915 -15  0.5  758 ~  W1AW N1AW AA11 
	 */
	@Test
	public void testSpotFieldsRegex_cqResponse_v2(){
		LogParserTask task = new LogParserTask();
		
		//no call to task.initHeader() for v2.1.0 log files
		Spot spot = task.parseDecodedSpot("190908_052545     7.074 Rx FT8    -17  0.5 1042 W1AW N1AW AA11");
		
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("052545", spot.getTime());
		TestCase.assertEquals("-17", spot.getSignalreport());
		TestCase.assertEquals("1042", spot.getFrequencyOffset());
		TestCase.assertEquals("0.5", spot.getTimeDeviation());
		TestCase.assertEquals("W1AW", spot.getWord1());
		TestCase.assertEquals("N1AW", spot.getWord2());
		TestCase.assertEquals("AA11", spot.getWord3());
	}    

	/**
	 * Test CQ response v2.1.0: 190908_052515    14.074 Rx FT8    -18  0.3 1304 W1AW N1AW +03
	 */
	@Test
	public void testSpotFieldsRegex_signalResponse_v210(){
		LogParserTask task = new LogParserTask();
		//no call to task.initHeader() for v2.1.0 log files
		
		Spot spot = task.parseDecodedSpot("190908_052515    14.074 Rx FT8    -18  0.3 1304 W1AW N1AW +03");
		
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("052515", spot.getTime());
		TestCase.assertEquals("-18", spot.getSignalreport());
		TestCase.assertEquals("1304", spot.getFrequencyOffset());
		TestCase.assertEquals("0.3", spot.getTimeDeviation());
		TestCase.assertEquals("W1AW", spot.getWord1());
		TestCase.assertEquals("N1AW", spot.getWord2());
		TestCase.assertEquals("+03", spot.getWord3());
	} 
	

}
