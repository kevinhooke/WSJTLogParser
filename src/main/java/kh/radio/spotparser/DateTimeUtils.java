package kh.radio.spotparser;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeUtils {

	public static long dateTimeToMillisUTC(LocalDateTime dateTime){
		return dateTime.toEpochSecond(ZoneOffset.UTC);
	}
}
