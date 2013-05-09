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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.Toast;

/**
 * Activity for Configuration of the widget.
 * 
 * @author MicMun
 * @version 1.0, 19.04.2013
 */
public class ConfigActivity extends Activity implements OnClickListener {
	private Button okBtn;
	private Button cancelBtn;

	private int appId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private PrefManager mPrefManager;

	private DatePicker mDatePicker;
	private CheckBox[] checkedDays;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);

		setContentView(R.layout.activity_config);

		okBtn = (Button) findViewById(R.id.okButton);
		okBtn.setOnClickListener(this);
		cancelBtn = (Button) findViewById(R.id.cancelButton);
		cancelBtn.setOnClickListener(this);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			appId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// if no valid id, cancel.
		if (appId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}
		Log.d("ConfigActivity", "config app id: " + appId);
		mPrefManager = new PrefManager(this, appId);
		load();
	}

	/**
	 * Save the configuration.
	 */
	private void save() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String[] chkKeys = { PrefManager.KEY_MONDAY, PrefManager.KEY_TUESDAY,
				PrefManager.KEY_WEDNESDAY, PrefManager.KEY_THURSDAY,
				PrefManager.KEY_FRIDAY, PrefManager.KEY_SATURDAY,
				PrefManager.KEY_SUNDAY, PrefManager.KEY_HOLIDAY };

		// Target date
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
		cal.set(Calendar.MONTH, mDatePicker.getMonth());
		cal.set(Calendar.YEAR, mDatePicker.getYear());
		Long target = Long.valueOf(cal.getTimeInMillis());
		map.put(PrefManager.KEY_TARGET, target);

		// Checked days
		Boolean b = null;

		for (int i = 0; i < checkedDays.length; ++i) {
			b = Boolean.valueOf(checkedDays[i].isChecked());
			map.put(chkKeys[i], b);
		}

		// save
		mPrefManager.save(map);
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v == okBtn) {
			save();
			startService(DaysLeftProvider.UPDATE);
			// result
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appId);
			setResult(RESULT_OK, resultValue);
		}

		finish();
	}

	/**
	 * Loads the preference.
	 */
	private void load() {
		Map<String, Object> map = mPrefManager.load();

		// Target date
		mDatePicker = (DatePicker) findViewById(R.id.targetDateChooser);
		long tlong = ((Long) map.get(PrefManager.KEY_TARGET)).longValue();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(tlong);
		mDatePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH), null);

		// Checked days
		checkedDays = new CheckBox[8];
		Boolean b = null;

		// Monday
		checkedDays[0] = (CheckBox) findViewById(R.id.chkMonday);
		b = (Boolean) map.get(PrefManager.KEY_MONDAY);
		checkedDays[0].setChecked(b.booleanValue());
		checkedDays[0].setOnCheckedChangeListener(new TooltipCheckedListener(
				R.string.mondayDesc));

		// Tuesday
		checkedDays[1] = (CheckBox) findViewById(R.id.chkTuesday);
		b = (Boolean) map.get(PrefManager.KEY_TUESDAY);
		checkedDays[1].setChecked(b.booleanValue());
		checkedDays[1].setOnCheckedChangeListener(new TooltipCheckedListener(
				R.string.tuesdayDesc));

		// Wednesday
		checkedDays[2] = (CheckBox) findViewById(R.id.chkWednesday);
		b = (Boolean) map.get(PrefManager.KEY_WEDNESDAY);
		checkedDays[2].setChecked(b.booleanValue());
		checkedDays[2].setOnCheckedChangeListener(new TooltipCheckedListener(
				R.string.wednesdayDesc));

		// Thursday
		checkedDays[3] = (CheckBox) findViewById(R.id.chkThursday);
		b = (Boolean) map.get(PrefManager.KEY_THURSDAY);
		checkedDays[3].setChecked(b.booleanValue());
		checkedDays[3].setOnCheckedChangeListener(new TooltipCheckedListener(
				R.string.thursdayDesc));

		// Friday
		checkedDays[4] = (CheckBox) findViewById(R.id.chkFriday);
		b = (Boolean) map.get(PrefManager.KEY_FRIDAY);
		checkedDays[4].setChecked(b.booleanValue());
		checkedDays[4].setOnCheckedChangeListener(new TooltipCheckedListener(
				R.string.fridayDesc));

		// Saturday
		checkedDays[5] = (CheckBox) findViewById(R.id.chkSaturday);
		b = (Boolean) map.get(PrefManager.KEY_SATURDAY);
		checkedDays[5].setChecked(b.booleanValue());
		checkedDays[5].setOnCheckedChangeListener(new TooltipCheckedListener(
				R.string.saturdayDesc));

		// Sunday
		checkedDays[6] = (CheckBox) findViewById(R.id.chkSunday);
		b = (Boolean) map.get(PrefManager.KEY_SUNDAY);
		checkedDays[6].setChecked(b.booleanValue());
		checkedDays[6].setOnCheckedChangeListener(new TooltipCheckedListener(
				R.string.sundayDesc));

		// Holidays
		checkedDays[7] = (CheckBox) findViewById(R.id.chkHoliday);
		b = (Boolean) map.get(PrefManager.KEY_HOLIDAY);
		checkedDays[7].setChecked(b.booleanValue());
		checkedDays[7].setOnCheckedChangeListener(new TooltipCheckedListener(
				R.string.holidaysDesc));
	}

	/**
	 * Shows a tooltip, when checkbox is checked or unchecked.
	 * 
	 * @author MicMun
	 * @version 1.0, 09.05.2013
	 * 
	 */
	private class TooltipCheckedListener implements OnCheckedChangeListener {
		private int mId;

		/**
		 * @param id
		 *           ressource id of the tooltip string.
		 */
		public TooltipCheckedListener(int id) {
			mId = id;
		}

		/**
		 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton,
		 *      boolean)
		 */
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			String tooltip = getResources().getString(mId);

			if (isChecked) {
				tooltip += " " + getResources().getString(R.string.checked);
			} else {
				tooltip += " " + getResources().getString(R.string.unchecked);
			}

			Toast.makeText(buttonView.getContext(), tooltip, Toast.LENGTH_SHORT)
					.show();
		}

	}
}
