/**
 * Copyright 2013-2014 by Michael Munzert
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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for the shared preferences.
 *
 * @author MicMun
 * @version 1.2, 22.05.2014
 */
public class PrefManager {
   private final String FILE_NAME = "de.micmun.android.workdaystarget.DaysLeftProvider";

   // Keys
   /**
    * Key for the target date.
    */
   public static final String KEY_TARGET = "TARGET";
   /**
    * Key for the difference.
    */
   public static final String KEY_DIFF = "DIFFERENCE";
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
   public static final String KEY_SUNDAY = "SUNDAY";
   /**
    * Key for holiday checked.
    */
   public static final String KEY_HOLIDAY = "HOLIDAY";

   private SharedPreferences mSharedPref;
   private int mAppId;

   /**
    * Creates a new PrefManager with Context und appId.
    *
    * @param ctx   Context of the Manager.
    * @param appId id of the widget.
    */
   public PrefManager(Context ctx, int appId) {
      mSharedPref = ctx.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
      mAppId = appId;
   }

   /**
    * Saves the value from input in the shared preferences.
    *
    * @param input Map of the input values to save.
    */
   public void save(Map<String, Object> input) {
      Editor editor = mSharedPref.edit();

      // target
      Long target = (Long) input.get(KEY_TARGET);
      if (target != null) {
         editor.putLong(KEY_TARGET + mAppId, target.longValue());
      }

      // Last difference
      Long diff = (Long) input.get(KEY_DIFF);
      if (diff != null) {
         editor.putLong(KEY_DIFF + mAppId, diff.longValue());
      }

      // checked values
      Boolean isMon = (Boolean) input.get(KEY_MONDAY);
      if (isMon != null) {
         editor.putBoolean(KEY_MONDAY + mAppId, isMon.booleanValue());
      }
      Boolean isTue = (Boolean) input.get(KEY_TUESDAY);
      if (isTue != null) {
         editor.putBoolean(KEY_TUESDAY + mAppId, isTue.booleanValue());
      }
      Boolean isWed = (Boolean) input.get(KEY_WEDNESDAY);
      if (isWed != null) {
         editor.putBoolean(KEY_WEDNESDAY + mAppId, isWed.booleanValue());
      }
      Boolean isThurs = (Boolean) input.get(KEY_THURSDAY);
      if (isThurs != null) {
         editor.putBoolean(KEY_THURSDAY + mAppId, isThurs.booleanValue());
      }
      Boolean isFri = (Boolean) input.get(KEY_FRIDAY);
      if (isFri != null) {
         editor.putBoolean(KEY_FRIDAY + mAppId, isFri.booleanValue());
      }
      Boolean isSat = (Boolean) input.get(KEY_SATURDAY);
      if (isSat != null) {
         editor.putBoolean(KEY_SATURDAY + mAppId, isSat.booleanValue());
      }
      Boolean isSun = (Boolean) input.get(KEY_SUNDAY);
      if (isSun != null) {
         editor.putBoolean(KEY_SUNDAY + mAppId, isSun.booleanValue());
      }
      Boolean isHoli = (Boolean) input.get(KEY_HOLIDAY);
      if (isHoli != null) {
         editor.putBoolean(KEY_HOLIDAY + mAppId, isHoli.booleanValue());
      }

      if (!editor.commit()) {
         Log.e("PrefManager", "Error comitting SharedPreferencess");
      }
   }

   /**
    * Returns the shared preferences as a map.
    *
    * @return map with shared preferences.
    */
   public Map<String, Object> load() {
      HashMap<String, Object> map = new HashMap<String, Object>();

      // Target
      Long target = mSharedPref.getLong(KEY_TARGET + mAppId,
            System.currentTimeMillis());
      map.put(KEY_TARGET, target);

      // Difference
      Long diff = mSharedPref.getLong(KEY_DIFF + mAppId, 0L);
      map.put(KEY_DIFF, diff);

      // checked values
      String[] chkKeys = {KEY_MONDAY, KEY_TUESDAY, KEY_WEDNESDAY,
            KEY_THURSDAY, KEY_FRIDAY, KEY_SATURDAY, KEY_SUNDAY, KEY_HOLIDAY};
      boolean[] defValues = {true, true, true, true, true, false, false, false};

      for (int i = 0; i < chkKeys.length; ++i) {
         Boolean check = mSharedPref.getBoolean(chkKeys[i] + mAppId,
               defValues[i]);
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

      long target = mSharedPref.getLong(KEY_TARGET + mAppId,
            System.currentTimeMillis());
      cal.setTimeInMillis(target);

      return cal;
   }

   /**
    * Returns the last difference to target.
    *
    * @return last difference to target.
    */
   public int getLastDiff() {
      return ((Long) mSharedPref.getLong(KEY_DIFF + mAppId, 0L)).intValue();
   }

   /**
    * Returns the checked days.
    *
    * @return checked days.
    */
   public boolean[] getCheckedDays() {
      String[] chkKeys = {KEY_MONDAY, KEY_TUESDAY, KEY_WEDNESDAY,
            KEY_THURSDAY, KEY_FRIDAY, KEY_SATURDAY, KEY_SUNDAY, KEY_HOLIDAY};
      boolean[] defValues = {true, true, true, true, true, false, false, false};
      boolean[] checkDays = new boolean[chkKeys.length];
      boolean chk;

      for (int i = 0; i < chkKeys.length; ++i) {
         chk = mSharedPref.getBoolean(chkKeys[i] + mAppId, defValues[i]);
         checkDays[i] = chk;
      }

      return checkDays;
   }
}
