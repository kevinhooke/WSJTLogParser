package kh.radio.spotparser;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

	private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MMM-dd kkmm").withZone(ZoneId.of("Z"));

	public static long dateTimeToMillisUTC(LocalDateTime dateTime){
		return dateTime.toEpochSecond(ZoneOffset.UTC);
	}
	
	public static LocalDateTime parseDateAndTime(String date, String time){
		StringBuilder combined = new StringBuilder();
		combined.append(date).append(" ").append(time);
		
		return LocalDateTime.parse(combined.toString(), formatter);
		
		
	}

	public static LocalDateTime updateTime(LocalDateTime lastLogParsedDateTime,
			String time) {
		String hour = time.substring(0, 2);
		String mins = time.substring(2, 4);
		int tmpHour = Integer.parseInt(hour);
		int tmpMins = Integer.parseInt(mins);
		
		return lastLogParsedDateTime.withHour(tmpHour).withMinute(tmpMins).withSecond(0);
	}
}