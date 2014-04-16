package it.gaiacri.mobile.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import it.gaiacri.mobile.R;

@SuppressLint("SimpleDateFormat")
public class DateUtils {
	
	public static String Month(int i,Context context) {
		switch(i){
		case 0: return context.getString(R.string.month_jan);
		case 1: return context.getString(R.string.month_feb);
		case 2: return context.getString(R.string.month_mar);
		case 3: return context.getString(R.string.month_apr);
		case 4: return context.getString(R.string.month_may);
		case 5: return context.getString(R.string.month_jun);
		case 6: return context.getString(R.string.month_jul);
		case 7: return context.getString(R.string.month_aug);
		case 8: return context.getString(R.string.month_sep);
		case 9: return context.getString(R.string.month_opt);
		case 10: return context.getString(R.string.month_nov);
		case 11: return context.getString(R.string.month_dec);
		}
		return "";
	}

	public static String DayWeek(int i,Context context) {
		switch(i){
		case 1: return context.getString(R.string.day_sunday);
		case 2: return context.getString(R.string.day_monday);
		case 3: return context.getString(R.string.day_tuesday);
		case 4: return context.getString(R.string.day_wednesday);
		case 5: return context.getString(R.string.day_thursday);
		case 6: return context.getString(R.string.day_friday);
		case 7: return context.getString(R.string.day_saturday);
		}
		return "";
	}
	
	public static String getDate(String start,Context context){
		String temp="";
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss"); 
		try {
			Date date = dt.parse(start);
			Calendar c=Calendar.getInstance();
			c.setTime(date);
			temp=createDate(c,context);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e("date",e.getMessage());
		} 
		return temp;
	}
	
	public static String getDate(long time, Context context) {
		String temp="";
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time*1000);
		temp=createDate(c,context);
		return temp;
	}
	public static String getOraDate(String start,Context context){
		String temp="";
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss"); 
		try {
			Date date = dt.parse(start);
			Calendar c=Calendar.getInstance();
			c.setTime(date);
			temp=createOraDate(c,context);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e("date",e.getMessage());
		} 
		return temp;
	}
	

	private static String Number(int num){
		if(num<10)
			return "0"+num;
		else
			return ""+num;
	}

	private static String createDate(Calendar c,Context context){
		return DateUtils.DayWeek(c.get(Calendar.DAY_OF_WEEK),context)+" "+c.get(Calendar.DAY_OF_MONTH)+ " "+ DateUtils.Month(c.get(Calendar.MONTH),context)+" "+ createOraDate(c,context);
	}
	private static String createOraDate(Calendar c,Context context){
		return c.get(Calendar.HOUR_OF_DAY) +":"+ Number(c.get(Calendar.MINUTE));
	}
}
