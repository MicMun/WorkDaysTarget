/**
 * Copyright 2013 by MicMun
 *
 */
package de.micmun.android.workdaystarget;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Manager for the shared preferences.
 * 
 * @author MicMun
 * @version 1.0, 19.04.2013
 */
public class PrefManager {
	private final String PREF_FILE_NAME = "workdaystarget_";

	// Keys
	/**
	 * Key for the target date
	 */
	public static final String KEY_TARGET = "TARGET";
	/**
	 * Key for monday checked.
	 */
	public static final String KEY_MONDAY = "MONDAY";
	/**
	 * Key for tuesday checked.
	 */
	public static final String KEY_TUESDAY = "TUESDAY";
	/**
	 * Key for wednesday checked.
	 */
	public static final String KEY_WEDNESDAY = "WEDNESDAY";
	/**
	 * Key for thursday checked.
	 */
	public static final String KEY_THURSDAY = "THURSDAY";
	/**
	 * Key for friday checked.
	 */
	public static final String KEY_FRIDAY = "FRIDAY";
	/**
	 * Key for saturday checked.
	 */
	public static final String KEY_SATURDAY = "SATURDAY";
	/**
	 * Key for monday checked.
	 */
	public static final String KEY_SUNDAY = "SUNDYDAY";
	/**
	 * Key for holiday checked.
	 */
	public static final String KEY_HOLIDAY = "HOLIDAY";

	private String mFileName;
	private SharedPreferences mSharedPref;
	private Context mCtx;

	/**
	 * Creates a new PrefManager with Context und appId.
	 * 
	 * @param ctx
	 *           Context of the Manager.
	 * @param appId
	 *           id of the widget.
	 */
	public PrefManager(Context ctx, int appId) {
		mCtx = ctx;
		mFileName = PREF_FILE_NAME + appId;
		mSharedPref = mCtx.getSharedPreferences(mFileName, Context.MODE_PRIVATE);
	}

	/**
	 * Saves the value from input in the shared preferences.
	 * 
	 * @param input
	 *           Map of the input values to save.
	 */
	public void save(Map<String, Object> input) {
		Editor editor = mSharedPref.edit();

		// target
		Long target = (Long) input.get(KEY_TARGET);
		if (target != null) {
			editor.putLong(KEY_TARGET, target.longValue());
		}

		// checked values
		Boolean isMon = (Boolean) input.get(KEY_MONDAY);
		if (isMon != null) {
			editor.putBoolean(KEY_MONDAY, isMon.booleanValue());
		}
		Boolean isTue = (Boolean) input.get(KEY_TUESDAY);
		if (isTue != null) {
			editor.putBoolean(KEY_TUESDAY, isTue.booleanValue());
		}
		Boolean isWed = (Boolean) input.get(KEY_WEDNESDAY);
		if (isWed != null) {
			editor.putBoolean(KEY_WEDNESDAY, isWed.booleanValue());
		}
		Boolean isThurs = (Boolean) input.get(KEY_THURSDAY);
		if (isThurs != null) {
			editor.putBoolean(KEY_THURSDAY, isThurs.booleanValue());
		}
		Boolean isFri = (Boolean) input.get(KEY_FRIDAY);
		if (isFri != null) {
			editor.putBoolean(KEY_FRIDAY, isFri.booleanValue());
		}
		Boolean isSat = (Boolean) input.get(KEY_SATURDAY);
		if (isSat != null) {
			editor.putBoolean(KEY_SATURDAY, isSat.booleanValue());
		}
		Boolean isSun = (Boolean) input.get(KEY_SUNDAY);
		if (isSun != null) {
			editor.putBoolean(KEY_SUNDAY, isSun.booleanValue());
		}
		Boolean isHoli = (Boolean) input.get(KEY_HOLIDAY);
		if (isHoli != null) {
			editor.putBoolean(KEY_HOLIDAY, isHoli.booleanValue());
		}

		editor.commit();
	}

	/**
	 * Returns the shared preferences as a map.
	 * 
	 * @return map with shared preferences.
	 */
	public Map<String, Object> load() {
		HashMap<String, Object> map = new HashMap<String, Object>();

		// Target
		Long target = mSharedPref.getLong(KEY_TARGET, System.currentTimeMillis());
		map.put(KEY_TARGET, target);

		// checked values
		String[] chkKeys = { KEY_MONDAY, KEY_TUESDAY, KEY_WEDNESDAY,
				KEY_THURSDAY, KEY_FRIDAY, KEY_SATURDAY, KEY_SUNDAY, KEY_HOLIDAY };
		boolean[] defValues = { true, true, true, true, true, false, false, false };

		for (int i = 0; i < chkKeys.length; ++i) {
			Boolean check = mSharedPref.getBoolean(chkKeys[i], defValues[i]);
			map.put(chkKeys[i], check);
		}

		return map;
	}

	/**
	 * Returns the target as Calendar object.
	 * 
	 * @return target calendar.
	 */
	public Calendar getTarget() {
		Calendar cal = Calendar.getInstance();

		long target = mSharedPref.getLong(KEY_TARGET, System.currentTimeMillis());
		cal.setTimeInMillis(target);

		return cal;
	}

	/**
	 * Returns the checked days.
	 * 
	 * @return checked days.
	 */
	public boolean[] getCheckedDays() {
		String[] chkKeys = { KEY_MONDAY, KEY_TUESDAY, KEY_WEDNESDAY,
				KEY_THURSDAY, KEY_FRIDAY, KEY_SATURDAY, KEY_SUNDAY, KEY_HOLIDAY };
		boolean[] defValues = { true, true, true, true, true, false, false, false };
		boolean[] checkDays = new boolean[chkKeys.length];
		boolean chk;

		for (int i = 0; i < chkKeys.length; ++i) {
			chk = mSharedPref.getBoolean(chkKeys[i], defValues[i]);
			checkDays[i] = chk;
		}

		return checkDays;
	}
}
