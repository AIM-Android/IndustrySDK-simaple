# IndustrySDK-simaple

A sample app shows how to use the advindustrysdk.jar and some system api.

### \libs
The directory \libs\ contains a advindustrysdk.jar, you can import the jar to your project to develop, understand functions intuitively.  


AdvIndustrySDK is an android sdk provided by Advantech. **System Permissions API of Android.**


### Class Summary

| Class         | Description                                |
| ------------- | ------------------------------------------ |
| AdvIndustrySDK | Provide a set of kiosk related operations. |
| AppInstallResultReceiver | A receiver to get the install app result, must register in the call project.<br />Support since v1.1.0. |
| AppUnInstallResultReceiver | A receiver to get the uninstall app result, must register in the call project.<br />Support since v1.1.0. |

### Exception Summary

| Exception                           | Description                                                  |
| ----------------------------------- | ------------------------------------------------------------ |
| NotSystemAppException               | Exception will be thrown when it isn't a system app.          |
| ActivityNotForegroundException      | Exception will be thrown when activity isn't foreground.      |
| PropertiesNotFoundException         | Exception will be thrown when the custom properties were not found. |
| AppLowerThanCurrentVersionException | Exception will be thrown when the apk's version code is lower than the installed one.<br />Support since v1.1.0. |
| InvalidApkFileException             | Exception will be thrown when the file is not an apk file.<br />Support since v1.1.0. |

### Interface Summary

| Interface          | Description                                                  |
| ------------------ | ------------------------------------------------------------ |
| AppManagerListener | A interface for callback when install/uninstall application.<br />Support since v1.1.0. |

### Method Summary

belongs to class AppManagerListener.

| Modifier and Type | Method and Description                                       |
| ----------------- | ------------------------------------------------------------ |
| void              | onInstalled(String filePath)<br />Called when the apk file was installed. |
| void              | onUninstalled(String pkgName)<br />Called when the package was uninstalled |
| void              | onFailed(@AppManagerAction int action, String source, String error)<br />Called when error happened. |

belongs to class AdvIndustrySDK.

| Modifier and Type | Method and Description                                       |
| ----------------- | ------------------------------------------------------------ |
| boolean           | launchApp(@NonNull String pkg)<br />Start the application.<br />Support since v1.1.0. |
| boolean           | forceStopApp(@NonNull String pkg)<br />Force stop the application process.<br />Support since v1.1.0. |
| boolean           | enableApplication(@NonNull String pkg)<br />Enable setting for an application.<br />Support since v1.1.0. |
| boolean           | disableApplication(@NonNull String pkg)<br />Disable setting for an application, the app will be hide, and can't start the app until enable it again.<br />Support since v1.1.0. |
| void              | installOrUpdateApkSilently(@NonNull String filePath, AppManagerListener listener)<br />Install the apk file silently.<br />Support since v1.1.0. |
| void              | uninstallAppSilently(@NonNull String pkg, AppManagerListener listener)<br />Uninstall the app silently.<br />Support since v1.1.0. |
| boolean           | isAppInstalled(@NonNull String packageName)<br />Whether the app has installed.<br />Support since v1.1.0. |
| String            | canInstallOrUpdateApk(@NonNull String filePath)<br />Whether the apk file can install or update, an apk file can be install when it's not installed or it's versioncode is not lower than current installed app.<br />Support since v1.1.0. |
| String            | getApkPkgName(@NonNull String filePath)<br />Get the package name of the apk file.<br />Support since v1.1.0. |
| long              | getApkVersionCode(@NonNull String filePath)<br />gGet the version code of the apk file.<br />Support since v1.1.0. |
| long              | getInstalledAppVersionCode(@NonNull String packageName)<br />Get the version code of the installed app.<br />Support since v1.1.0. |
| static boolean    | setKiosk(@NonNull Activity activity) <br />Lock the Android screen to the task where the target activity is located, and then enter kiosk mode.<br />High safety factor, recommended. |
| static void       | cancelKiosk(@NonNull Activity activity)<br />Leave the kiosk mode set by setKiosk. |
| static boolean    | hideStatusNavBar(@NonNull Context context)<br />Hide the system's status bar and navigation bar. <br />Medium safety factor. |
| static boolean    | showStatusNavBar(@NonNull Context context)<br />Show the system's status bar and navigation bar. |
| static void       | hideStatusNavBarImmersive()<br />Hide the system's status bar and navigation bar immersive, shutdown, sliding up or down will forget the status.<br />Low safety factor. |
| static void       | cancelHideStatusNavBarImmersive()<br />Cancel hiding the bars immersive by hideStatusNavBarImmersive and then restore to default status. |
| static String     | getFullScreenStatus(@NonNull Context context)<br />Get the status of full screen which set by hideStatusNavBar & showStatusNavBar. |
| static String     | getFullScreenStatusImmersive(@NonNull Context context)<br />Get the status of full screen immersive which set by hideStatusNavBarImmersive & cancelHideStatusNavBarImmersive. |

