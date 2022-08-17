package com.advantech.simpale;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class MyPreferenceCategory extends PreferenceCategory {



    public MyPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTextSize(18);//设置title文本的字体大小
            tv.setAllCaps(false);//设置title文本不全为大写
            tv.setBackgroundColor(Color.parseColor("#fafafa"));
            tv.setTextColor(Color.parseColor("#009688"));// 字体颜色
            tv.setSingleLine(true);
            tv.setPadding(tv.getPaddingLeft(), 0, tv.getPaddingLeft(), 0);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));// 字体风格
            tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        //    tv.setTextColor(Color.parseColor("#4878a4"));//设置title文本的颜色
        }
    }




}
