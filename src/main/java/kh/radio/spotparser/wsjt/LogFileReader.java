package kh.radio.spotparser.wsjt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LogFileReader {

	private String path;
	private BufferedReader reader;
	
	public LogFileReader(){
		
	}
	
	public LogFileReader(String path) throws FileNotFoundException {
		this.path = path;
		File file = new File(this.path);
		this.reader = new BufferedReader(new FileReader(file));
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
