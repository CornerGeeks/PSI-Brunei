package org.thewheatfield.psi_brunei;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PSIData{
	public final static String SEPARATOR = "_";
	public final static String DISTRICT_PREFIX = "district" + SEPARATOR;
	String date = "";
	HashMap<String, String> data = new HashMap<String, String>();
    SharedPreferences settings = null;
    SharedPreferences.Editor editor = null;
	public PSIData(Activity a){
		settings = a.getSharedPreferences("DATA", android.content.Context.MODE_PRIVATE);
		editor = settings.edit();
		Map<String, ?> map = settings.getAll();
	    for(String s : map.keySet()){
	    	data.put(s, map.get(s).toString());
	    }		    
	    
	}
	public PSIData(Context c){
		settings = c.getSharedPreferences("DATA", android.content.Context.MODE_PRIVATE);
		editor = settings.edit();
		Map<String, ?> map = settings.getAll();
	    for(String s : map.keySet()){
	    	data.put(s, map.get(s).toString());
	    }		    
	    
	}
	public void save(String var, String value){
		if(settings == null) return;
	    editor.putString(var, value);
	    editor.commit();
	}
	public void saveRowData(String var, Map<String,String> value){
		if(settings == null) return;
		for(String s : value.keySet()){
		    editor.putString(var + SEPARATOR + s, value.get(s).toString());
		}
	    editor.commit();
	}
	public void save(){
	    for(String s : data.keySet()){
	    	editor.putString(s, data.get(s).toString());
	    }		    
	    editor.commit();
	}
	
	public String getData(String d){
		if(data.get(d) == null) return "";
		return data.get(d).toString();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Map> getDistrictData(){
		HashMap<String, Map> districts = new HashMap<String, Map>();
		SortedSet<String> keys = new TreeSet<String>(data.keySet());
	    for(String s : keys){
	    	if(s.startsWith(DISTRICT_PREFIX)){
	    		String[] parts = s.split(SEPARATOR);
	    		String districtName = parts[1];
	    		String key = parts[2];
	    		if(!districts.containsKey(districtName )){
	    			HashMap<String, HashMap> district = new HashMap<String, HashMap>();
	    			districts.put(districtName , district);
	    		}
    			districts.get(districtName).put(key, data.get(s).toString());
	    	}
	    }		    
		return districts;
	}
	public void setData(String d, String v){
		data.put(d, v);
	}
	public void setRowData(String var, Map<String,String> value){
	    for(String s : value.keySet()){
	    	data.put(var + SEPARATOR + s, value.get(s).toString());
	    }		    
	}
}