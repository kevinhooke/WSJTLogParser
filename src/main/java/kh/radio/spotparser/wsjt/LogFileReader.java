package kh.radio.spotparser.wsjt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Wrapper for reading log files. Initialize prior to use by calling resetToStartOfFile().
 * 
 * @author kevin.hooke
 *
 */
public class LogFileReader {

	private String path;
	private Reader reader;
	private BufferedReader bufferedReader;
	
	public LogFileReader(){
		
	}
	
	/**
	 * Initialized LogFileReader with a path to a physical file.
	 * @param path
	 * @throws FileNotFoundException
	 */
	public LogFileReader(String path) throws FileNotFoundException {
		this.path = path;
		this.reader = new FileReader(new File(this.path));
	}
	
	/**
	 * Reopens the file and resets to read the first line.
	 * @return
	 */
	public void resetToStartOfFile() throws FileNotFoundException {
		this.bufferedReader = new BufferedReader(this.reader);
	}
	
	/**
	 * Reads next line from the log. If BufferedReader not yet initialized, initializes 
	 * before first read. Otherwise assumes we're already initialized and we'll 
	 * continue reading from the current file position.
	 * @return
	 * @throws FileNotFoundException 
	 */
	public String nextLine() throws FileNotFoundException{
		
		if(this.bufferedReader == null){
			this.resetToStartOfFile();
		}
		
		String line = null;
		
		try {
			line = this.bufferedReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return line;
	}

	public Reader getReader() {
		return reader;
	}

	public void setReader(Reader reader) {
		this.reader = reader;
	}
	
	
}
