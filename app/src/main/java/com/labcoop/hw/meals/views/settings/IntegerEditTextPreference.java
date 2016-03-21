package com.labcoop.hw.meals.views.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by holcz on 21/03/16.
 */
public class IntegerEditTextPreference extends EditTextPreference {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IntegerEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public IntegerEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IntegerEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntegerEditTextPreference(Context context) {
        super(context);
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        Integer defaultIntReturnValue = 0;
        try{
            defaultIntReturnValue = Integer.valueOf(defaultReturnValue);
        }catch (Exception e){}
        return String.valueOf(getPersistedInt(defaultIntReturnValue));
    }

    @Override
    protected boolean persistString(String value) {
        Integer persistingValue = 0;
        try{
            persistingValue = Integer.valueOf(value);
        }catch (Exception e){}
        return super.persistInt(persistingValue);
    }
}
