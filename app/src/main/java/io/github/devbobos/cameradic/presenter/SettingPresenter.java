package io.github.devbobos.cameradic.presenter;

import android.content.DialogInterface;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import android.widget.NumberPicker;

import com.orhanobut.logger.Logger;

import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.helper.DatabaseHelper;
import io.github.devbobos.cameradic.helper.PreferenceHelper;
import io.github.devbobos.cameradic.view.SettingActivity;

/**
 * Created by devbobos on 2018. 8. 26..
 */
public class SettingPresenter
{
    private SettingActivity activity;

    public SettingPresenter(SettingActivity activity)
    {
        this.activity = activity;
    }
    public void setActionBar()
    {
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle(activity.getResources().getString(R.string.setting_title));
        }
        else
        {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getActionBar().setTitle(activity.getResources().getString(R.string.setting_title));
        }
    }
    public String getRecognitionMinValue(int value)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(value);
        builder.append("%");
        return builder.toString();
    }
    public boolean isAutoRecording()
    {
        if(PreferenceHelper.getInstance().getAutoRecording(activity.getApplicationContext()) == SystemConstant.AUTO_RECORDING_ENABLED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean isShowRecognitionMinValue()
    {
        if(PreferenceHelper.getInstance().getShowRecognitionValue(activity.getApplicationContext()) == SystemConstant.SHOW_RECOGNITION_VALUE_ENABLED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public void showRecognitionMinValueSelector(int recentValue)
    {
        final NumberPicker numberPicker = new NumberPicker(activity);
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle(activity.getResources().getString(R.string.setting_popup_recognition_title));
        builder.setMessage(activity.getResources().getString(R.string.setting_popup_recognition_description));
        builder.setPositiveButton(activity.getResources().getString(R.string.setting_popup_accept), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Logger.d("setRecognitionMinValue : "+numberPicker.getValue());
                activity.setRecognitionMinValue(numberPicker.getValue());
            }
        });
        builder.setNegativeButton(activity.getResources().getString(R.string.setting_popup_decline), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });
        numberPicker.setMaxValue(99);
        numberPicker.setMinValue(60);
        numberPicker.setValue(recentValue);
        builder.setView(numberPicker);
        builder.create();
        builder.show();
    }
    public void showDictionaryTypeSelector(int recentValue)
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        CharSequence items[] = new CharSequence[]{SystemConstant.LANGUAGE_STRING_KOREAN,
                SystemConstant.LANGUAGE_STRING_ENGLISH,
                SystemConstant.LANGUAGE_STRING_JAPANESE,
                SystemConstant.LANGUAGE_STRING_CHINESE,
                SystemConstant.LANGUAGE_STRING_RUSSIAN};
        builder.setTitle(activity.getResources().getString(R.string.setting_popup_dictionary_title));
        builder.setSingleChoiceItems(items, recentValue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Logger.d("dictionary type : "+which);
                activity.setDictionaryType(which);
            }
        });
        builder.setPositiveButton(activity.getResources().getString(R.string.setting_popup_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });
        builder.setNegativeButton(activity.getResources().getString(R.string.setting_popup_decline), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });
        builder.create();
        builder.show();
    }
    public void showResetDatabaseSelector()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle(activity.getResources().getString(R.string.setting_popup_resetdatabase_title));
        builder.setMessage(activity.getResources().getString(R.string.setting_popup_resetdatabase_description));
        builder.setPositiveButton(activity.getResources().getString(R.string.setting_popup_reset), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                DatabaseHelper.getInstance(activity.getApplicationContext()).resetHistoryDatabase();
                return;
            }
        });
        builder.setNegativeButton(activity.getResources().getString(R.string.setting_popup_decline), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });
        builder.create();
        builder.show();
    }
}
