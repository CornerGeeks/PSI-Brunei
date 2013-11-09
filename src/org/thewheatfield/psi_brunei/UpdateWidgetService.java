package org.thewheatfield.psi_brunei;


import java.util.ArrayList;
import java.util.Map;

import android.os.IBinder;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service implements IDownloadPageCallback {
	private static final String LOG = "org.thewheatfield.psi_brunei.widget.service";

	private Intent intent;
	private int startId;
	@Override
	public void onStart(Intent intent, int startId) {
		this.intent = intent;
		this.startId = startId;
		Log.i(LOG, "Called");
	    
		PSIData data = new PSIData(this.getApplicationContext());
		data.save("last_update_widget", Util.getFullDate());
		
		DownloadWebPageTask task = new DownloadWebPageTask();
		task.setCallback(this);
		task.execute(new String[] {  getText(R.string.URL_DATA).toString() });	
	}

	private void updateUI(){
	    
	    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
	        .getApplicationContext());

	    int[] allWidgetIds = intent
	        .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

	    ComponentName thisWidget = new ComponentName(getApplicationContext(),
	        PSIReadingWidgetProvider.class);
	    int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);
	    Log.w(LOG, "From Intent" + String.valueOf(allWidgetIds.length));
	    Log.w(LOG, "Direct" + String.valueOf(allWidgetIds2.length));

	    
		PSIData data = new PSIData(this.getApplicationContext());
	    for (int appWidgetId : allWidgetIds) {
	    	Context context = this.getApplicationContext();

			// Create an Intent to launch ExampleActivity
			Intent clickIntent = new Intent(context, PSIReadingActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					clickIntent, 0);
			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget);
			views.setOnClickPendingIntent(R.id.entireWidget, pendingIntent);
			views.setOnClickPendingIntent(R.id.wdgtDate, pendingIntent);
						
			// https://github.com/commonsguy/cw-advandroid/blob/b01438e7f0fed8f795ddec4be43066905f03d0cc/AppWidget/TwitterWidget/AndroidManifest.xml
			// ImageButton btn = (ImageButton) context.bindService(arg0, arg1, arg2).R.id.imgReload			
			
			// To update a label
			views.setTextViewText(R.id.wdgtDate, data.getData("date") + "\n@" + data.getData("time"));

			views.removeAllViews(R.id.listWidget);
			Map<String, Map> districts = data.getDistrictData();			
			ArrayList<String> order = new ArrayList<String>(); 
			order.add("Brunei Muara");
			order.add("Belait");
			order.add("Temburong");
			order.add("Tutong");			 

			for(String s : order){
				RemoteViews newView = new RemoteViews(context.getPackageName(), R.layout.listitem_widget);
				@SuppressWarnings("unchecked")
				Map<String,String> district = districts.get(s);				
				if(district == null || district.keySet().size() == 0) continue;
				// Healthy Moderate Unhealthy
				// green    orange  red
				// 0-50		50-100	100-xxx
				int color = context.getResources().getColor(R.color.fg);
				try
				{
					int psi = Integer.parseInt(district.get("psi"));
					if(psi < 51)
						color = context.getResources().getColor(R.color.good);
					else if (psi < 101)
						color = context.getResources().getColor(R.color.moderate);
					else
						color = context.getResources().getColor(R.color.bad);
				}catch(Exception e){
					
				}
				newView.setTextViewText(R.id.txtDistrict, s);
				newView.setTextViewText(R.id.txtPSI, district.get("psi"));
				newView.setTextColor(R.id.txtDistrict, color);
				newView.setTextColor(R.id.txtPSI, color);
				newView.setOnClickPendingIntent(R.id.txtDistrict, pendingIntent);
				newView.setOnClickPendingIntent(R.id.txtPSI, pendingIntent);
		        views.addView(R.id.listWidget, newView);

			}
			RemoteViews newView2 = new RemoteViews(context.getPackageName(), R.layout.listitem_footer_widget);
			newView2.setTextViewText(R.id.txtFooter, data.getData("last_update_widget"));
	        views.addView(R.id.listWidget, newView2);
			// Tell the AppWidgetManager to perform an update on the current app
			// widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
	    }
	    stopSelf();

	    super.onStart(intent, startId);
	    //super.onStartCommand(intent, ST, startId);
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	    return null;
	  }
	  
		// data has been retrieved
		// display on screen / update widget / save in cache	
		@Override
		public void process(String data) {
			PSIData myData = new PSIData(this.getApplicationContext());
			Util.processData(data, myData);
			updateUI();		
		}	  
}
