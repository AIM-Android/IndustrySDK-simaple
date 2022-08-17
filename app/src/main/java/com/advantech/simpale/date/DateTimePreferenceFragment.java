package com.advantech.simpale.date;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.provider.Settings.Global.AUTO_TIME;
import static android.provider.Settings.Global.AUTO_TIME_ZONE;

import androidx.annotation.Nullable;

import com.advantech.simpale.R;


/**
 * ClassName:   DateTimePreferenceFragment
 * Description: TODO
 * CreateDate   2021/09/14
 * Author:  Fengchao.dai
 */
public class DateTimePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener
        , Preference.OnPreferenceClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "TimeSetting";
    SwitchPreference mAutoTimePreference;
    Preference mSetDatePreference;
    Preference mSetTimePreference;
    SwitchPreference mAutoTimeZonePreference;
    Preference mSetTimeZonePreference;
    SwitchPreference mDateFormatPreference;

    private static final String AUTOMATIC_DATE_AND_TIME = "automatic_date_and_time";
    private static final String SET_DATE = "set_date";
    private static final String SET_TIME = "set_time";
    private static final String AUTOMATIC_TIME_ZONE = "automatic_time_zone";
    private static final String SET_TIME_ZONE = "set_time_zone";
    private static final String USE_24_HOUR_FORMAT = "use_24_hour_format";

    public static final int SET_TIME_ZONE_RESULT_CODE = 0;
    public static final String TIME_ZONE_ID = "TIME_ZONE_ID";

    static final String HOURS_12 = "12";
    static final String HOURS_24 = "24";
    public static final int EXTRA_TIME_PREF_VALUE_USE_12_HOUR = 0;
    public static final int EXTRA_TIME_PREF_VALUE_USE_24_HOUR = 1;
    public static final int EXTRA_TIME_PREF_VALUE_USE_LOCALE_DEFAULT = 2;
    public static final String EXTRA_TIME_PREF_24_HOUR_FORMAT =
            "android.intent.extra.TIME_PREF_24_HOUR_FORMAT";
    public static final int FLAG_RECEIVER_INCLUDE_BACKGROUND = 0x01000000;


    private UpdateUIRunnable updateUIRunnable;
    private AlarmManager mAlarmManager;
    private SimpleDateFormat mTimeFormatter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_time_settings);
        mAlarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUIRunnable = new UpdateUIRunnable(getActivity().getApplicationContext(), 1000);
        updateUIRunnable.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (updateUIRunnable != null) {
            updateUIRunnable.stop();
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();
        if (SET_DATE.equals(key)) {
            initCalendar();
        } else if (SET_TIME.equals(key)) {
            initTimer();
        } else if (SET_TIME_ZONE.equals(key)) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), TimeZoneSetting.class);
            startActivityForResult(intent, SET_TIME_ZONE_RESULT_CODE);
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (AUTOMATIC_DATE_AND_TIME.equals(key)) {
            if ((Boolean) objValue) {
                Settings.Global.putInt(getContext().getContentResolver(), "auto_time", 1);
                mSetDatePreference.setEnabled(false);
                mSetTimePreference.setEnabled(false);
            } else {
                Settings.Global.putInt(getContext().getContentResolver(), "auto_time", 0);
                mSetDatePreference.setEnabled(true);
                mSetTimePreference.setEnabled(true);
            }
        } else if (AUTOMATIC_TIME_ZONE.equals(key)) {
            if ((Boolean) objValue) {
                Settings.Global.putInt(getContext().getContentResolver(), AUTO_TIME_ZONE, 1);
                mSetTimeZonePreference.setEnabled(false);
            } else {
                Settings.Global.putInt(getContext().getContentResolver(), AUTO_TIME_ZONE, 0);
                mSetTimeZonePreference.setEnabled(true);
            }
        } else if (USE_24_HOUR_FORMAT.equals(key)) {
            if ((Boolean) objValue) {
                mDateFormatPreference.setChecked(true);
                mDateFormatPreference.setSummary(getResources().getString(R.string.use_24_hour_format_summary_13));
                mTimeFormatter = new SimpleDateFormat("HH:mm:ss");
                set24Hour(getContext(), true);
                timeUpdated(getContext(), true);
            } else {
                mDateFormatPreference.setChecked(false);
                mDateFormatPreference.setSummary(getResources().getString(R.string.use_24_hour_format_summary_1_pm));
                mTimeFormatter = new SimpleDateFormat("hh:mm:ss a");
                set24Hour(getContext(), false);
                timeUpdated(getContext(), false);
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SET_TIME_ZONE_RESULT_CODE) {
            if (data != null) {
                String timezone = data.getStringExtra(TIME_ZONE_ID);
                if (timezone != null && !timezone.isEmpty()) {
                    if (mAlarmManager != null) {
                        Log.d(TAG, "Select timezone is " + timezone);
                        mAlarmManager.setTimeZone(timezone);
                    } else {
                        Log.e(TAG, "mAlarmManager == null");
                    }
                } else {
                    Log.e(TAG, "timezone is empty");
                }
            }
        }
    }

    private void initView() {
        mAutoTimePreference = (SwitchPreference) findPreference(AUTOMATIC_DATE_AND_TIME);
        mAutoTimePreference.setOnPreferenceChangeListener(this);

        mSetDatePreference = findPreference(SET_DATE);
        mSetDatePreference.setOnPreferenceClickListener(this);

        mSetTimePreference = findPreference(SET_TIME);
        mSetTimePreference.setOnPreferenceClickListener(this);

        mAutoTimeZonePreference = (SwitchPreference) findPreference(AUTOMATIC_TIME_ZONE);
        mAutoTimeZonePreference.setOnPreferenceChangeListener(this);
        mSetTimeZonePreference = findPreference(SET_TIME_ZONE);
        mSetTimeZonePreference.setOnPreferenceClickListener(this);

        mDateFormatPreference = (SwitchPreference) findPreference(USE_24_HOUR_FORMAT);
        mDateFormatPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        String desc = String.format("您选择的日期是：%s年%s月%s日", year, month, dayOfMonth);
        if (setSysDate(year, month, dayOfMonth)) {
            Log.d(TAG, "Set Data succeed: " + desc);
        } else {
            Log.e(TAG, "Set Data failed: " + desc);
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        String desc = String.format("您选择的时间是：       %s:%s", hour, minute);
        if (setSysTime(hour, minute)) {
            Log.d(TAG, "Set Time succeed: " + desc);
        } else {
            Log.e(TAG, "Set Time failed: " + desc);
        }
    }


    class UpdateUIRunnable implements Runnable {

        private ScheduledExecutorService scheduler;
        private long freq;
        private Context mContext;

        private UpdateUIRunnable(Context context, long freq) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            this.freq = freq;
            this.mContext = context;
        }

        public void start() {
            scheduler.scheduleWithFixedDelay(this, 0L, freq, TimeUnit.MILLISECONDS);
        }

        public void stop() {
            scheduler.shutdown();
        }

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI(mContext);
                }
            });
        }
    }

    private void updateUI(Context context) {
        int autoTime = Settings.Global.getInt(getContext().getContentResolver(), AUTO_TIME, 0);
        if (autoTime == 0) {
            mAutoTimePreference.setChecked(false);
            mSetDatePreference.setEnabled(true);
            mSetTimePreference.setEnabled(true);
        } else {
            mAutoTimePreference.setChecked(true);
            mSetDatePreference.setEnabled(false);
            mSetTimePreference.setEnabled(false);
        }

        Calendar now = Calendar.getInstance();
        Date date = now.getTime();
        TimeZone timeZone = now.getTimeZone();

        String timezoneSummary = timeZone.getDisplayName(false, TimeZone.SHORT) + " " + timeZone.getDisplayName();
        mSetTimeZonePreference.setSummary(String.format(getResources().getString(R.string.select_time_zone_summary), timezoneSummary));

        SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        mSetDatePreference.setSummary(mDateFormatter.format(date));

        if (DateFormat.is24HourFormat(context)) {
            mDateFormatPreference.setChecked(true);
            mDateFormatPreference.setSummary(getResources().getString(R.string.use_24_hour_format_summary_13));
            mTimeFormatter = new SimpleDateFormat("HH:mm:ss");

        } else {
            mDateFormatPreference.setChecked(false);
            mDateFormatPreference.setSummary(getResources().getString(R.string.use_24_hour_format_summary_1_pm));
            mTimeFormatter = new SimpleDateFormat("hh:mm:ss a");
        }

        mSetTimePreference.setSummary(String.format(getResources().getString(R.string.select_time_zone_summary), mTimeFormatter.format(date)));

        int autoTimeZone = Settings.Global.getInt(getContext().getContentResolver(), AUTO_TIME_ZONE, 0);
        if (autoTimeZone == 0) {
            mAutoTimeZonePreference.setChecked(false);
            mSetTimeZonePreference.setEnabled(true);
        } else {
            mAutoTimeZonePreference.setChecked(true);
            mSetTimeZonePreference.setEnabled(false);
        }
    }


    void set24Hour(Context context, Boolean is24Hour) {
        String value = is24Hour == null ? null :
                is24Hour ? HOURS_24 : HOURS_12;
        Settings.System.putString(context.getContentResolver(),
                Settings.System.TIME_12_24, value);
    }

    @SuppressLint("WrongConstant")
    static void timeUpdated(Context context, Boolean is24Hour) {
        Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
        timeChanged.addFlags(FLAG_RECEIVER_INCLUDE_BACKGROUND);
        int timeFormatPreference;
        if (is24Hour == null) {
            timeFormatPreference = EXTRA_TIME_PREF_VALUE_USE_LOCALE_DEFAULT;
        } else {
            timeFormatPreference = is24Hour ? EXTRA_TIME_PREF_VALUE_USE_24_HOUR
                    : EXTRA_TIME_PREF_VALUE_USE_12_HOUR;
        }
        timeChanged.putExtra(EXTRA_TIME_PREF_24_HOUR_FORMAT, timeFormatPreference);
        context.sendBroadcast(timeChanged);
    }

    public boolean setSysTime(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            if (mAlarmManager != null) {
                mAlarmManager.setTime(when);
                return true;
            } else {
                Log.e(TAG, "mAlarmManager == null");
            }
        }
        return false;
    }

    public boolean setSysDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            if (mAlarmManager != null) {
                mAlarmManager.setTime(when);
                return true;
            } else {
                Log.e(TAG, "mAlarmManager == null");
            }
        }
        return false;
    }

    private void initCalendar() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this
                , calendar.get(Calendar.YEAR)//年份
                , calendar.get(Calendar.MONTH)//月份
                , calendar.get(Calendar.DAY_OF_MONTH));//日子
        dialog.show();
    }

    private void initTimer() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), this
                , calendar.get(Calendar.HOUR_OF_DAY)//小时
                , calendar.get(Calendar.MINUTE)//分钟
                , DateFormat.is24HourFormat(getContext()));//24小时制
        dialog.show();
    }
}
