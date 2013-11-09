package org.thewheatfield.psi_brunei;

import java.util.Arrays;
import java.util.Map;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

public class PSIReadingWidgetProvider extends AppWidgetProvider {
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
	    ComponentName thisWidget = new ComponentName(context,
	            PSIReadingWidgetProvider.class);
	        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	    Intent intent = new Intent(context.getApplicationContext(),
	        UpdateWidgetService.class);
	    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

	    // Update the widgets via the service
	    context.startService(intent);

	}
}
