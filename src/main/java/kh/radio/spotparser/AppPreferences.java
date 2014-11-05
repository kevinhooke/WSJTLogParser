package kh.radio.spotparser;


public class AppPreferences {

	private long lastLogProcessedMillisUTC;

	public long getLastLogProcessedMillisUTC() {
		return lastLogProcessedMillisUTC;
	}

	public void setLastLogProcessedMillisUTC(long lastLogProcessed) {
		this.lastLogProcessedMillisUTC = lastLogProcessed;
	}
	
}
