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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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
	public static final Intent UPDATE = new Intent(DaysLeftService.ACTION_UPDATE);
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
	}
}
