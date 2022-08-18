# IndustrySDK-simaple

A sample app shows how to use the advindustrysdk.jar and some system api.

### \libs
The directory \libs\ contains a advindustrysdk.jar, you can import the jar to your project to develop, understand functions intuitively.  


AdvIndustrySDK is an android sdk provided by Advantech. **System Permissions API of Android.**

In addition to the functions in the advandustrysdk, this app has the following functions::
  
​	  Time and date setting function
​		Power on and restart function
​		Turn WiFi on or off  function
​		Turn Bluetooth on or off function
​		Turn on or off the functions of positioning and obtaining positioning information
​		Turn on or off functions such as ADB debugging
​		Function of setting sleep duration
​		Function of setting system brightness
​		Function of setting screen orientation
​		Function of obtaining battery information
​	  Set the ringing tone, multimedia alarm clock, sound size, etc

How to implement the specific functions? Please refer to the source code in simple
The following are the names and locations of some methods to implement these functions

### Method Summary

The location of these methods is in the navigationactivity

| Modifier and Type | Method and Description                                       |
| ----------------- | ------------------------------------------------------------ |
| void              | public void reboot(@Nullable String reason)<br />Called when you restart |
| void              | public void shutdown(boolean confirm, String reason, boolean wait)<br />Called when you shut down|
| void              | public boolean setWifiEnabled(boolean enabled)<br />Called when you turn WiFi on or off. |
| void              | public void openGPS(boolean open)<br />Called when you turn positioning on or off |
| void              | public void formListenerGetLocation()<br />Called when you get the location information |
| void              | ModifySettingsScreenBrightness(Context context, int birghtessValue))<br />Called when you set the background brightness. |
| void              | private int getScreenBrightness()<br />Called when you get the background brightness |


belongs to class VolumeUtil.

| Modifier and Type | Method and Description                                       |
| ----------------- | ------------------------------------------------------------ |
| void              | public void setringVolume(int volume)<br />Called when you set the ringtone size. |
| void              | public void setMediaVolume(int volume)<br />Called when you set the multimedia sound size. |
| void              | public void setAlermVolume(int volume)<br />Called when you set the alarm clock sound. |


