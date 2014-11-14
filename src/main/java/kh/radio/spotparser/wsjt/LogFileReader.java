package kh.radio.spotparser.wsjt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Wrapper for reading log files. Initialize prior to use by calling resetToStartOfFile().
 * 
 * @author kevin.hooke
 *
 */
public class LogFileReader {

	private String path;
	private BufferedReader reader;
	
	public LogFileReader(){
		
	}
	
	public LogFileReader(String path) throws FileNotFoundException {
		this.path = path;
	}
	
	/**
	 * Reopens the file and resets to read the first line.
	 * @return
	 */
	public void resetToStartOfFile() throws FileNotFoundException {
		this.reader = new BufferedReader(new FileReader(new File(this.path)));
	}
	
	/**
	 * Reads next line from the log.
	 * @return
	 */
	public String nextLine(){
		String line = null;
		
		try {
			line = this.reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return line;
	}
	
	
}
