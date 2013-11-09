package org.thewheatfield.psi_brunei;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PSIReadingActivity extends Activity implements IDownloadPageCallback{
	
	PSIData myData = null;
	TextView tv = null;
	TextView tvDate = null;			
	TextView tvTime = null;			
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		myData = new PSIData(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_psireading);
		tv = (TextView)  findViewById(R.id.textView1);
		tvDate = (TextView)  findViewById(R.id.txtDate);
		tvTime = (TextView)  findViewById(R.id.txtTime);
	    if(Util.isCacheCurrent(myData)){
			tv.setText(getText(R.string.load_cache));
	    	updateUI();
	    }
	    else{
			tv.setText(getText(R.string.downloading));
	    	downloadUpdate();
	    }
	}
	private void downloadUpdate(){
		DownloadWebPageTask task = new DownloadWebPageTask();
		task.setCallback(this);
		task.execute(new String[] {  getText(R.string.URL_DATA).toString() });	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.psireading, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case R.id.action_refresh:
    		if(tv != null)
			tv.setText(getText(R.string.downloading));
    		downloadUpdate();
            return true;
        case R.id.action_go_to_site:
        	startActivity(new Intent( Intent.ACTION_VIEW , Uri.parse(getText(R.string.URL_DATA).toString())));
            return true;
        case R.id.action_download_pdf:
        	startActivity(new Intent( Intent.ACTION_VIEW , Uri.parse(getText(R.string.URL_PDF).toString())));
            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	

	// data has been retrieved
	// display on screen / update widget / save in cache	
	@Override
	public void process(String data) {
		Util.processData(data, myData);
		updateUI();		
	}

	
	private void updateUI(){
		TextView tv = (TextView)  findViewById(R.id.textView1);
		if(tv != null){
			StringBuilder sb = new StringBuilder();
			// sb.append(myData.getData("last_check") + "|" + myData.getData("last_update_widget"));
			sb.append(getText(R.string.last_updated) + ": " + myData.getData("last_check"));
			tvDate.setText(myData.getData("date"));
			tvTime.setText(myData.getData("time"));
			
			Map<String, ?> districts = myData.getDistrictData();
			ListView listL = (ListView) findViewById(R.id.listL);
			LinearLayout list = (LinearLayout) findViewById(R.id.list);
			list.removeAllViews();
			
			ArrayList<String> order = new ArrayList<String>(); 
			order.add("Brunei Muara");
			order.add("Belait");
			order.add("Temburong");
			order.add("Tutong");			 

			for(String s : order){
				@SuppressWarnings("unchecked")
				Map<String,String> district = (Map<String,String>) districts.get(s);
				if(district == null || district.keySet().size() == 0) continue;
				int color = getResources().getColor(R.color.fg);
				try
				{
					int psi = Integer.parseInt(district.get("psi"));
					if(psi < 51)
						color = getResources().getColor(R.color.good);
					else if (psi < 101)
						color = getResources().getColor(R.color.moderate);
					else
						color = getResources().getColor(R.color.bad);
				}catch(Exception e){
					
				}
		        View child = getLayoutInflater().inflate(R.layout.listitem, null);
		        
		        LinearLayout layout = (LinearLayout) child.findViewById(R.id.colDetails);
				layout.setBackgroundColor(color);
		        
		        TextView lbl = null;
		        lbl = (TextView) child.findViewById(R.id.txtDistrict);
		        lbl.setText(s);
		        lbl = (TextView) child.findViewById(R.id.txtAirQuality);
		        lbl.setText(district.get("air"));
		        // lbl.setBackgroundColor(color);
		        lbl = (TextView) child.findViewById(R.id.txtPSI);
		        lbl.setText(district.get("psi")); 
		        list.addView(child);
			}
			
			listL.setAdapter(new PSIAdapter(districts));
			
			tv.setText(sb.toString());			
		}			
	}
	public class PSIAdapter extends BaseAdapter {

		Map<String, ?> districts;
		ArrayList<String> order = new ArrayList<String>();
		public PSIAdapter(Map<String,?> districts){
			this.districts = districts;
			order.add("Brunei Muara");
			order.add("Belait");
			order.add("Temburong");
			order.add("Tutong");			 
		}
		@Override
		public int getCount() {
			return districts.keySet().size();
		}

		@Override
		public Object getItem(int position) {
			return districts.get(order.get(position));
		}


		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			if (convertView == null)
				vi = getLayoutInflater().inflate(R.layout.listitem, null);
			

			Map<String,String> district = (Map<String, String>) getItem(position);
			if(district == null || district.keySet().size() == 0) return vi;
			int color = getResources().getColor(R.color.fg);
			try
			{
				int psi = Integer.parseInt(district.get("psi"));
				if(psi < 51)
					color = getResources().getColor(R.color.good);
				else if (psi < 101)
					color = getResources().getColor(R.color.moderate);
				else
					color = getResources().getColor(R.color.bad);
			}catch(Exception e){
				
			}
	        LinearLayout layout = (LinearLayout) vi.findViewById(R.id.colDetails);
			layout.setBackgroundColor(color);
	        TextView lbl = null;
	        lbl = (TextView) vi.findViewById(R.id.txtDistrict);
	        lbl.setText(order.get(position));
	        lbl = (TextView) vi.findViewById(R.id.txtAirQuality);
	        lbl.setText(district.get("air"));
	        // lbl.setBackgroundColor(color);
	        lbl = (TextView) vi.findViewById(R.id.txtPSI);
	        lbl.setText(district.get("psi")); 
			
			return vi;
		}
	}
	
}
