package kh.radio.spotparser;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateTimeUtils {

	private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MMM-dd kkmm").withZone(ZoneId.of("Z"));

	private static final DateTimeFormatter formatter_v2 =
            DateTimeFormatter.ofPattern("yyyy-MM-dd kkmmss").withZone(ZoneId.of("Z"));
	
	private static final DateTimeFormatter formatter_v210 =
            DateTimeFormatter.ofPattern("yyMMdd kkmmss").withZone(ZoneId.of("Z"));
	
	public static long dateTimeToMillisUTC(LocalDateTime dateTime){
		return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
		//return dateTime.toEpochSecond(ZoneOffset.UTC);
	}
	
	public static LocalDateTime parseDateAndTime(String date, String time){
		StringBuilder combined = new StringBuilder();
		combined.append(date).append(" ").append(time);
		
		return LocalDateTime.parse(combined.toString(), formatter);
	}

	public static LocalDateTime parseDateAndTimeV2(String date, String time){
		StringBuilder combined = new StringBuilder();
		combined.append(date).append(" ").append(time);
		
		return LocalDateTime.parse(combined.toString(), formatter_v2);
	}
	
	public static LocalDateTime parseDateAndTimeV210(String date, String time){
		StringBuilder combined = new StringBuilder();
		combined.append(date).append(" ").append(time);
		
		return LocalDateTime.parse(combined.toString(), formatter_v210);
	}
	
	
	public static LocalDateTime updateTime(LocalDateTime lastLogParsedDateTime,
			String time) {
		String hour = time.substring(0, 2);
		String mins = time.substring(2, 4);
		int tmpHour = Integer.parseInt(hour);
		int tmpMins = Integer.parseInt(mins);
		
		return lastLogParsedDateTime.withHour(tmpHour).withMinute(tmpMins).withSecond(0);
	}
	
	public static XMLGregorianCalendar createXMLGregorianFromLocalDateTime(String date, String time) throws DatatypeConfigurationException{
		
		//if date string has 3 character month like JAN, parse with original method parseDateAndTime()
		//otherwise if all numeric parse with parseDateAndTimeV2()
		LocalDateTime dateTime = null;
		//TODO: handling the old date format vs the new is the same here and in LogParserTask
		if(Pattern.compile("\\d{4}-\\w{3}-\\d{2}").matcher(date).find()) {
			dateTime = parseDateAndTime(date, time);
		}
		//if v2.1.0 date
		else if(date.length() == 6){
			dateTime = parseDateAndTimeV210(date, time);
		}
		else {
			dateTime = parseDateAndTimeV2(date, time);
		}

		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(
				dateTime.getYear(), dateTime.getMonthValue(),
				dateTime.getDayOfMonth(), dateTime.getHour(),
				dateTime.getMinute(), dateTime.getSecond(),
				dateTime.getNano(), 0);
		return xmlCal;
	}
}
