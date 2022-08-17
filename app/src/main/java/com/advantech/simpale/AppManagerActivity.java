package com.advantech.simpale;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.advantech.advindustrysdk.AdvIndustrySDK;
import com.advantech.advindustrysdk.excption.InvalidApkFileException;
import com.advantech.advindustrysdk.excption.NotSystemAppException;
import com.advantech.advindustrysdk.excption.AppLowerThanCurrentVersionException;



public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener, AdvIndustrySDK.AppManagerListener {

    public static final String TAG = "AppManagerActivity";

    String apkPath = "/sdcard/123.apk";
    String pkg = "com.android.settings";

    EditText apkPathEt, appPkgEt;
    Button installBt, uninstallBt, launchBt, forcestopBt, enableBt, disableBt;
    TextView appStatusTv;


    AdvIndustrySDK advKioskUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        getSupportActionBar().setTitle("AppManagerActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appStatusTv = findViewById(R.id.app_status);

        apkPathEt = findViewById(R.id.apk_filepath);
        apkPathEt.setText(apkPath);
        appPkgEt = findViewById(R.id.app_pkg);
        appPkgEt.setText(pkg);

        installBt = findViewById(R.id.install_apk);
        uninstallBt = findViewById(R.id.uninstall_app);
        launchBt = findViewById(R.id.launch_app);
        forcestopBt = findViewById(R.id.forcestop_app);
        enableBt = findViewById(R.id.enable_app);
        disableBt = findViewById(R.id.disable_app);
        installBt.setOnClickListener(this);
        uninstallBt.setOnClickListener(this);
        launchBt.setOnClickListener(this);
        forcestopBt.setOnClickListener(this);
        enableBt.setOnClickListener(this);
        disableBt.setOnClickListener(this);

        advKioskUtils = AdvIndustrySDK.getInstance(this);
    }

    @Override
    protected void onDestroy() {
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
            case R.id.launch_app:
                try {
                    boolean b = advKioskUtils.launchApp(appPkgEt.getText().toString());
                    Toast.makeText(this, "launch "+appPkgEt.getText().toString()+ (b?" success!":" failed!"), Toast.LENGTH_SHORT).show();
                } catch (NotSystemAppException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "not system app", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.forcestop_app:
                try {
                    boolean b = advKioskUtils.forceStopApp(appPkgEt.getText().toString());
                    Toast.makeText(this, "forceStop "+appPkgEt.getText().toString()+ (b?" success!":" failed!"), Toast.LENGTH_SHORT).show();
                } catch (NotSystemAppException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "not system app", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.enable_app:
                try {
                    boolean b = advKioskUtils.enableApplication(appPkgEt.getText().toString());
                    Toast.makeText(this, "enable "+appPkgEt.getText().toString()+ (b?" success!":" failed!"), Toast.LENGTH_SHORT).show();
                } catch (NotSystemAppException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "not system app", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.disable_app:
                try {
                    boolean b = advKioskUtils.disableApplication(appPkgEt.getText().toString());
                    Toast.makeText(this, "disable "+appPkgEt.getText().toString()+ (b?" success!":" failed!"), Toast.LENGTH_SHORT).show();
                } catch (NotSystemAppException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "not system app", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.install_apk:
                try {
                    appStatusTv.setText("Installing "+apkPathEt.getText().toString()+"...");
                    installBt.setBackgroundColor(getResources(). getColor(R.color.Grey200));
                    installBt.setTextColor(getResources().getColor(R.color.Blue900));
                    installBt.setEnabled(false);

                    advKioskUtils.installOrUpdateApkSilently(apkPathEt.getText().toString(), this);
                } catch (AppLowerThanCurrentVersionException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onClick: 不支持版本倒退");
                    appStatusTv.setText("Install "+apkPathEt.getText().toString()+" Failed!" + "\n" + "error: "+e.getMessage());

                    installBt.setBackgroundColor(getResources(). getColor(R.color.Blue900));
                    installBt.setTextColor(getResources().getColor(R.color.white));
                    installBt.setEnabled(true);
                } catch (NotSystemAppException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onClick: 非系统应用！");
                    appStatusTv.setText("Install "+apkPathEt.getText().toString()+" Failed!" + "\n" + "error: "+e.getMessage());

                    installBt.setBackgroundColor(getResources(). getColor(R.color.Blue900));
                    installBt.setTextColor(getResources().getColor(R.color.white));
                    installBt.setEnabled(true);
                } catch (InvalidApkFileException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onClick: invalid apk file！");
                    appStatusTv.setText("Install "+apkPathEt.getText().toString()+" Failed!" + "\n" + "error: "+e.getMessage());

                    installBt.setBackgroundColor(getResources(). getColor(R.color.Blue900));
                    installBt.setTextColor(getResources().getColor(R.color.white));
                    installBt.setEnabled(true);
                }
                break;
            case R.id.uninstall_app:
                try {
                    appStatusTv.setText("Uninstalling "+appPkgEt.getText().toString()+"...");
                    uninstallBt.setBackgroundColor(getResources(). getColor(R.color.Grey200));
                    uninstallBt.setTextColor(getResources().getColor(R.color.Blue900));
                    uninstallBt.setEnabled(false);
                    advKioskUtils.uninstallAppSilently(appPkgEt.getText().toString(), this);
                } catch (NotSystemAppException e) {
                    e.printStackTrace();
                    appStatusTv.setText("Install "+appPkgEt.getText().toString()+" Failed!" + "\n" + "error: "+e.getMessage());

                    uninstallBt.setBackgroundColor(getResources(). getColor(R.color.Blue900));
                    uninstallBt.setTextColor(getResources().getColor(R.color.white));
                    uninstallBt.setEnabled(true);
                }
                break;
        }
    }

    @Override
    public void onInstalled(String filePath) {
        Log.d(TAG, "onInstalled: "+filePath);
        appStatusTv.setText("Install "+filePath+" succeed!");
        installBt.setBackgroundColor(getResources(). getColor(R.color.Blue900));
        installBt.setTextColor(getResources().getColor(R.color.white));
        installBt.setEnabled(true);
    }

    @Override
    public void onUninstalled(String pkgName) {
        Log.d(TAG, "onUninstalled: "+pkgName);
        appStatusTv.setText("Uninstall "+pkgName+" succeed!");
        uninstallBt.setBackgroundColor(getResources().getColor(R.color.Blue900));
        uninstallBt.setTextColor(getResources().getColor(R.color.white));
        uninstallBt.setEnabled(true);
    }

    @Override
    public void onFailed(int action, String source, String error) {
        Log.d(TAG, "onFailed: action = "+action+", source = "+source+", error = "+error);
        if(action == AdvIndustrySDK.AppManagerAction.ACTION_INSTALL){
            String apkFilePath = source;
            appStatusTv.setText("Install "+apkFilePath+" Failed!" + "\n" + "error: "+error);
            installBt.setBackgroundColor(getResources(). getColor(R.color.Blue900));
            installBt.setTextColor(getResources().getColor(R.color.white));
            installBt.setEnabled(true);
        }else if(action == AdvIndustrySDK.AppManagerAction.ACTION_UNINSTALL){
            String pkgName = source;
            appStatusTv.setText("Uninstall "+pkgName+" Failed!" + "\n" + "error: "+error);
            uninstallBt.setBackgroundColor(getResources(). getColor(R.color.Blue900));
            uninstallBt.setTextColor(getResources().getColor(R.color.white));
            uninstallBt.setEnabled(true);
        }
    }
}