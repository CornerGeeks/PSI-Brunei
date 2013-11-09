package org.thewheatfield.psi_brunei;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	public static Map<String,String> extractDistrictRow(String row){
		HashMap<String,String> data = new HashMap<String,String>();
		Pattern pattern = Pattern.compile("([A-Za-z]+)(\\d+)");
	    Matcher matcher = pattern.matcher(row);
	    while (matcher.find()) {
	    	if(matcher.groupCount() == 2){
		        data.put("air", Util.capitalize(matcher.group(1)));
		        data.put("psi", matcher.group(2));
	    	}
		}
		return data;
	}
	public static void processData(String data, PSIData myData){
	    String date = Util.getDateAsSeenOnWebsite();
		String time = "";
		if(data != null){
			data = android.text.Html.fromHtml(data).toString();
			date = Util.extract(data, "date:", "time:");
			time = Util.extract(data, "time:", "district");
			Map<String,String> rowData;
			rowData = Util.extractDistrictRow(Util.extract(data, "brunei muara", "belait"));
			myData.setRowData(PSIData.DISTRICT_PREFIX + "Brunei Muara", rowData);
			rowData = Util.extractDistrictRow(Util.extract(data, "belait", "temburong"));
			myData.setRowData(PSIData.DISTRICT_PREFIX + "Belait", rowData);
			rowData = Util.extractDistrictRow(Util.extract(data, "temburong", "tutong"));
			myData.setRowData(PSIData.DISTRICT_PREFIX + "Temburong", rowData);
			rowData = Util.extractDistrictRow(Util.extract(data, "tutong", "view"));
			myData.setRowData(PSIData.DISTRICT_PREFIX + "Tutong", rowData);			 
		    myData.setData("date", date);
		    myData.setData("time", time);
		    myData.setData("last_check", Util.getFullDate());
		    myData.save();
		}		
	}
	public static String extract(String dataNormalCase, String startWordLowerCase, String endWordLowerCase){
		dataNormalCase = dataNormalCase.replace("\t", "").replace("\r", "").replace("\n", "");


		String dataLowerCase = dataNormalCase.toLowerCase();
		int start = dataLowerCase.indexOf(startWordLowerCase);
		if(start == -1) return "";
		int end = dataLowerCase.indexOf(endWordLowerCase, start);
		if(end == -1) return "";
		
		return dataNormalCase.substring(start+startWordLowerCase.length(),end);
	}
	public static String capitalize(String line)
	{
		return Character.toUpperCase(line.charAt(0)) + line.toLowerCase().substring(1);
	}
	public static String getDateAsSeenOnWebsite(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd / MM / yyyy");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return dateFormat.format(new Date());	
	}
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
	public static String getFullDate(){
		return dateFormat.format(new Date());	
	}
	public static Date parseFullDate(String date){
		try{
			return dateFormat.parse(date);
		}catch(Exception e){
			return null;
		}
	}
	public static Boolean isCacheCurrent(PSIData data){
		String last_downloaded = data.getData("last_check");
		try{
			Date last_downloaded_date = Util.parseFullDate(last_downloaded);
			int diffInHours = (int) ((new Date()).getTime() - last_downloaded_date.getTime()) / (1000 * 60 * 60);
			if(diffInHours == 0){
				return true;
			}
		}
		catch(Exception e){
		}
		return false;
	}
	
}
