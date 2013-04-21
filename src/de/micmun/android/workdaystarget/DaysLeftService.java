/**
 * Copyright 2013 MicMun
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
import java.util.Locale;

import org.json.JSONException;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
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
		ComponentName cName = new ComponentName(this, DaysLeftProvider.class);
		int[] appIds = appManager.getAppWidgetIds(cName);

		DayCalculator dayCalc = new DayCalculator();

		for (int appId : appIds) {
			Log.d(TAG, "Widget: " + appId);
			PrefManager pm = new PrefManager(this, appId);
			Calendar target = pm.getTarget();
			boolean[] chkDays = pm.getCheckedDays();
			int days = 0;
			try {
				days = dayCalc.getDaysLeft(target.getTime(), chkDays);
			} catch (JSONException e) {
				Log.e(TAG, "ERROR holidays: " + e.getLocalizedMessage());
			}
			Log.d(TAG, "Days: " + days);
			RemoteViews rv = new RemoteViews(this.getPackageName(),
					R.layout.appwidget_layout);
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
			String targetStr = df.format(target.getTime());
			rv.setTextViewText(R.id.target, targetStr);
			String dayStr = String.format(Locale.getDefault(), "%3d", days);
			rv.setTextViewText(R.id.dayCount, dayStr);
			appManager.updateAppWidget(appId, rv);
		}
	}
}
