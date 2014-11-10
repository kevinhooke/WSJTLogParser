package kh.radio.spotparser;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

	private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MMM-DD kk:mm").withZone(ZoneId.of("Z"));
	
	public static long dateTimeToMillisUTC(LocalDateTime dateTime){
		return dateTime.toEpochSecond(ZoneOffset.UTC);
	}
	
	public static LocalDateTime parseDateAndTime(String date, String time){
		StringBuilder combined = new StringBuilder();
		combined.append(date).append(" ").append(time);
		
		return LocalDateTime.parse(combined.toString(), formatter);
		
		
	}
}