### Method Details

#### 1. Methods belongs to AppManagerListener:

##### onInstalled

```
void onInstalled(String filePath) 
```

Called when the apk file was installed.

Support since v1.1.0

- Parameters:

​		`filePath` - the installed apk file.

##### onUninstalled

```
void onUninstalled(String pkgName) 
```

Called when the package was uninstalled.

Support since v1.1.0

- Parameters:

​		`pkgName` - the uninstalled package name.

##### onFailed

```
void onFailed(@AppManagerAction int action, String source, String error) 
```

Called when error happened.

Support since v1.1.0

- Parameters:

​		`action` - see{@link AppManagerAction}.

​		`source` - package name(uninstall) or apk file path(install).

​		`error` - error info.

#### 2. Methods belongs to AdvIndustrySDK:

##### launchApp

```
public boolean launchApp(@NonNull String pkg) 
```

Start the application.

Support since v1.1.0

- Parameters:

​		`pkg` - package name of the target application.

- Returns:

​		Whether the operation success.

- Throws:

​		NotSystemAppException

##### forceStopApp

```
public boolean forceStopApp(@NonNull String pkg)
```

Force stop the application process.

Support since v1.1.0

- Parameters:

​		`pkg` - package name of the target application.

- Returns:

​		Whether the operation success.

- Throws:

​		NotSystemAppException

##### enableApplication

```
public boolean enableApplication(@NonNull String pkg) 
```

Enable setting for an application.

Support since v1.1.0

- Parameters:

​		`pkg` - package name of the target application.

- Returns:

​		Whether the operation success.

- Throws:

​		NotSystemAppException

##### disableApplication

```
public boolean disableApplication(@NonNull String pkg)
```

Disable setting for an application, the app will be hide, and can't start the app until enable it again.

Support since v1.1.0

- Parameters:

​		`pkg` - package name of the target application.

- Returns:

​		Whether the operation success.

- Throws:

​		NotSystemAppException

##### installOrUpdateApkSilently

```
public void installOrUpdateApkSilently(@NonNull String filePath, AppManagerListener listener) 
```

Install the apk file silently, provided for system app.

Support since v1.1.0

- Parameters:

​		`filePath` - the apk file path ready to install.

