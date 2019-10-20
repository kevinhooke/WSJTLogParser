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
	
	@Parameter(names = { "--showlastdate" }, description = "Outputs the date/time of last log line processes")
	private boolean showLastdate;
	
	@Parameter(names = { "--maxupload" }, description = "Maximum number of spots to upload before exiting")
	private int maxUploads = 0;
	
	@Parameter(names = { "--uploadsize" }, description = "Number of spots to upload before pausing")
	private int uploadsize = 0;
	
	@Parameter(names = { "--uploadpausetime" }, description = "Time in seconds to pause before uploading next block")
	private int uploadPauseTime = 0;
	
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

	public boolean isShowLastdate() {
		return showLastdate;
	}

	public void setShowLastdate(boolean showLastdate) {
		this.showLastdate = showLastdate;
	}

	public int getMaxUploads() {
		return maxUploads;
	}

	public void setMaxUploads(int maxUploads) {
		this.maxUploads = maxUploads;
	}

	public int getUploadsize() {
		return uploadsize;
	}

	public void setUploadsize(int uploadsize) {
		this.uploadsize = uploadsize;
	}

	public int getUploadPauseTime() {
		return uploadPauseTime;
	}

	public void setUploadPauseTime(int uploadPauseTime) {
		this.uploadPauseTime = uploadPauseTime;
	}

}
