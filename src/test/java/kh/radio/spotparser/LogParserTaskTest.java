package kh.radio.spotparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import kh.radio.spotparser.wsjt.LogFileReader;

import org.junit.Test;

public class LogParserTaskTest {

	private static final String HEADER_set1_1 = "2014-Jul-07 19:40  14.076 MHz  JT9+JT65\n";
	private static final String LINE_set1_2 = "1950 -21  0.2 1231 # CQ KN8J EM99\n";
	private static final String LINE_set1_3 = "1952 -19  0.2 1231 # CQ KN8J EM99\n";
	private static final String LINE_set1_4 = "2001 -19  0.2 1231 # CQ KN8J EM99\n";
	

	private LogParserTask createTestTask(String lines){
		LogParserTask task = new LogParserTask();
		task.setLastLogParsedPreferencesKey(LogParserTask.LAST_LOG_PARSED_TEST);
		task.initPreferences();
				

		
		LogFileReader reader = new LogFileReader();
		reader.setReader(new StringReader(lines));
		task.setReader(reader);
		return task;
	}

	@Test
	public void testParseOneLine() throws IOException {

		StringBuilder sb = new StringBuilder();
		sb.append(HEADER_set1_1);
		sb.append(LINE_set1_2);

		LogParserTask task = this.createTestTask(sb.toString());
		
		int linesProcessed = task.parseAllLines();
		assertTrue("Expecting 1 line processed", linesProcessed == 1);
	}

	@Test
	public void testParseTwoLines() throws IOException {

		StringBuilder sb = new StringBuilder();
		sb.append(HEADER_set1_1);
		sb.append(LINE_set1_2);
		sb.append(LINE_set1_3);
		

		LogParserTask task = this.createTestTask(sb.toString());
		
		int linesProcessed = task.parseAllLines();
		assertTrue("Expecting 2 line processed", linesProcessed == 2);
	}

	@Test
	public void testParse2LinesAdd1Line() throws IOException {

		StringBuilder sb = new StringBuilder();
		sb.append(HEADER_set1_1);
		sb.append(LINE_set1_2);
		sb.append(LINE_set1_3);
		

		LogParserTask task = this.createTestTask(sb.toString());
		
		int linesProcessed = task.parseAllLines();
		assertTrue("Expecting 2 line processed", linesProcessed == 2);
		
		sb.append(LINE_set1_4);
		task = this.createTestTask(sb.toString());
		
		linesProcessed = task.parseAllLines();
		assertTrue("Expecting only 1 line processed", linesProcessed == 1);

		//TODO: work out how to update the string being parsed by BufferedReader so we can add new lines to it here
		
	}
	
	
}
