package kh.radio.spotparser.domain;

public class DecodedSpot {

	private String time;
	private String signalreport;
	private String timeDeviation;
	private String frequencyOffset;
	private String word1;
	private String word2;
	private String word3;
	
	public DecodedSpot(String time, String signalreport, String timeDeviation,
			String frequencyOffset, String word1, String word2, String word3) {
		this.time = time;
		this.signalreport = signalreport;
		this.timeDeviation = timeDeviation;
		this.frequencyOffset = frequencyOffset;
		this.word1 = word1;
		this.word2 = word2;
		this.word3 = word3;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSignalreport() {
		return signalreport;
	}

	public void setSignalreport(String signalreport) {
		this.signalreport = signalreport;
	}

	public String getTimeDeviation() {
		return timeDeviation;
	}

	public void setTimeDeviation(String timeDeviation) {
		this.timeDeviation = timeDeviation;
	}

	public String getFrequencyOffset() {
		return frequencyOffset;
	}

	public void setFrequencyOffset(String frequencyOffset) {
		this.frequencyOffset = frequencyOffset;
	}

	public String getWord1() {
		return word1;
	}

	public void setWord1(String word1) {
		this.word1 = word1;
	}

	public String getWord2() {
		return word2;
	}

	public void setWord2(String word2) {
		this.word2 = word2;
	}

	public String getWord3() {
		return word3;
	}

	public void setWord3(String word3) {
		this.word3 = word3;
	}

	
	
}
