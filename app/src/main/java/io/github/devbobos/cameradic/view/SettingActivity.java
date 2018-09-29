package io.github.devbobos.cameradic.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.helper.PreferenceHelper;
import io.github.devbobos.cameradic.helper.TextHelper;
import io.github.devbobos.cameradic.presenter.SettingPresenter;

/**
 * Created by devbobos on 2018. 8. 26..
 */
public class SettingActivity extends AppCompatActivity
{
    @BindView(R.id.setting_textview_regoniationMinValue) TextView textViewRegoniationMinValue;
    @BindView(R.id.setting_textview_dictionaryType) TextView textViewDictionaryType;
    @BindView(R.id.setting_checkbox_autoRecording) CheckBox checkBoxAutoRecording;
    @BindView(R.id.setting_checkbox_showRecognitionMinValue) CheckBox checkBoxShowRecognitionMinValue;
    @BindColor(R.color.colorAvalonYellow) int colorAvalonYellow;
    @BindColor(R.color.colorAvalonDeepYellow) int colorAvalonDeepYellow;

    private int dictionaryType;
    private int recognitionMinValue;
    private SettingPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        presenter = new SettingPresenter(this);
        presenter.setActionBar();
        setContentView(R.layout.setting_activity);
        ButterKnife.bind(this);
        recognitionMinValue = PreferenceHelper.getInstance().getRecognitionMinValue(this);
        textViewRegoniationMinValue.setText(presenter.getRecognitionMinValue(recognitionMinValue));
        dictionaryType = PreferenceHelper.getInstance().getDictionaryType(getApplicationContext());
        textViewDictionaryType.setText(TextHelper.getInstance().getDictionaryType(dictionaryType));
        if(presenter.isAutoRecording())
        {
            checkBoxAutoRecording.setChecked(true);
        }
        else
        {
            checkBoxAutoRecording.setChecked(false);
        }
        if(presenter.isShowRecognitionMinValue())
        {
            checkBoxShowRecognitionMinValue.setChecked(true);
        }
        else
        {
            checkBoxShowRecognitionMinValue.setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        PreferenceHelper.getInstance().setRecognitionMinValue(getApplicationContext(), recognitionMinValue);
        PreferenceHelper.getInstance().setAutoRecording(getApplicationContext(), checkBoxAutoRecording.isChecked()? SystemConstant.AUTO_RECORDING_ENABLED:SystemConstant.AUTO_RECORDING_DISABLED);
        PreferenceHelper.getInstance().setDictionaryType(getApplicationContext(), dictionaryType);
        PreferenceHelper.getInstance().setShowRecognitionValue(getApplicationContext(), checkBoxShowRecognitionMinValue.isChecked()? SystemConstant.SHOW_RECOGNITION_VALUE_ENABLED :SystemConstant.SHOW_RECOGNITION_VALUE_DISABLED);
    }

    @OnClick({R.id.setting_linearlayout_dictionaryType, R.id.setting_linearlayout_regoniationMinValue, R.id.setting_linearlayout_resetDatabase, R.id.setting_linearlayout_license})
    void onMenuClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.setting_linearlayout_regoniationMinValue:
                presenter.showRecognitionMinValueSelector(PreferenceHelper.getInstance().getRecognitionMinValue(getApplicationContext()));
                break;
            case R.id.setting_linearlayout_dictionaryType:
                presenter.showDictionaryTypeSelector(PreferenceHelper.getInstance().getDictionaryType(getApplicationContext()));
                break;
            case R.id.setting_linearlayout_resetDatabase:
                presenter.showResetDatabaseSelector();
                break;
            case R.id.setting_linearlayout_license:
                Intent intent = new Intent(this, LicenseActivity.class);
                startActivity(intent);
                break;
        }
    }
    @OnTouch({R.id.setting_linearlayout_dictionaryType, R.id.setting_linearlayout_regoniationMinValue})
    boolean onMenuTouched(View view, MotionEvent event)
    {
        switch (view.getId())
        {
            case R.id.setting_linearlayout_regoniationMinValue:
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        textViewRegoniationMinValue.setTextColor(colorAvalonYellow);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        textViewRegoniationMinValue.setTextColor(colorAvalonDeepYellow);
                        break;
                }
                break;
            case R.id.setting_linearlayout_dictionaryType:
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        textViewDictionaryType.setTextColor(colorAvalonYellow);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        textViewDictionaryType.setTextColor(colorAvalonDeepYellow);
                        break;
                }
                break;
        }
        return false;
    }

    public void setDictionaryType(int dictionaryType)
    {
        this.dictionaryType = dictionaryType;
        textViewDictionaryType.setText(TextHelper.getInstance().getDictionaryType(dictionaryType));
    }

    public void setRecognitionMinValue(int recognitionMinValue)
    {
        this.recognitionMinValue = recognitionMinValue;
        textViewRegoniationMinValue.setText(presenter.getRecognitionMinValue(recognitionMinValue));
    }
}
