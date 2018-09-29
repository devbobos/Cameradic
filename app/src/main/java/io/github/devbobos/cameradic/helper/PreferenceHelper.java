package io.github.devbobos.cameradic.helper;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.devbobos.cameradic.constant.SystemConstant;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class PreferenceHelper
{
    private static PreferenceHelper instance;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    private PreferenceHelper() {}
    public static PreferenceHelper getInstance()
    {
        if(instance == null)
        {
            instance = new PreferenceHelper();
        }
        return instance;
    }

    public void clearPreference(Context context)
    {
        editor = context.getSharedPreferences(SystemConstant.VERSION_PREFERENCE, 0).edit();
        editor.clear();
        editor.commit();
    }
    public void setRunningDate(Context context, String yyyymmdd)
    {
        editor = context.getSharedPreferences(SystemConstant.VERSION_PREFERENCE, 0).edit();
        editor.putString("runningDate", yyyymmdd);
        editor.commit();
    }
    public String getRunningDate(Context context)
    {
        preferences = context.getSharedPreferences(SystemConstant.VERSION_PREFERENCE, 0);
        return preferences.getString("runningDate", "");
    }
    public void setRecognitionMinValue(Context context, int value)
    {
        editor = context.getSharedPreferences(SystemConstant.VERSION_PREFERENCE, 0).edit();
        editor.putInt("recognitionMinValue", value);
        editor.commit();
    }
    public int getRecognitionMinValue(Context context)
    {
        preferences = context.getSharedPreferences(SystemConstant.VERSION_PREFERENCE, 0);
        return preferences.getInt("recognitionMinValue", 75);
    }
    public void setShowRecognitionValue(Context context, int value)
    {
        editor = context.getSharedPreferences(SystemConstant.VERSION_PREFERENCE, 0).edit();
        editor.putInt("isShowRecognitionValue", value);
        editor.commit();
    }
    public int getShowRecognitionValue(Context context)
    {
        preferences = context.getSharedPreferences(SystemConstant.VERSION_PREFERENCE, 0);
        return preferences.getInt("isShowRecognitionValue", SystemConstant.SHOW_RECOGNITION_VALUE_DISABLED);
    }
    public void setDictionaryType(Context context, int value)
    {
        editor = context.getSharedPreferences(SystemConstant.VERSION_PREFERENCE, 0).edit();
        editor.putInt("dictionaryType", value);
        editor.commit();
    }
    public int getDictionaryType(Context context)
    {
        preferences = context.getSharedPreferences(SystemConstant.VERSION_PREFERENCE, 0);
        return preferences.getInt("dictionaryType", SystemConstant.LANGUAGE_TYPE_KOREAN);
    }
    public void setAutoRecording(Context context, int value)
    {
        editor = context.getSharedPreferences(SystemConstant.VERSION_PREFERENCE, 0).edit();
        editor.putInt("autoRecording", value);
        editor.commit();
    }
    public int getAutoRecording(Context context)
    {
        preferences = context.getSharedPreferences(SystemConstant.VERSION_PREFERENCE, 0);
        return preferences.getInt("autoRecording", SystemConstant.AUTO_RECORDING_ENABLED);
    }
}