​		`listener` - listener{@link AppManagerListener#onInstalled(String)} will be called when install app success, otherwise, {@link AppManagerListener#onFailed(int, String, String)} will call.

- Throws:

​		NotSystemAppException

​		AppLowerThanCurrentVersionException

​		InvalidApkFileException

##### uninstallAppSilently

```
public void uninstallAppSilently(@NonNull String pkg, AppManagerListener listener) 
```

Uninstall the app silently, provided for system app.

Support since v1.1.0

- Parameters:

​		`pkg` - the package name ready to uninstall.

​		`listener` - listener {@link AppManagerListener#onUninstalled(String)} will be called when uninstall app success, otherwise, {@link AppManagerListener#onFailed(int, String, String)} will call.

- Throws:

​		NotSystemAppException

##### isAppInstalled

```
public boolean isAppInstalled(@NonNull String packageName) 
```

Whether the app has installed, only for system app.

Support since v1.1.0

- Parameters:

​		`packageName` - package name of target app.

- Returns:

​		True when the app was installed.

##### canInstallOrUpdateApk

```
public boolean canInstallOrUpdateApk(@NonNull String filePath) 
```

Whether the apk file can install or update, only for system app.

An apk file can be install when it's not installed or it's versioncode is not lower than current installed app.

Support since v1.1.0

- Parameters:

​		`filePath` - file path of target apk file.

- Returns:

​		True when the apk can install or update

##### getApkPkgName

```
public String getApkPkgName(@NonNull String filePath) 
```

Get the package name of the apk file, only for system app.

Support since v1.1.0

- Parameters:

​		`filePath` - file path of target apk file.

- Returns:

​		Package name.

##### getApkVersionCode

```
public long getApkVersionCode(@NonNull String filePath) 
```

Get the version code of the apk file, only for system app.

Support since v1.1.0

- Parameters:

​		`filePath` - file path of target apk file.

- Returns:

​		The version code of the apk file, or -1 when error.

##### getInstalledAppVersionCode

```
public long getInstalledAppVersionCode(@NonNull String packageName)
```

Get the version code of the installed app, only for system app.

Support since v1.1.0

- Parameters:

​		`filePath` - file path of target apk file.

- Returns:

​		The version code of the installed app, or -1 when the app is not install.

##### setKiosk

```
public static boolean setKiosk(@NonNull Activity activity) 
```

Lock the Android screen to the task where the target activity is located, and then enter kiosk mode. You can't leave the activity task util call cancelKiosk.<br />High safety factor, recommended.

- Parameters:

​		`activity` - target activity want to be locked into kiosk mode, must be a foreground activity.

- Returns:

​		Whether the kiosk mode is set successfully.

- Throws:

​		NotSystemAppException

​		ActivityNotForegroundException

​		PropertiesNotFoundException

- Note:

  The app need to be added into the lock task whitelist by a device owner app, then you can enter kiosk mode silently(device owner will be support later). Otherwise, the system will pop up a dialog to request the lock task permission from the user, please don't refuse the request.

##### cancelKiosk

```
public static void cancelKiosk(@NonNull Activity activity) 
```

Leave the kiosk mode set by setKiosk. 

- Parameters:

​		`activity` - activities in lock task which was set by setKiosk previously.

##### hideStatusNavBar

```
public static boolean hideStatusNavBar(@NonNull Context context)
```

Hide the system's status bar and navigation bar.  Some special operations maybe brings you to other application.<br />Medium safety factor.

- Parameters:

​		`context` - a valid Context object.

- Returns:

​		Whether the bars were hide successfully.

- Throws:

​		NotSystemAppException

​		PropertiesNotFoundException

##### showStatusNavBar

```
public static boolean showStatusNavBar(@NonNull Context context)
```

Show the system's status bar and navigation bar.

- Parameters:

​		`context` - a valid Context object.

- Returns:

​		Whether the bars were shown successfully.

##### hideStatusNavBarImmersive

```
public static void hideStatusNavBarImmersive()
```

Hide the system's status bar and navigation bar immersive. Shutdown, sliding up or down will forget the status.<br />Low safety factor.

##### cancelHideStatusNavBarImmersive

```
public static void cancelHideStatusNavBarImmersive()  
```

Cancel hiding the bars immersive by hideStatusNavBarImmersive and then restore to default status(bars are shown always).

##### getFullScreenStatus

```
public static String getFullScreenStatus(@NonNull Context context) 
```

Get the status of full screen which set by hideStatusNavBar & showStatusNavBar.

- Parameters:

​		`context` - a valid Context object.

- Returns:

​		null(can't get status), "false"(bars were hide), "true"(bars were shown, default).

##### getFullScreenStatusImmersive

```
public static String getFullScreenStatusImmersive(@NonNull Context context)
```

Get the status of full screen immersive which set by hideStatusNavBarImmersive & cancelHideStatusNavBarImmersive.

- Parameters:

​		`context` - a valid Context object.

- Returns:

​		null(can't get status), "immersive=*"(bars were hide immersive), "null"(bars were shown always, default).

### Exception Details

##### NotSystemAppException:				

```
public class NotSystemAppException extends Exception
```

Exception will be thrown when it isn't a system app.

Constructor:

`NotSystemAppException(String message)`

- Parameters:

​		`message` - the detail message.

##### ActivityNotForegroundException:		

```
public class ActivityNotForegroundException extends Exception
```

Exception will be thrown when activity isn't foreground.

Constructor:

`ActivityNotForegroundException(String message)`

- Parameters:

​		`message` - the detail message.

##### PropertiesNotFoundException:		

```
public class PropertiesNotFoundException extends Exception
```

Exception will be thrown when the custom properties were not found.

Constructor:

`PropertiesNotFoundExceptionextends(String message)`

- Parameters:

​		`message` - the detail message.

##### AppLowerThanCurrentVersionException:		

```
public class AppLowerThanCurrentVersionException extends Exception
```

Exception will be thrown when the apk's version code is lower than the installed one.

Constructor:

`AppLowerThanCurrentVersionException(String message)`

- Parameters:

​		`message` - the detail message.

##### InvalidApkFileException:		

```
public class InvalidApkFileException extends Exception
```

Exception will be thrown when the file is not an apk file.

Constructor:

`InvalidApkFileException(String message)`

- Parameters:

​		`message` - the detail message.

# Auto Start When Boot Completely

We won't provide the API about app start automatically after boot completely.

If you are interested in this feature, please refer to the sample code which is realized by register the boot broadcast.

# System APIS

Here are some standard APIs provided by Android, you can click the link for more information if you care about it.

AdvIndustrySDK doesn't contains these APIs below.


### Method Summary

belongs to class NavigationActivity.

| Modifier and Type | Method and Description                                       |
| ----------------- | ------------------------------------------------------------ |
| void              | public void reboot(@Nullable String reason)<br />Called when the apk file was installed. |
| void              | public void shutdown(boolean confirm, String reason, boolean wait)<br />Called when the package was uninstalled |
| void              | public boolean setWifiEnabled(boolean enabled)<br />Called when the apk file was installed. |
| void              | public void openGPS(boolean open)<br />Called when the package was uninstalled |
| void              | public void formListenerGetLocation()<br />Called when the package was uninstalled |
| void              | ModifySettingsScreenBrightness(Context context, int birghtessValue))<br />Called when the apk file was installed. |
| void              | private int getScreenBrightness()<br />Called when the package was uninstalled |

belongs to class VolumeUtil.

| Modifier and Type | Method and Description                                       |
| ----------------- | ------------------------------------------------------------ |
| void              | public void setringVolume(int volume)<br />Called when error happened. |
| void              | public void setMediaVolume(int volume)<br />Called when the apk file was installed. |
| void              | public void setAlermVolume(int volume)<br />Called when error happened. |


### Reboot & Shutdown

```
public void reboot(@Nullable String reason)
```

- Link:

​		https://developer.android.google.cn/reference/android/os/PowerManager#reboot(java.lang.String)

```
public void shutdown(boolean confirm, String reason, boolean wait)
```

- Link:

  android.os.PowerManager

### About WIFI

```
public boolean setWifiEnabled(boolean enabled)
```

- Link:

​		https://developer.android.google.cn/reference/android/net/wifi/WifiManager#setWifiEnabled(boolean)

```
public void connect(@NonNull WifiConfiguration config, @Nullable ActionListener listener)
```

- Link:

​		android.net.wifi.WifiManager

```
public void connect(int networkId, @Nullable ActionListener listener)
```

- Link:

​		android.net.wifi.WifiManager

```
public int addNetwork (WifiConfiguration config)
```

- Link:

​		https://developer.android.google.cn/reference/android/net/wifi/WifiManager#addNetwork(android.net.wifi.WifiConfiguration)

```
public boolean disconnect ()
```

- Link:

​		https://developer.android.google.cn/reference/android/net/wifi/WifiManager#disconnect()

```
public boolean removeNetwork (int netId)
```

- Link:

​		https://developer.android.google.cn/reference/android/net/wifi/WifiManager#removeNetwork(int)

### About Bluetooth

```
  public void setbluetooth(boolean state)
```

- Link:

​		https://developer.android.google.cn/reference/android/bluetooth/BluetoothAdapter#enable()



### About Location

 
```
public void openGPS(boolean open)
```

```
public void formListenerGetLocation()
```

```
public static final String LOCATION_MODE
```


```
public static final String LOCATION_MODE
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.Secure#LOCATION_MODE

```
public static final int LOCATION_MODE_BATTERY_SAVING
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.Secure#LOCATION_MODE_BATTERY_SAVING

```
public static final int LOCATION_MODE_HIGH_ACCURACY
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.Secure#LOCATION_MODE_HIGH_ACCURACY

```
public static final int LOCATION_MODE_OFF
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.Secure#LOCATION_MODE_OFF

```
public static final int LOCATION_MODE_SENSORS_ONLY
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.Secure#LOCATION_MODE_SENSORS_ONLY

### About ADB

#### Open adb
```
 Settings.Global.putInt(getActivity().getContentResolver(), Settings.Global.ADB_ENABLED, 1);
```

#### Close adb
```
 Settings.Global.putInt(getActivity().getContentResolver(), Settings.Global.ADB_ENABLED, 0);
```


```
public static final String DEVELOPMENT_SETTINGS_ENABLED
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.Global#DEVELOPMENT_SETTINGS_ENABLED

```
public static final String ADB_ENABLED
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.Global#ADB_ENABLED

### System Setting


#### Time setting
```
 Settings.System.putInt(getActivity().getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT,time);
```
#### Get brightness

```
private int getScreenBrightness()
```

#### Set up brightness
```
private void ModifySettingsScreenBrightness(Context context, int birghtessValue)
```

```
public static final String SCREEN_OFF_TIMEOUT
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.System#SCREEN_OFF_TIMEOUT

```
public static final String AUTO_TIME
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.System#AUTO_TIME

```
public static final String AUTO_TIME_ZONE
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.System#AUTO_TIME_ZONE

```
public static final String SCREEN_BRIGHTNESS_MODE
```

```
public static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC
```

```
public static final int SCREEN_BRIGHTNESS_MODE_MANUAL
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.System#SCREEN_BRIGHTNESS_MODE

```
public static final String ACCELEROMETER_ROTATION
```

- Link:

​		https://developer.android.google.cn/reference/android/provider/Settings.System#ACCELEROMETER_ROTATION

#### Battery Setting

##### get Battery power
```
intent.getIntExtra("level", -1)
```

##### get Battery scale
```
intent.getIntExtra("scale", -1)
```

##### get Battery status
```
intent.getIntExtra("status", -1)
```

##### get Battery health
```
intent.getIntExtra("health", -1)
```

##### get Battery temperature
```
 intent.getIntExtra(EXTRA_TEMPERATURE, -1)
```

#### Sound Setting

##### Set ring

```
public void setringVolume(int volume)
```

##### Set media

```
public void setMediaVolume(int volume)
```
##### Set clock

```
public void setAlermVolume(int volume)
```


