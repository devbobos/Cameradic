package io.github.devbobos.cameradic.presenter;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;

import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.view.GuideActivity;
import io.github.devbobos.cameradic.view.camera.CameraActivity;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class GuidePresenter
{
    private GuideActivity activity;

    public GuidePresenter(GuideActivity activity)
    {
        this.activity = activity;
    }

    public void hideStatusBar()
    {
//        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }
    }

    public void showPermissionDialog()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle(activity.getResources().getString(R.string.guide_permission_popup_info))
                .setMessage(activity.getResources().getString(R.string.guide_permission_popup_info_description))
                .setPositiveButton(activity.getResources().getString(R.string.guide_permission_popup_info_accept), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, activity.REQUEST_CODE_PERMISSION);
                    }
                })
                .setNegativeButton(activity.getResources().getString(R.string.guide_permission_popup_info_decline), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        showAlertDialogPermission();
                    }
                })
                .setIcon(activity.getResources().getDrawable(R.drawable.ic_camera_alt_black_24dp))
                .show();
    }
    public void showAlertDialogPermission()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle("경고")
                .setMessage(R.string.guide_permission_popup_deny_description)
                .setPositiveButton(activity.getResources().getString(R.string.guide_permission_popup_deny_accept), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, activity.REQUEST_CODE_PERMISSION);
                    }
                })
                .setNegativeButton(activity.getResources().getString(R.string.guide_permission_popup_deny_decline), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        activity.finish();
                    }
                })
                .setIcon(activity.getResources().getDrawable(R.drawable.ic_warning_black_24dp))
                .show();
    }
    public void showAlertDialogDestroy()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle("경고")
                .setMessage(R.string.guide_permission_popup_api_level)
                .setNegativeButton(activity.getResources().getString(R.string.guide_permission_popup_deny_decline), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        activity.finish();
                    }
                })
                .setIcon(activity.getResources().getDrawable(R.drawable.ic_warning_black_24dp))
                .show();
    }
    public void startMainActivity()
    {
        Intent intent = new Intent(activity, CameraActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
