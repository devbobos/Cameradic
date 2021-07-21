package io.github.devbobos.cameradic.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.presenter.SplashPresenter;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class SplashActivity extends AppCompatActivity
{
    private SplashPresenter presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        presenter = new SplashPresenter(this);
        presenter.hideStatusBar();
        setContentView(R.layout.splash_activity);
        presenter.initializeApplication();
    }
}
