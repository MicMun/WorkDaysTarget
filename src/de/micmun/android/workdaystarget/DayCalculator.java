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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Calculates the difference to target in days.
 * 
 * @author MicMun
 * @version 1.0, 07.04.2013
 * 
 */
public class DayCalculator {
	private static final int POS_HOLIDAY = 7; // position of holiday
	private static final int POS_SUNDAY = 6; // position of sunday

	private Calendar today = null;
	private Calendar target = null;
	private List<Date> holidays = null;

	/**
	 * Creates a new DayCalculator with target date.
	 * 
	 * @param t
	 *           target date.
	 * @throws JSONException
	 *            if getting the holidays fails.
	 */
	public DayCalculator(Date t) throws JSONException {
		today = Calendar.getInstance();
		target = Calendar.getInstance();
		target.setTime(t);
		setMidnight();
		holidays = new ArrayList<Date>();
		getHolidays();
	}

	/**
	 * Returns the number of days between today and target, skipped days in
	 * appliance of checkedDays.
	 * 
	 * @param checkedDays
	 *           Array with boolean flags, which says if a weekday should count
	 *           or not.
	 * @return days to target.
	 */
	public int getDaysLeft(boolean[] checkedDays) {
		int count = 0;
		Calendar curr = Calendar.getInstance();
		curr.setTime(today.getTime());

		while (curr.getTimeInMillis() != target.getTimeInMillis()) {
			curr.add(Calendar.DAY_OF_MONTH, 1);
			int dayOfWeek = curr.get(Calendar.DAY_OF_WEEK);

			if (!checkedDays[POS_HOLIDAY] && isHoliday(curr)) {
				continue;
			} else if (!checkedDays[POS_SUNDAY] && dayOfWeek == Calendar.SUNDAY) {
				continue;
			} else if (dayOfWeek != Calendar.SUNDAY
					&& !checkedDays[dayOfWeek - Calendar.MONDAY]) {
				continue;
			}
			count++;
		}

		return count;
	}

	/**
	 * Returns <code>true</code>, if the date is a holiday.
	 * 
	 * @param d
	 *           Date to check.
	 * @return <code>true</code>, if the date is a holiday.
	 */
	private boolean isHoliday(Calendar d) {
		return holidays.contains(d.getTime());
	}

	/**
	 * Returns the list with holydays between now and target.
	 * 
	 * @return the list with holydays between now and target.
	 * @throws JSONException
	 *            if parsing the holidays fails.
	 */
	private void getHolidays() throws JSONException {
		String basisUrl = "http://kayaposoft.com/enrico/json/v1.0/"
				+ "?action=getPublicHolidaysForDateRange&fromDate=%s&toDate=%s"
				+ "&country=ger&region=Bavaria";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy",
				Locale.getDefault());

		String url = String.format(basisUrl, sdf.format(today.getTime()),
				sdf.format(target.getTime()));
		BufferedReader br = null;
		String text = null;

		try {
			URL httpUrl = new URL(url);
			HttpURLConnection con = (HttpURLConnection) httpUrl.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			InputStream is = con.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String line;

			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			br.close();
			text = sb.toString();
		} catch (MalformedURLException e) {
			System.err.println("FEHLER: " + e.getMessage());
			holidays = null;
		} catch (IOException e) {
			System.err.println("FEHLER: " + e.getMessage());
			holidays = null;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
				br = null;
			}
		}

		if (text != null) {
			JSONArray array = new JSONArray(text);
			for (int i = 0; i < array.length(); ++i) {
				JSONObject h = array.getJSONObject(i);
				JSONObject o = h.getJSONObject("date");
				int day = o.getInt("day");
				int month = o.getInt("month");
				int year = o.getInt("year");
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, day);
				cal.set(Calendar.MONTH, month - 1);
				cal.set(Calendar.YEAR, year);
				setMidnight(cal);
				holidays.add(cal.getTime());
			}
		}
	}

	/**
	 * Sets the time of today and target to midnight.
	 */
	private void setMidnight() {
		if (today != null) {
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
		}
		if (target != null) {
			target.set(Calendar.HOUR_OF_DAY, 0);
			target.set(Calendar.MINUTE, 0);
			target.set(Calendar.SECOND, 0);
			target.set(Calendar.MILLISECOND, 0);
		}
	}

	/**
	 * Sets the time of given calendar to midnight.
	 * 
	 * @param c
	 *           calendar to set the time.
	 */
	private void setMidnight(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	}
}
