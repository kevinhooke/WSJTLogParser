package kh.radio.spotparser;

import com.beust.jcommander.Parameter;

public class SpotParserArguments {

	@Parameter(names = { "--mycall" }, description = "Your callsign (or callsign of operator's logfile you are parsing)")
	private String spotterCallsign;
	
	@Parameter(names = { "--localtest" }, description = "Run the app in local/test mode only (for development only)")
	private boolean localMode;

	@Parameter(names = { "--resetlog" }, description = "Rests 'log line read' to reprocess all log file entries on the next run")
	private boolean resetLog;

	@Parameter(names = { "--file" }, description = "Path to the WSJT log file to process and upload for visualization")
	private String pathToFile;
	
	public String getSpotterCallsign() {
		return spotterCallsign;
	}

	public void setSpotterCallsign(String spotterCallsign) {
		this.spotterCallsign = spotterCallsign;
	}

	public boolean isLocalMode() {
		return localMode;
	}

	public void setLocalMode(boolean localMode) {
		this.localMode = localMode;
	}

	public boolean isResetLog() {
		return resetLog;
	}

	public void setResetLog(boolean resetLog) {
		this.resetLog = resetLog;
	}

	public String getPathToFile() {
		return pathToFile;
	}

	public void setPathToFile(String pathToFile) {
		this.pathToFile = pathToFile;
	}

}
