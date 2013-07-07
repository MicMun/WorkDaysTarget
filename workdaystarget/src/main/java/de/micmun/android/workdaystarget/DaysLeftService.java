/**
 * Copyright 2013 by Michael Munzert
 * 
 * This program is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU >General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or >
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; >without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. >See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see >http://www.gnu.org/licenses/.
 */

package de.micmun.android.workdaystarget;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Service to handle updates.
 * 
 * @author MicMun
 * @version 1.0, 18.03.2013
 * 
 */
public class DaysLeftService extends IntentService {
	private static final String TAG = "DaysLeftService";

	/**
	 * Action for intent, which starts the service.
	 */
	public static final String ACTION_UPDATE = "de.micmun.android.workdaystarget.ACTION_UPDATE";

	/**
	 * Creates a new DaysLeftService.
	 */
	public DaysLeftService() {
		super(TAG);
	}

	/**
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent arg0) {
		if (arg0.getAction().equals(ACTION_UPDATE)) {
			updateDays();
		}
	}

	/**
	 * Updates the days to target.
	 * 
	 * @param now
	 *           Calendar of the current date.
	 */
	private void updateDays() {
		AppWidgetManager appManager = AppWidgetManager.getInstance(this);
		ComponentName cName = new ComponentName(getApplicationContext(),
				DaysLeftProvider.class);
		int[] appIds = appManager.getAppWidgetIds(cName);

		DayCalculator dayCalc = new DayCalculator();
		if (!isOnline()) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				Log.e(TAG, "Interrupted: " + e.getLocalizedMessage());
			}
		}

		for (int appId : appIds) {
			PrefManager pm = new PrefManager(this, appId);
			Calendar target = pm.getTarget();
			boolean[] chkDays = pm.getCheckedDays();
			int days = pm.getLastDiff();

			if (isOnline()) {
				try {
					days = dayCalc.getDaysLeft(target.getTime(), chkDays);
					Map<String, Object> saveMap = new HashMap<String, Object>();
					Long diff = Long.valueOf(days);
					saveMap.put(PrefManager.KEY_DIFF, diff);
					pm.save(saveMap);
				} catch (JSONException e) {
					Log.e(TAG, "ERROR holidays: " + e.getLocalizedMessage());
				}
			} else {
				Log.e(TAG, "No internet connection!");
			}
			RemoteViews rv = new RemoteViews(this.getPackageName(),
					R.layout.appwidget_layout);
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
			String targetStr = df.format(target.getTime());
			rv.setTextViewText(R.id.target, targetStr);
			String dayStr = String.format(Locale.getDefault(), "%d %s", days,
					getResources().getString(R.string.unit));
			rv.setTextViewText(R.id.dayCount, dayStr);

			// put widget id into intent
			Intent configIntent = new Intent(this, ConfigActivity.class);
			configIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			configIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appId);
			configIntent.setData(Uri.parse(configIntent
					.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent pendIntent = PendingIntent.getActivity(this, 0,
					configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.widgetLayout, pendIntent);

			// update widget
			appManager.updateAppWidget(appId, rv);
		}
	}

	/**
	 * Returns <code>true</code>, if you are connected to the internet.
	 * 
	 * @return <code>true</code>, if connected to the internet.
	 */
	private boolean isOnline() {
		boolean ret = false;
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();

		if (ni != null && ni.isConnected() && !ni.isRoaming())
			ret = true;

		return ret;
	}
}
