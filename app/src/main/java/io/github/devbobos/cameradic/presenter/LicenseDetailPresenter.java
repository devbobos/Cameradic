package io.github.devbobos.cameradic.presenter;

import android.content.Intent;
import android.os.Bundle;

import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.view.LicenseActivity;
import io.github.devbobos.cameradic.view.LicenseDetailActivity;

/**
 * Created by devbobos on 2018. 9. 2..
 */
public class LicenseDetailPresenter
{
    private LicenseDetailActivity activity;

    public LicenseDetailPresenter(LicenseDetailActivity activity)
    {
        this.activity = activity;
    }

    public void setActionBar(Intent intent)
    {
        String title = "";
        if(intent.getStringExtra(SystemConstant.KEY_TITLE)!=null)
        {
            title = intent.getStringExtra(SystemConstant.KEY_TITLE);
        }
        else
        {
            title = activity.getResources().getString(R.string.setting_title_license);
        }
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle(title);
        }
        else
        {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getActionBar().setTitle(title);
        }
    }
    public String getDescription(Intent intent)
    {
        String description = "";
        if(intent.getStringExtra(SystemConstant.KEY_DESCRIPTION)!=null)
        {
            return intent.getStringExtra("description");
        }
        else
        {
            return description;
        }
    }
}
