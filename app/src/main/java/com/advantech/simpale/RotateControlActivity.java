package com.advantech.simpale;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;




public class RotateControlActivity extends Activity {
    private SwitchButton mRotationSwitchButton[];
    private static final int ROTATION_NUMBER = 4;
    private SharePreferenceUtil privateData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_control);
        initView();
    }

    private void initView() {
        privateData = new SharePreferenceUtil(getApplicationContext(), SharePreferenceUtil.PRIVATEDATA, Context.MODE_PRIVATE);

        mRotationSwitchButton = new SwitchButton[ROTATION_NUMBER];

        final int[] idsRotationSwitchButton = {R.id.shortcut_landscape_sb, R.id.shortcut_portrait_sb,
                R.id.shortcut_reverse_landscape_sb, R.id.shortcut_reverse_portrait_sb};

        for (int i = 0; i < ROTATION_NUMBER; i++) {
            mRotationSwitchButton[i] = findViewById(idsRotationSwitchButton[i]);
            final int ids = idsRotationSwitchButton[i];
            final int index = i;
            mRotationSwitchButton[i].setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if (isChecked) {
                        changedRotation(ids, index);
                    }
                }
            });
        }

        SwitchButton mKeepRotateOnbootSwitchButton = findViewById(R.id.shortcut_keep_rotate_onboot_sb);
        mKeepRotateOnbootSwitchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    privateData.put("keep_orientation", "true");
                } else {
                    privateData.put("keep_orientation", "false");
                }
            }
        });


        int orientation = (int) privateData.getSharedPreference("orientation", -1);
        int index = -1;
        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                index = 0;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                index = 1;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                index = 2;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                index = 3;
                break;
            default:
        }

        if (index != -1) {
            for (int i = 0; i < ROTATION_NUMBER; i++) {
                if (index == i) {
                    if (!mRotationSwitchButton[i].isChecked()) {
                        if (getRequestedOrientation() == orientation) {
                            mRotationSwitchButton[i].setChecked(true);
                            mRotationSwitchButton[i].setEnabled(false);
                        } else {
                            mRotationSwitchButton[i].toggle(false);
                        }
                    }
                } else {
                    if (mRotationSwitchButton[i].isChecked()) {
                        mRotationSwitchButton[i].setChecked(false);
                    }
                }
            }
        }

        String keep_orientation = (String) privateData.getSharedPreference("keep_orientation", "false");
        if (keep_orientation != null && keep_orientation.equals("true")) {
            mKeepRotateOnbootSwitchButton.setChecked(true);
        }
    }

    private void changedRotation(int ids, int index) {
        for (int i = 0; i < ROTATION_NUMBER; i++) {
            if (index == i) {
                mRotationSwitchButton[i].setEnabled(false);
            } else {
                mRotationSwitchButton[i].setEnabled(true);
                if (mRotationSwitchButton[i].isChecked()) {
                    mRotationSwitchButton[i].toggle(false);
                }
            }
        }

        int orientation = -1;
        switch (ids) {
            case R.id.shortcut_landscape_sb:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case R.id.shortcut_portrait_sb:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case R.id.shortcut_reverse_landscape_sb:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            case R.id.shortcut_reverse_portrait_sb:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
        }

        if (orientation != -1) {
            setRequestedOrientation(orientation);
            privateData.put("orientation", orientation);
            setOrientation(this, orientation);
        }
    }


    public static void setOrientation(Context context, int orientation) {
        if (orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE &&
                orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT &&
                orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE &&
                orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            return;
        }

        LinearLayout orientationChanger = new LinearLayout(context);
        WindowManager.LayoutParams orientationLayout = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0, PixelFormat.RGBA_8888);
        orientationLayout.screenOrientation = orientation;
        WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        if (wm != null) {
            wm.addView(orientationChanger, orientationLayout);
            orientationChanger.setVisibility(View.VISIBLE);
        }
    }

}
