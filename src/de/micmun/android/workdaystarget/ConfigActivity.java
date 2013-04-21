package de.micmun.android.workdaystarget;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RemoteViews;

/**
 * Activity for Configuration of the widget.
 * 
 * @author MicMun
 * @version 1.0, 19.04.2013
 */
public class ConfigActivity extends Activity implements OnClickListener {
	private Button okBtn;
	private Button cancelBtn;

	private int appId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	}

	/**
	 * Save the configuration.
	 */
	private void save() {
		PrefManager pm = new PrefManager(this, appId);
		HashMap<String, Object> map = new HashMap<String, Object>();

		// Target date
		DatePicker dp = (DatePicker) findViewById(R.id.targetDateChooser);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
		cal.set(Calendar.MONTH, dp.getMonth());
		cal.set(Calendar.YEAR, dp.getYear());
		Long target = Long.valueOf(cal.getTimeInMillis());
		map.put(PrefManager.KEY_TARGET, target);

		// Checked days
		CheckBox cb = null;
		Boolean b = null;

		// Monday
		cb = (CheckBox) findViewById(R.id.chkMonday);
		b = Boolean.valueOf(cb.isChecked());
		map.put(PrefManager.KEY_MONDAY, b);
		// Tuesday
		cb = (CheckBox) findViewById(R.id.chkTuesday);
		b = Boolean.valueOf(cb.isChecked());
		map.put(PrefManager.KEY_TUESDAY, b);
		// Wednesday
		cb = (CheckBox) findViewById(R.id.chkWednesday);
		b = Boolean.valueOf(cb.isChecked());
		map.put(PrefManager.KEY_WEDNESDAY, b);
		// Thursday
		cb = (CheckBox) findViewById(R.id.chkThursday);
		b = Boolean.valueOf(cb.isChecked());
		map.put(PrefManager.KEY_THURSDAY, b);
		// Friday
		cb = (CheckBox) findViewById(R.id.chkFriday);
		b = Boolean.valueOf(cb.isChecked());
		map.put(PrefManager.KEY_FRIDAY, b);
		// Saturday
		cb = (CheckBox) findViewById(R.id.chkSaturday);
		b = Boolean.valueOf(cb.isChecked());
		map.put(PrefManager.KEY_SATURDAY, b);
		// Sunday
		cb = (CheckBox) findViewById(R.id.chkSunday);
		b = Boolean.valueOf(cb.isChecked());
		map.put(PrefManager.KEY_SUNDAY, b);
		// Holidays
		cb = (CheckBox) findViewById(R.id.chkHoliday);
		b = Boolean.valueOf(cb.isChecked());
		map.put(PrefManager.KEY_HOLIDAY, b);

		// save
		pm.save(map);
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		setResult(RESULT_CANCELED);

		if (v == okBtn) {
			save();
			// update widget
			AppWidgetManager awm = AppWidgetManager.getInstance(this);
			RemoteViews views = new RemoteViews(this.getPackageName(),
					R.layout.appwidget_layout);
			awm.updateAppWidget(appId, views);
			// result
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appId);
			setResult(RESULT_OK, resultValue);
		}

		finish();
	}
}
