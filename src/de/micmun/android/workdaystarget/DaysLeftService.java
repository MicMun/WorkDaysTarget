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

import java.util.Calendar;

import org.json.JSONException;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

/**
 * Service to handle updates.
 * 
 * @author MicMun
 * @version 1.0, 18.03.2013
 * 
 */
public class DaysLeftService extends IntentService {
	private static final String TAG = "DaysLeftService";

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
		Calendar now = Calendar.getInstance();
		try {
			DayCalculator dayCalc = new DayCalculator(now.getTime());
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		
	}

}
