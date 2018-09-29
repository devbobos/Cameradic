package io.github.devbobos.cameradic.presenter;

import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.view.LicenseActivity;

/**
 * Created by devbobos on 2018. 9. 2..
 */
public class LicensePresenter
{
    private LicenseActivity activity;

    public LicensePresenter(LicenseActivity activity)
    {
        this.activity = activity;
    }

    public void setActionBar()
    {
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle(activity.getResources().getString(R.string.setting_title_license));
        }
        else
        {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getActionBar().setTitle(activity.getResources().getString(R.string.setting_title_license));
        }
    }
}
