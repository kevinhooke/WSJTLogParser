package kh.radio.spotparser.wsjt;

import junit.framework.TestCase;
import kh.radio.spotcollector.client.generated.Spot;
import kh.radio.spotparser.LogParserTask;
import kh.radio.spotparser.domain.ReceivedSpotHeader;

import org.junit.Test;


public class LogfileReader_v200_Test {
	
	private LogFileReader reader;

	@Test
	public void testReadFirstLine_v2() throws Exception{
		//TODO: need to load a test file from classpath
		this.reader = new LogFileReader("./src/test/resources/ALL-2019.TXT");
		
		String line = this.reader.nextLine();
		TestCase.assertEquals("2017-12-03 00:32  14.076 MHz  JT9", line);
		System.out.println(line);
	}

	@Test
	public void testIdentifyFirstLine_v2() throws Exception{
		this.reader = new LogFileReader("./src/test/resources/ALL-2019.TXT");
		
		String line = this.reader.nextLine();
		LogParserTask task = new LogParserTask("./src/test/resources/ALL-2019.TXT",
				true, "test");
		LogLineType type = task.identfyLineType(line);
		
		TestCase.assertEquals(LogLineType.NEW_DAY_HEADER_LINE_V2, type);
	}

	/**
	 * Test CQ spot v2
	 */
	@Test
	public void testSpotFieldsRegex_CQ_v2(){
		LogParserTask task = new LogParserTask();
		task.initHeader("2017-12-03 00:32  14.076 MHz  JT9");
		Spot spot = task.parseDecodedSpot("223000 -12 -0.6  496 ~  CQ W1AW AA11");

		TestCase.assertNotNull(spot);
		TestCase.assertEquals("223000", spot.getTime());
		TestCase.assertEquals("-12", spot.getSignalreport());
		TestCase.assertEquals("496", spot.getFrequencyOffset());
		TestCase.assertEquals("-0.6", spot.getTimeDeviation());
		TestCase.assertEquals("CQ", spot.getWord1());
		TestCase.assertEquals("W1AW", spot.getWord2());
		TestCase.assertEquals("AA11", spot.getWord3());
	}
	
	/**
	 * Test CQ response v2 : 222915 -15  0.5  758 ~  W1AW N1AW AA11
	 */
	@Test
	public void testSpotFieldsRegex_cqResponse_v2(){
		LogParserTask task = new LogParserTask();
		task.initHeader("2017-12-03 00:32  14.076 MHz  JT9");
		Spot spot = task.parseDecodedSpot("222915 -15  0.5  758 ~  W1AW N1AW AA11");
		
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("222915", spot.getTime());
		TestCase.assertEquals("-15", spot.getSignalreport());
		TestCase.assertEquals("758", spot.getFrequencyOffset());
		TestCase.assertEquals("0.5", spot.getTimeDeviation());
		TestCase.assertEquals("W1AW", spot.getWord1());
		TestCase.assertEquals("N1AW", spot.getWord2());
		TestCase.assertEquals("AA11", spot.getWord3());
	}     

	/**
	 * Test CQ response v2: 222915 -19  0.2 1852 ~  W1AW N1AW R-12 
	 */
	@Test
	public void testSpotFieldsRegex_signalResponse_v2(){
		LogParserTask task = new LogParserTask();
		task.initHeader("2017-12-03 00:32  14.076 MHz  JT9");
		//TODo need to handle the optional R in signal report
		Spot spot = task.parseDecodedSpot("222915 -19  0.2 1852 ~  W1AW N1AW R-12");
		
		TestCase.assertNotNull(spot);
		TestCase.assertEquals("222915", spot.getTime());
		TestCase.assertEquals("-19", spot.getSignalreport());
		TestCase.assertEquals("1852", spot.getFrequencyOffset());
		TestCase.assertEquals("0.2", spot.getTimeDeviation());
		TestCase.assertEquals("W1AW", spot.getWord1());
		TestCase.assertEquals("N1AW", spot.getWord2());
		TestCase.assertEquals("R-12", spot.getWord3());
	} 

}
