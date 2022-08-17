package com.advantech.simpale;

import android.app.ActivityOptions;
import android.app.admin.DevicePolicyManager;
import android.app.admin.FreezePeriod;
import android.app.admin.SystemUpdatePolicy;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.advantech.advindustrysdk.AdvIndustrySDK;
import com.advantech.advindustrysdk.excption.ActivityNotForegroundException;
import com.advantech.advindustrysdk.excption.NotSystemAppException;
import com.advantech.advindustrysdk.excption.PropertiesNotFoundException;



import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Google lock task: here!
 * https://developer.android.google.cn/work/dpc/dedicated-devices/lock-task-mode#java
 */
public class KioskActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "MainActivity";

    TextView statusTv;
    Button startKioskBt, stopKioskBt;
    CheckBox autoStartCb;

    SharePreferenceUtil sp;


    /**
     * 设置系统更新冻结期（最大90天），不接受任何系统更新，适用于假日/繁忙期某些场景。
     * ---> https://developer.android.google.cn/work/dpc/system-updates#freeze-periods
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    void freezeOSUpdate(){
        DevicePolicyManager dpm = null;
        // Get the existing policy from the DevicePolicyController instance.
        SystemUpdatePolicy policy = dpm.getSystemUpdatePolicy();

        try {
            // Set the two annual freeze periods on the policy for our
            // retail point-of-sale devices.
            FreezePeriod summerSale = new FreezePeriod(
                    MonthDay.of(6, 1),
                    MonthDay.of(7, 31)); // Jun 1 - Jul 31 inclusive
            FreezePeriod winterSale = new FreezePeriod(
                    MonthDay.of(11, 20),
                    MonthDay.of(1, 12)); // Nov 20 - Jan 12 inclusive
            policy.setFreezePeriods(Arrays.asList(summerSale, winterSale));

            // Don’t forget to set the policy again to activate the freeze periods.
            dpm.setSystemUpdatePolicy(null, policy);

        } catch (SystemUpdatePolicy.ValidationFailedException e) {
            // There must be previous periods recorded on the device because summerSale
            // and winterSale don’t overlap and are separated by more than 60 days.
            // Report the overlap ...
        }
    }

    /**
     * 禁止second user的登出系统UI；
     * 保留一部分apk，second user直接安装
     * ---> https://developer.android.google.cn/work/dpc/dedicated-devices/multiple-users
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    void multipleUser(){
        DevicePolicyManager dpm = null;
        dpm.setLogoutEnabled(null, false);

        // 定制系统切换用户的message
        dpm.setStartUserSessionMessage(null, "START_USER_SESSION_MESSAGE");
        dpm.setEndUserSessionMessage(null, "END_USER_SESSION_MESSAGE");


        // Set the package to keep. This method assumes that the package is already
        // installed on the device by managed Google Play.
        String cachedAppPackageName = "com.example.android.myapp";
        List<String> packages = new ArrayList<String>();
        packages.add(cachedAppPackageName);
        dpm.setKeepUninstalledPackages(null, packages);

        // ...

        // The admin of a secondary user installs the app.
        boolean success = dpm.installExistingPackage(null, cachedAppPackageName);
    }

    /**
     * 启动一个已在白名单的应用页面，更多细节看这里:
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    void lockTaskTest(){
        String KIOSK_PACKAGE = "";

        // Set an option to turn on lock task mode when starting the activity.
        ActivityOptions options = ActivityOptions.makeBasic();
        options.setLockTaskEnabled(true);

        // Start our kiosk app's main activity with our lock task mode option.
        PackageManager packageManager = getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage(KIOSK_PACKAGE);
        if (launchIntent != null) {
            startActivity(launchIntent, options.toBundle());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk);

        getSupportActionBar().setTitle("KioskActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        statusTv = findViewById(R.id.kiosk_status);
        startKioskBt = findViewById(R.id.start_kiosk);
        stopKioskBt = findViewById(R.id.stop_kiosk);
        autoStartCb = findViewById(R.id.auto_start_cb);
//        findViewById(R.id.jump).setOnClickListener(this);
        startKioskBt.setOnClickListener(this);
        stopKioskBt.setOnClickListener(this);

        startKioskBt.setBackgroundColor(getResources().getColor(R.color.Blue700));
        stopKioskBt.setBackgroundColor(getResources().getColor(R.color.Blue700));
        startKioskBt.setTextColor(getResources().getColor(R.color.white));
        stopKioskBt.setTextColor(getResources().getColor(R.color.white));

        sp = new SharePreferenceUtil(this, SharePreferenceUtil.KIOSK_STATUS, MODE_PRIVATE);
        if((boolean)sp.getSharedPreference("auto_start_when_boot", false)){
            autoStartCb.setChecked(true);
        }else {
            autoStartCb.setChecked(false);
        }

        if((boolean)sp.getSharedPreference("kiosk_status", false)){
            stopKioskBt.setBackgroundColor(getResources().getColor(R.color.Blue700));
            stopKioskBt.setTextColor(getResources().getColor(R.color.white));
            stopKioskBt.setEnabled(true);
            startKioskBt.setBackgroundColor(getResources().getColor(R.color.Grey200));
            startKioskBt.setTextColor(getResources().getColor(R.color.Blue900));
            startKioskBt.setEnabled(false);
            statusTv.setText("Entered Kiosk Mode");
        } else {
            startKioskBt.setBackgroundColor(getResources().getColor(R.color.Blue700));
            startKioskBt.setTextColor(getResources().getColor(R.color.white));
            startKioskBt.setEnabled(true);
            stopKioskBt.setBackgroundColor(getResources().getColor(R.color.Grey200));
            stopKioskBt.setTextColor(getResources().getColor(R.color.Blue900));
            stopKioskBt.setEnabled(false);
            statusTv.setText("Not In Kiosk Mode");
        }

        if((boolean)sp.getSharedPreference("kiosk_status", false) &&
                getIntent().getBooleanExtra("boot_start", false)){
            enterKiosk();
        }

        autoStartCb.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        //when reboot or shutdown, exit kiosk mode.
//        if((boolean)sp.getSharedPreference("kiosk_status", false)){
//            exitKiosk();
//        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 处理返回逻辑
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_kiosk:
                enterKiosk();
                break;
            case R.id.stop_kiosk:
                exitKiosk();
                break;
//            case R.id.jump:
//                startActivity(new Intent(KioskActivity.this, AppManagerActivity.class));
//                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sp.put("auto_start_when_boot", isChecked);
        if((boolean)sp.getSharedPreference("auto_start_when_boot", false) == isChecked){
            Toast.makeText(this, "success!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "failed!", Toast.LENGTH_SHORT).show();
            autoStartCb.setChecked(false);
        }
    }


    private void enterKiosk(){
        Log.d(TAG, "enterKiosk: call");
        startKioskBt.setBackgroundColor(getResources(). getColor(R.color.Grey200));
        startKioskBt.setTextColor(getResources().getColor(R.color.Blue900));
        startKioskBt.setEnabled(false);
        stopKioskBt.setBackgroundColor(getResources().getColor(R.color.Grey200));
        stopKioskBt.setTextColor(getResources().getColor(R.color.Blue900));
        stopKioskBt.setEnabled(false);

        boolean inKiosk = false;
        try {
            inKiosk = AdvIndustrySDK.setKiosk(this);
        } catch (NotSystemAppException | ActivityNotForegroundException | PropertiesNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "enterKiosk: exception = "+e.getClass().getSimpleName()+" : "+e.getMessage());
            //TODO show a toast
            Toast.makeText(this, "Enter kiosk failed:"+e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if(!inKiosk){
                Log.d(TAG, "enterKiosk: failed");
                startKioskBt.setBackgroundColor(getResources().getColor(R.color.Blue700));
                startKioskBt.setTextColor(getResources().getColor(R.color.white));
                startKioskBt.setEnabled(true);
                statusTv.setText("Not In Kiosk Mode");

                sp.put("kiosk_status", false);
            }else {
                Log.d(TAG, "enterKiosk: success");
                stopKioskBt.setBackgroundColor(getResources().getColor(R.color.Blue700));
                stopKioskBt.setTextColor(getResources().getColor(R.color.white));
                stopKioskBt.setEnabled(true);
                statusTv.setText("Entered Kiosk Mode");

                sp.put("kiosk_status", true);
            }
        }
    }

    private void exitKiosk(){
        Log.d(TAG, "exitKiosk: call");

        startKioskBt.setBackgroundColor(getResources().getColor(R.color.Grey200));
        startKioskBt.setTextColor(getResources().getColor(R.color.Blue900));
        startKioskBt.setEnabled(false);
        stopKioskBt.setBackgroundColor(getResources().getColor(R.color.Grey200));
        stopKioskBt.setTextColor(getResources().getColor(R.color.Blue900));
        stopKioskBt.setEnabled(false);

        AdvIndustrySDK.cancelKiosk(this);

        startKioskBt.setBackgroundColor(getResources().getColor(R.color.Blue700));
        startKioskBt.setTextColor(getResources().getColor(R.color.white));
        startKioskBt.setEnabled(true);
        statusTv.setText("Not In Kiosk Mode");

        sp.put("kiosk_status", false);
    }

}