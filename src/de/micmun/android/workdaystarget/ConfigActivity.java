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
import android.widget.DatePicker;

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

		// Tuesday
		checkedDays[1] = (CheckBox) findViewById(R.id.chkTuesday);
		b = (Boolean) map.get(PrefManager.KEY_TUESDAY);
		checkedDays[1].setChecked(b.booleanValue());

		// Wednesday
		checkedDays[2] = (CheckBox) findViewById(R.id.chkWednesday);
		b = (Boolean) map.get(PrefManager.KEY_WEDNESDAY);
		checkedDays[2].setChecked(b.booleanValue());

		// Thursday
		checkedDays[3] = (CheckBox) findViewById(R.id.chkThursday);
		b = (Boolean) map.get(PrefManager.KEY_THURSDAY);
		checkedDays[3].setChecked(b.booleanValue());

		// Friday
		checkedDays[4] = (CheckBox) findViewById(R.id.chkFriday);
		b = (Boolean) map.get(PrefManager.KEY_FRIDAY);
		checkedDays[4].setChecked(b.booleanValue());

		// Saturday
		checkedDays[5] = (CheckBox) findViewById(R.id.chkSaturday);
		b = (Boolean) map.get(PrefManager.KEY_SATURDAY);
		checkedDays[5].setChecked(b.booleanValue());

		// Sunday
		checkedDays[6] = (CheckBox) findViewById(R.id.chkSunday);
		b = (Boolean) map.get(PrefManager.KEY_SUNDAY);
		checkedDays[6].setChecked(b.booleanValue());

		// Holidays
		checkedDays[7] = (CheckBox) findViewById(R.id.chkHoliday);
		b = (Boolean) map.get(PrefManager.KEY_HOLIDAY);
		checkedDays[7].setChecked(b.booleanValue());
	}
}
