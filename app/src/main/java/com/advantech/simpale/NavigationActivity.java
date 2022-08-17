package com.advantech.simpale;

import static android.os.BatteryManager.EXTRA_TEMPERATURE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

    }

    public static class SettingsFragment extends PreferenceFragment {

        private Preference settingdate, kiosk_activity, app_manager, Shutdown, screen_light, SCREEN_ORIENTATION,
                battery_tem, battery_status, battery_level, bat_scale, bat_health, sound_set, Multimedia, clock,
                longitude, latitude, altitude, satellites;
        private SwitchPreference Reboot, wifi_state, bluetooth, location_btn, adb;
        private ListPreference time_out;
        private Handler handler;
        private BluetoothAdapter adapter;
        private VolumeUtil volumeUtil;
        private static BroadcastReceiver receiver;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.navigationfragment);

            settingdate = findPreference("settingdate");
            kiosk_activity = findPreference("kiosk_activity");
            app_manager = findPreference("app_manager");
            Shutdown = findPreference("Shutdown");
            screen_light = findPreference("screen_light");
            battery_tem = findPreference("battery_tem");
            battery_status = findPreference("battery_status");
            battery_level = findPreference("battery_level");
            bat_scale = findPreference("bat_scale");
            bat_health = findPreference("bat_health");
            sound_set = findPreference("sound_set");
            Multimedia = findPreference("Multimedia");
            clock = findPreference("clock");
            time_out = (ListPreference) findPreference("time_out");
            Reboot = (SwitchPreference) findPreference("Reboot");
            wifi_state = (SwitchPreference) findPreference("wifi_state");
            bluetooth = (SwitchPreference) findPreference("bluetooth");
            location_btn = (SwitchPreference) findPreference("location");
            longitude = findPreference("longitude");
            latitude = findPreference("latitude");
            altitude = findPreference("altitude");
            satellites = findPreference("satellites");
            adb = (SwitchPreference) findPreference("adb");
            SCREEN_ORIENTATION = findPreference("SCREEN_ORIENTATION");
            Reboot.setChecked(false);
            adapter = BluetoothAdapter.getDefaultAdapter();
            volumeUtil = new VolumeUtil(getContext());
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean present = intent.getBooleanExtra("present", false);
                    if (present) {
                        int rawlevel = intent.getIntExtra("level", -1);//当前电量
                        int scale = intent.getIntExtra("scale", -1);//电量规格
                        //     int status = intent.getIntExtra("status", -1);//状态
                        int health = intent.getIntExtra("health", -1);//健康度
                        int temperature = intent.getIntExtra(EXTRA_TEMPERATURE, -1) / 10; //温度
                        int temp = intent.getIntExtra(EXTRA_TEMPERATURE, -1) % 10; //温度
                        battery_tem.setSummary(context.getResources().getString(R.string.battery_temperature) + ":" + temperature + "." + temp + "℃");
                        ((PreferenceCategory) findPreference("mPreCatefgory")).removePreference(battery_status);
                        battery_level.setSummary(context.getResources().getString(R.string.power_level) + ":" + rawlevel + "%");
                        bat_scale.setSummary(context.getResources().getString(R.string.battery_specification) + ":" + scale);
                        bat_health.setSummary(context.getResources().getString(R.string.health) + ":" + health);
                    } else {
                        battery_status.setSummary(context.getResources().getString(R.string.no_battery));
                        ((PreferenceCategory) findPreference("mPreCatefgory")).removePreference(battery_tem);
                        ((PreferenceCategory) findPreference("mPreCatefgory")).removePreference(battery_level);
                        ((PreferenceCategory) findPreference("mPreCatefgory")).removePreference(bat_scale);
                        ((PreferenceCategory) findPreference("mPreCatefgory")).removePreference(bat_health);
                    }
                }
            };
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            getActivity().registerReceiver(receiver, filter);
            if (isLocationEnabled()) {
                location_btn.setSummary(getResources().getString(R.string.open));
                location_btn.setChecked(true);
                ((PreferenceCategory) findPreference("location_setting")).addPreference(longitude);
                ((PreferenceCategory) findPreference("location_setting")).addPreference(latitude);
                ((PreferenceCategory) findPreference("location_setting")).addPreference(altitude);
                ((PreferenceCategory) findPreference("location_setting")).addPreference(satellites);
            } else {
                location_btn.setSummary(getResources().getString(R.string.close));
                location_btn.setChecked(false);
                ((PreferenceCategory) findPreference("location_setting")).removePreference(longitude);
                ((PreferenceCategory) findPreference("location_setting")).removePreference(latitude);
                ((PreferenceCategory) findPreference("location_setting")).removePreference(altitude);
                ((PreferenceCategory) findPreference("location_setting")).removePreference(satellites);
            }
            Timer timer2 = new Timer();
            timer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    updata_ui();
                }
            }, 1000, 3000);
            settingdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
            kiosk_activity.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), KioskActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
            app_manager.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), AppManagerActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            Reboot.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    if ((boolean) o) {
                        Reboot.setSummary(getResources().getString(R.string.restarted));
                        countDownTimer.start();
                    } else {
                        countDownTimer.cancel();
                        Reboot.setSummary(getResources().getString(R.string.power_on_status));

                    }
                    return true;
                }
            });
            Shutdown.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    shutdown();
                    return true;
                }
            });
            wifi_state.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if ((boolean) o) {
                        setWifiEnabled(true);
                        wifi_state.setSummary(getResources().getString(R.string.open));
                        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));

                    } else {
                        setWifiEnabled(false);
                        wifi_state.setSummary(getResources().getString(R.string.close));
                    }
                    return true;
                }
            });
            bluetooth.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @SuppressLint("MissingPermission")
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if ((boolean) o) {
                        setbluetooth(true);
                        bluetooth.setSummary(getResources().getString(R.string.open));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (adapter.isEnabled()) {
                                        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                                        break;
                                    }
                                }
                            }
                        }).start();
                    } else {
                        setbluetooth(false);
                        bluetooth.setSummary(getResources().getString(R.string.close));
                    }
                    return true;
                }
            });
            location_btn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if ((boolean) o) {
                        openGPS(true);
                        if (isLocationEnabled()) {
                            formListenerGetLocation();
                            ((PreferenceCategory) findPreference("location_setting")).addPreference(longitude);
                            ((PreferenceCategory) findPreference("location_setting")).addPreference(latitude);
                            ((PreferenceCategory) findPreference("location_setting")).addPreference(altitude);
                            ((PreferenceCategory) findPreference("location_setting")).addPreference(satellites);
                        } else {
                            Toast.makeText(getActivity(), "打开失败", Toast.LENGTH_SHORT).show();
                            location_btn.setChecked(false);
                        }

                    } else {
                        openGPS(false);
                        ((PreferenceCategory) findPreference("location_setting")).removePreference(longitude);
                        ((PreferenceCategory) findPreference("location_setting")).removePreference(latitude);
                        ((PreferenceCategory) findPreference("location_setting")).removePreference(altitude);
                        ((PreferenceCategory) findPreference("location_setting")).removePreference(satellites);
                    }

                    return true;
                }
            });
            adb.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if ((boolean) o) {

                        // Settings.Global.putInt(getActivity().getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 1);
                        Settings.Global.putInt(getActivity().getContentResolver(), Settings.Global.ADB_ENABLED, 1);
                        adb.setSummary(getResources().getString(R.string.open));

                    } else {

                        //  Settings.Global.putInt(getActivity().getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
                        Settings.Global.putInt(getActivity().getContentResolver(), Settings.Global.ADB_ENABLED, 0);
                        adb.setSummary(getResources().getString(R.string.close));

                    }
                    return true;
                }
            });
            time_out.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    //把preference这个Preference强制转化为ListPreference类型
                    ListPreference listPreference = (ListPreference) preference;

                    //获取ListPreference中的实体内容
                    CharSequence[] entries = listPreference.getEntries();

                    //获取ListPreference中的实体内容的下标值
                    int index = listPreference.findIndexOfValue((String) o);

                    //把listPreference中的摘要显示为当前ListPreference的实体内容中选择的那个项目
                    listPreference.setSummary(entries[index]);
                    switch (index) {

                        case 0:
                            Settings.System.putInt(getActivity().getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 10 * 1000);
                            break;
                        case 1:
                            Settings.System.putInt(getActivity().getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 15 * 1000);
                            break;
                        case 2:
                            Settings.System.putInt(getActivity().getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 60 * 1000);
                            break;
                        case 3:
                            Settings.System.putInt(getActivity().getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 120 * 1000);
                            break;
                        case 4:
                            Settings.System.putInt(getActivity().getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 301 * 1000);
                            break;
                        case 5:
                            Settings.System.putInt(getActivity().getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 60 * 10 * 1000);
                            break;
                        case 6:
                            Settings.System.putInt(getActivity().getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 60 * 30 * 1000);
                            break;
                        case 7:
                            Settings.System.putInt(getActivity().getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE);
                            break;

                    }

                    return true;
                }
            });

            screen_light.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    show_dialog(getScreenBrightness());
                    return true;
                }
            });

            SCREEN_ORIENTATION.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), RotateControlActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            sound_set.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    show_audiodialog(volumeUtil.getringVolume(), volumeUtil.getringmaxVolume(), volumeUtil, voiceType.ring);

                    return true;
                }
            });

            Multimedia.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    show_audiodialog(volumeUtil.getMediaVolume(), volumeUtil.getMediaMaxVolume(), volumeUtil, voiceType.Multimedia);

                    return true;
                }
            });

            clock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    show_audiodialog(volumeUtil.getAlermVolume(), volumeUtil.getAlermMaxVolume(), volumeUtil, voiceType.clock);
                    return true;
                }
            });


            handler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0:
                            reboot();
                            break;
                        case 1:
                            break;
                        case 2:
                            timer2.cancel();

                            break;
                    }

                }
            };

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Message message = new Message();
            message.what = 2;
            handler.sendMessage(message);
            getActivity().unregisterReceiver(receiver);

        }

        /**
         * 通过LocationListener来获取Location信息
         */
        public void formListenerGetLocation() {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    //位置信息变化时触发
                    longitude.setSummary(location.getLongitude() + "");
                    latitude.setSummary(location.getLatitude() + "");
                    altitude.setSummary(location.getAltitude() + "");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    //GPS状态变化时触发
                }

                @Override
                public void onProviderEnabled(String provider) {
                    //GPS禁用时触发
                }

                @Override
                public void onProviderDisabled(String provider) {
                    //GPS开启时触发
                }
            };
            /**
             * 绑定监听
             * 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种，前者是GPS,后者是GPRS以及WIFI定位
             * 参数2，位置信息更新周期.单位是毫秒
             * 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
             * 参数4，监听
             * 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
             */
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.addGpsStatusListener(statusListener); // 注册状态信息回调

        }

        private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号
        /**
         * 卫星状态监听器
         */
        private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
            public void onGpsStatusChanged(int event) { // GPS状态变化时的回调，如卫星数

                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                @SuppressLint("MissingPermission") GpsStatus status = locationManager.getGpsStatus(null); // 取当前状态
                updateGpsStatus(event, status);
                satellites.setSummary(numSatelliteList.size() + "");
            }
        };

        private void updateGpsStatus(int event, GpsStatus status) {
            if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                int maxSatellites = status.getMaxSatellites();

                Iterator<GpsSatellite> it = status.getSatellites().iterator();
                numSatelliteList.clear();
                int count = 0;
                while (it.hasNext() && count <= maxSatellites) {
                    GpsSatellite s = it.next();
                    numSatelliteList.add(s);
                    if (s.getSnr() > 10)//只有信躁比不为0的时候才算搜到了星
                    {
                        numSatelliteList.add(s);
                        count++;

                    }
                }

            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void shutdown() {
            PowerManager pm = (PowerManager) getContext().getApplicationContext().getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                try {
                    @SuppressLint("SoonBlockedPrivateApi")
                    Method shutdown = PowerManager.class.getDeclaredMethod("shutdown", boolean.class, String.class, boolean.class);
                    shutdown.invoke(pm, false, null, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void reboot() {
            PowerManager pm = (PowerManager) getContext().getApplicationContext().getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                pm.reboot(null);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void setWifiEnabled(boolean enabled) {

            final WifiManager wm = (WifiManager) getContext().getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);

            if (enabled) {
                wm.setWifiEnabled(true);

            } else {
                wm.setWifiEnabled(false);
            }

        }

        // 获取 WIFI 的状态.
        @RequiresApi(api = Build.VERSION_CODES.M)
        public int getWifiState() {
            final WifiManager manager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            return manager == null ? WifiManager.WIFI_STATE_UNKNOWN : manager.getWifiState();
        }

        /**
         * 注意:
         * WiFi 的状态目前有五种, 分别是:
         * WifiManager.WIFI_STATE_ENABLING: WiFi正要开启的状态, 是 Enabled 和 Disabled 的临界状态;
         * WifiManager.WIFI_STATE_ENABLED: WiFi已经完全开启的状态;
         * WifiManager.WIFI_STATE_DISABLING: WiFi正要关闭的状态, 是 Disabled 和 Enabled 的临界状态;
         * WifiManager.WIFI_STATE_DISABLED: WiFi已经完全关闭的状态;
         * WifiManager.WIFI_STATE_UNKNOWN: WiFi未知的状态, WiFi开启, 关闭过程中出现异常, 或是厂家未配备WiFi外挂模块会出现的情况;
         */

        @SuppressLint("MissingPermission")
        public void setbluetooth(boolean state) {
            // 获取本地的蓝牙适配器实例
            //    adapter.enable();
            if (state) {
                if (adapter != null) {
                    if (!adapter.isEnabled()) {
                        //通过这个方法来请求打开我们的蓝牙设备
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivity(intent);
                    }
                } else {
                    Log.e("本地设备驱动异常", null);
                }

            } else {

                adapter.disable();
            }


        }

        /**
         * 判断定位服务是否开启
         *
         * @param
         * @return true 表示开启
         */
        public boolean isLocationEnabled() {
            int locationMode = 0;
            String locationProviders;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    locationMode = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
                return locationMode != Settings.Secure.LOCATION_MODE_OFF;
            } else {
                locationProviders = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                return !TextUtils.isEmpty(locationProviders);
            }
        }

        //        //打开或者关闭gps
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void openGPS(boolean open) {
            if (Build.VERSION.SDK_INT < 19) {
                Settings.Secure.setLocationProviderEnabled(getContext().getContentResolver(),
                        LocationManager.GPS_PROVIDER, open);
            } else {
                if (!open) {
                    Settings.Secure.putInt(getContext().getContentResolver(), Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_OFF);
                } else {
                    Settings.Secure.putInt(getContext().getContentResolver(), Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
                }
            }
        }

        /**
         * 1.获取系统默认屏幕亮度值 屏幕亮度值范围（0-255）
         **/
        @RequiresApi(api = Build.VERSION_CODES.M)
        private int getScreenBrightness() {
            ContentResolver contentResolver = getContext().getContentResolver();
            int defVal = 125;
            return Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS, defVal);
        }

        /**
         * 3.关闭光感，设置手动调节背光模式
         * <p>
         * SCREEN_BRIGHTNESS_MODE_AUTOMATIC 自动调节屏幕亮度模式值为1
         * <p>
         * SCREEN_BRIGHTNESS_MODE_MANUAL 手动调节屏幕亮度模式值为0
         **/
        public void setScreenManualMode(Context context) {
            ContentResolver contentResolver = context.getContentResolver();
            try {
                int mode = Settings.System.getInt(contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE);
                if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    Settings.System.putInt(contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                } else {
                    Settings.System.putInt(contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * 5.修改Setting 中屏幕亮度值
         * <p>
         * 修改Setting的值需要动态申请权限 <uses-permission
         * android:name="android.permission.WRITE_SETTINGS"/>
         **/
        private void ModifySettingsScreenBrightness(Context context, int birghtessValue) {
            // 首先需要设置为手动调节屏幕亮度模式
            setScreenManualMode(context);

            ContentResolver contentResolver = context.getContentResolver();
            Settings.System.putInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS, birghtessValue);


        }

        public String readFileByLines(String fileName) {
            File file = new File(fileName);
            if (file.exists()) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(file));
                    String tempString = null;
                    //int line = 1;
                    // 一次读入一行，直到读入null为文件结束
                    while ((tempString = reader.readLine()) != null) {
                        // 显示行号
                        return tempString;
                        //line++;
                    }
                    reader.close();
                } catch (IOException e) {
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e1) {
                        }
                    }
                }
                return null;
            } else {
                return null;
            }
        }

        public String getScreenBrightnessMaximum(Context context) {
            File file = new File("/sys/class/backlight/intel_backlight/max_brightness");
            if (file.exists()) {
                ///sys/class/backlight/intel_backlight for x86(aim 65) brightness
                String intel_max_backlight = readFileByLines("/sys/class/backlight/intel_backlight/max_brightness");
                if (intel_max_backlight.equals("96000")) {//ppc-3100
                    return "255";
                } else {
                    return intel_max_backlight;
                }
            } else {
                File advbrightnessinfo = new File("/proc/advbrightnessinfo");
                if (advbrightnessinfo.exists()) {
                    return "9";
                } else {
                    //for risc
                    return "255";
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private void show_dialog(int devalue) {
            final SeekBar seekBar = new SeekBar(getActivity());
            getScreenBrightnessMaximum(getContext());
            seekBar.setMax(Integer.parseInt(getScreenBrightnessMaximum(getContext())));
            seekBar.setProgress(devalue);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.brightness_adjustment));

            builder.setView(seekBar);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    ModifySettingsScreenBrightness(getActivity(), i);
                    screen_light.setSummary(getResources().getString(R.string.brightness) + ":" + i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            builder.show();
        }

        private int getScreenOffTime() {
            int screenOffTime = 0;
            try {
                screenOffTime = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.SCREEN_OFF_TIMEOUT);
            } catch (Exception localException) {

            }
            return screenOffTime;
        }


        private void getbattery(BroadcastReceiver receiver) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean present = intent.getBooleanExtra("present", false);
                    if (present) {
                        int rawlevel = intent.getIntExtra("level", -1);//当前电量
                        int scale = intent.getIntExtra("scale", -1);//电量规格
                        //     int status = intent.getIntExtra("status", -1);//状态
                        int health = intent.getIntExtra("health", -1);//健康度
                        int temperature = intent.getIntExtra(EXTRA_TEMPERATURE, -1) / 10; //温度
                        int temp = intent.getIntExtra(EXTRA_TEMPERATURE, -1) % 10; //温度
                        battery_tem.setSummary(context.getResources().getString(R.string.battery_temperature) + ":" + temperature + "." + temp + "℃");
                        ((PreferenceCategory) findPreference("mPreCatefgory")).removePreference(battery_status);
                        battery_level.setSummary(context.getResources().getString(R.string.power_level) + ":" + rawlevel + "%");
                        bat_scale.setSummary(context.getResources().getString(R.string.battery_specification) + ":" + scale);
                        bat_health.setSummary(context.getResources().getString(R.string.health) + ":" + health);
                    } else {
                        battery_status.setSummary(getResources().getString(R.string.no_battery));
                        ((PreferenceCategory) findPreference("mPreCatefgory")).removePreference(battery_tem);
                        ((PreferenceCategory) findPreference("mPreCatefgory")).removePreference(battery_level);
                        ((PreferenceCategory) findPreference("mPreCatefgory")).removePreference(bat_scale);
                        ((PreferenceCategory) findPreference("mPreCatefgory")).removePreference(bat_health);
                    }

                }
            };
        }

        /**
         * CountDownTimer 实现倒计时
         */

        private CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int value = (int) (millisUntilFinished / 1000);
                Reboot.setSummary(getResources().getString(R.string.the_system_will) + value + getResources().getString(R.string.shutdown_restart));

            }

            @Override
            public void onFinish() {
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        };

        //声音类型
        private enum voiceType {
            ring,
            Multimedia,
            clock,
        }

        private void show_audiodialog(int voice, int max_voice, VolumeUtil V, voiceType voicetype) {


            final SeekBar seekBar = new SeekBar(getActivity());
            seekBar.setMax(max_voice);
            seekBar.setProgress(voice);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.sound_size));
            builder.setView(seekBar);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    if (voicetype == voiceType.ring) {
                        V.setringVolume(i);
                        sound_set.setSummary(getResources().getString(R.string.sound_size) + ((i * 100 / max_voice)) + "%");
                    } else if (voicetype == voiceType.Multimedia) {
                        V.setMediaVolume(i);
                        Multimedia.setSummary(getResources().getString(R.string.sound_size) + ((i * 100 / max_voice)) + "%");
                    } else if (voicetype == voiceType.clock) {
                        V.setAlermVolume(i);
                        clock.setSummary(getResources().getString(R.string.sound_size) + ((i * 100 / max_voice)) + "%");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            builder.show();
        }

        private void getaudio() {
            sound_set.setSummary(getActivity().getResources().getString(R.string.sound_size) + volumeUtil.getringVolume() * 100 / volumeUtil.getringmaxVolume() + "%");
            Multimedia.setSummary(getActivity().getResources().getString(R.string.sound_size) + volumeUtil.getMediaVolume() * 100 / volumeUtil.getMediaMaxVolume() + "%");
            clock.setSummary(getActivity().getResources().getString(R.string.sound_size) + volumeUtil.getAlermVolume() * 100 / volumeUtil.getSystemMaxVolume() + "%");
        }

        private void updata_ui() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            if (getWifiState() == 3) {
                                wifi_state.setSummary(getResources().getString(R.string.open) + "");
                                wifi_state.setChecked(true);
                            } else if (getWifiState() == 1) {
                                wifi_state.setSummary(getResources().getString(R.string.close) + "");
                                wifi_state.setChecked(false);
                            }

                            if (adapter != null) {
                                if (adapter.isEnabled()) {
                                    bluetooth.setSummary(getResources().getString(R.string.open) + "");
                                    bluetooth.setChecked(true);

                                } else if (!adapter.isEnabled()) {
                                    bluetooth.setSummary(getResources().getString(R.string.close) + "");
                                    bluetooth.setChecked(false);
                                }
                            }
                            if (isLocationEnabled()) {
                                location_btn.setSummary(getResources().getString(R.string.open));
                                location_btn.setChecked(true);
                            } else {
                                location_btn.setSummary(getResources().getString(R.string.close));
                                location_btn.setChecked(false);
                            }
                            if (Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0) {
                                adb.setSummary(getResources().getString(R.string.open) + "");
                                adb.setChecked(true);
                            } else {
                                adb.setSummary(getResources().getString(R.string.close) + "");
                                adb.setChecked(false);
                            }
                            int screenOffTime = getScreenOffTime();
                            screenOffTime /= 1000;
                            if (screenOffTime < 60) {
                                time_out.setSummary(screenOffTime + getResources().getString(R.string.seconds));
                                if (screenOffTime == 10)
                                    time_out.setValueIndex(0);
                                else if (screenOffTime == 15)
                                    time_out.setValueIndex(1);
                            } else if (screenOffTime >= 60 && screenOffTime <= 30 * 60) {
                                screenOffTime /= 60;
                                time_out.setSummary(screenOffTime + getResources().getString(R.string.minutes));
                                if (screenOffTime == 1)
                                    time_out.setValueIndex(2);
                                else if (screenOffTime == 2)
                                    time_out.setValueIndex(3);
                                else if (screenOffTime == 5)
                                    time_out.setValueIndex(4);
                                else if (screenOffTime == 10)
                                    time_out.setValueIndex(5);
                                else if (screenOffTime == 30)
                                    time_out.setValueIndex(6);
                            } else {
                                time_out.setSummary(getResources().getString(R.string.never_sleep) + "");
                                time_out.setValueIndex(7);
                            }
                            screen_light.setSummary(getResources().getString(R.string.brightness) + ":" + getScreenBrightness() + "");
                            getbattery(receiver);
                            getaudio();
                        }
                    });
                }
            }).start();
        }
    }
}




