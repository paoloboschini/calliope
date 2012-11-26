package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateConverter {
	
	private static DateConverter instance;
	private SimpleDateFormat sdf;
	
	private DateConverter(){
		sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
	}
	
	public static DateConverter getInstance(){
		if (null == instance)
			instance = new DateConverter();
		return instance;
	}
	
	public String dateToString(Date d){
		return sdf.format(d);
	}
	
	public Date stringToDate(String s){
		try {
			return sdf.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Date intToDate(Date date, int hour, int min) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (hour == 24) {
			cal.add(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		} else {
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, min);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);			
		}
		return cal.getTime();
	}

}
