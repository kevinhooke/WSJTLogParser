package kh.radio.spotparser;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;

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

	private LogParserTask createTestTaskWithFile(String pathToFile) throws Exception{
		LogParserTask task = new LogParserTask(pathToFile, true);
		task.setLastLogParsedPreferencesKey(LogParserTask.LAST_LOG_PARSED_TEST);
		task.initPreferences();
		return task;
	}
	
	
	@Test
	public void testLastLogParsed(){
		LogParserTask task = new LogParserTask();
		
		//read curent last log parsed
		task.setLastLogParsedPreferencesKey(LogParserTask.LAST_LOG_PARSED_TEST);
		task.initPreferences();
		
		//read after init
		LocalDateTime dateTime = task.getLastLogParsedDateTime();
		System.out.println("read time from prefs: " + dateTime);
		
		//reset to now and persist
		LocalDateTime newDateTime = LocalDateTime.now();
		task.setLastLogParsedDateTime(newDateTime);
		task.storeLastLogLineProcessedTime();
		
		//re-read and check
		task.initPreferences();
		LocalDateTime dateTimeReRead = task.getLastLogParsedDateTime();
		System.out.println("re-read time from prefs: " + dateTimeReRead);
		System.out.println("original: " + dateTime);
		assertTrue(dateTimeReRead.compareTo(newDateTime) == 0);
		
		
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
	public void testParse2LinesAdd1Line() throws Exception {

		StringBuilder sb = new StringBuilder();
		sb.append(HEADER_set1_1);
		sb.append(LINE_set1_2);
		sb.append(LINE_set1_3);
		
		File testFile = File.createTempFile("LogParserTaskTest", "_testFile");

		BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
		writer.append(sb.toString());
		writer.flush();
		System.out.println("temp file: " + testFile.getAbsolutePath());
		LogParserTask task = this.createTestTaskWithFile(testFile.getAbsolutePath());
		
		//TODO: need need to mock out the endpoint so parsed lines are not actually sent to endpoint
		int linesProcessed = task.parseAllLines();
		assertTrue("Expecting 2 line processed", linesProcessed == 2);
		
		//write additional line to file
		writer.append(LINE_set1_4);
		writer.flush();
				
		linesProcessed = task.parseAllLines();
		
		assertTrue("Expecting only 1 newly added line processed", linesProcessed == 1);
	}
	
	
}
