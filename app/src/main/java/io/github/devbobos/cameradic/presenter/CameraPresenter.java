package io.github.devbobos.cameradic.presenter;

import android.app.Fragment;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;
import com.orhanobut.logger.Logger;

import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.view.camera.CameraActivity;

/**
 * Created by devbobos on 2018. 8. 17..
 */
public class CameraPresenter
{
    private CameraActivity activity;

    public CameraPresenter(CameraActivity activity)
    {
        this.activity = activity;
    }
    public int getProgressDescriptionPosition(int recentPosition)
    {
        String[] textArray = activity.getResources().getStringArray(R.array.camera_progress_description);
        int result = -1;
        while(true)
        {
            result = (int)(Math.random() * textArray.length);
            if(recentPosition!=result)
            {
                break;
            }
        }
//        Logger.d("random "+result);
        return result;
    }
    public String getProgressDescriptionText(int position)
    {
        String[] textArray = activity.getResources().getStringArray(R.array.camera_progress_description);
        return textArray[position];
    }

}
