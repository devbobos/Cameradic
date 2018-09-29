package io.github.devbobos.cameradic.view;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.presenter.GuidePresenter;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class GuideActivity extends AppCompatActivity
{
    @BindView(R.id.guide_textview_title) TextView textViewTitle;
    @BindView(R.id.guide_textview_description) TextView textViewDescription;
    @BindView(R.id.guide_imageview) AppCompatImageView imageView;
    @BindColor(R.color.colorAvalonWhite) int colorAvalonWhite;
    @BindColor(R.color.colorAvalonWhitePressed) int colorAvalonWhitePressed;
    @BindString(R.string.guide_permission_info) String stringPermissionInfo;
    @BindString(R.string.guide_api_level_info) String stringApiLevelInfo;
    @BindString(R.string.guide_title_permission) String stringTitlePermission;
    @BindString(R.string.guide_title_destroy) String stringTitleDestroy;
    public static final int REQUEST_CODE_PERMISSION = 1;
    public static final int GUIDE_TYPE_RUNTIME_PERMISSION = 2;
    public static final int GUIDE_TYPE_API_LEVEL = 3;
    private GuidePresenter presenter;
    private int guideType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        presenter = new GuidePresenter(this);
        presenter.hideStatusBar();
        setContentView(R.layout.guide_activity);
        ButterKnife.bind(this);
        guideType = getIntent().getIntExtra(SystemConstant.REQUEST_GUIDE_TYPE, GUIDE_TYPE_API_LEVEL);
        switch (guideType)
        {
            case GUIDE_TYPE_API_LEVEL:
                imageView.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
                textViewTitle.setText(stringTitleDestroy);
                textViewDescription.setText(stringApiLevelInfo);
                break;
            case GUIDE_TYPE_RUNTIME_PERMISSION:
                imageView.setImageResource(R.drawable.ic_camera_alt_black_24dp);
                textViewTitle.setText(stringTitlePermission);
                textViewDescription.setText(stringPermissionInfo);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_CODE_PERMISSION:
                boolean isPermissionGranted = false;
                if(grantResults.length > 0)
                {
                    for(int i = 0; i<grantResults.length; i++)
                    {
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        {
                            isPermissionGranted = true;
                        }
                        else
                        {
                            isPermissionGranted = false;
                        }
                    }
                    if(isPermissionGranted)
                    {
                        Logger.d("permission is granted");
                        presenter.startMainActivity();
                    }
                }
                else
                {
                    Logger.d("permission is denied");
                }
                break;
        }
    }
    @OnClick(R.id.guide_relativelayout_button) void onClickPermisson()
    {
        switch (guideType)
        {
            case GUIDE_TYPE_API_LEVEL:
                presenter.showAlertDialogDestroy();
                break;
            case GUIDE_TYPE_RUNTIME_PERMISSION:
                presenter.showPermissionDialog();
                break;
        }
    }
    @OnTouch(R.id.guide_relativelayout_button) boolean onTouchPermission(View view, MotionEvent event)
    {
        switch (view.getId())
        {
            case R.id.guide_relativelayout_button:
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        textViewDescription.setTextColor(colorAvalonWhitePressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        textViewDescription.setTextColor(colorAvalonWhite);
                        break;
                }
                break;
        }
        return false;
    }
}
