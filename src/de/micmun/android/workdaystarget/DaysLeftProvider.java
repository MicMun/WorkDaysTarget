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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Provider for data of widget.
 * 
 * @author MicMun
 * @version 1.0, 18.03.2013
 * 
 */
public class DaysLeftProvider extends AppWidgetProvider {
	public static final Intent UPDATE = new Intent(
			DaysLeftService.ACTION_UPDATE);
	private static final int REQUEST_CODE = 1;
	private Context mContext;

	/**
	 * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context,
	 *      android.appwidget.AppWidgetManager, int[])
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		mContext = context;
		mContext.startService(UPDATE);
		scheduleUpdates();
	}

	/**
	 * @see android.appwidget.AppWidgetProvider#onDeleted(android.content.Context,
	 *      int[])
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		int[] remainingIds = mgr.getAppWidgetIds(new ComponentName(context, this
				.getClass()));
		if (remainingIds == null || remainingIds.length <= 0) {
			PendingIntent pi = PendingIntent.getService(context, REQUEST_CODE,
					UPDATE, PendingIntent.FLAG_NO_CREATE);
			if (pi != null) {
				AlarmManager am = (AlarmManager) context
						.getSystemService(Context.ALARM_SERVICE);
				am.cancel(pi);
				pi.cancel();
			}
		}
	}

	/**
	 * Sets a timer to schedule updates automatically.
	 */
	private void scheduleUpdates() {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		AlarmManager am = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getService(mContext, REQUEST_CODE,
				UPDATE, PendingIntent.FLAG_NO_CREATE);
		if (pi == null) {
			pi = PendingIntent.getService(mContext, REQUEST_CODE, UPDATE,
					PendingIntent.FLAG_CANCEL_CURRENT);
			long seconds = 6 * 60 * 60;
			am.setRepeating(AlarmManager.RTC, date.getTimeInMillis(),
					seconds * 1000, pi);
		}
	}
}
