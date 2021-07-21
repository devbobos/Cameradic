package io.github.devbobos.cameradic.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.presenter.LicenseDetailPresenter;

/**
 * Created by devbobos on 2018. 9. 2..
 */
public class LicenseDetailActivity extends AppCompatActivity
{
     @BindView(R.id.licensedetail_textview) TextView textView;
    private LicenseDetailPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        presenter = new LicenseDetailPresenter(this);
        presenter.setActionBar(getIntent());
        setContentView(R.layout.licensedetail_activity);
        ButterKnife.bind(this);
        textView.setText(presenter.getDescription(getIntent()));
        textView.setMovementMethod(new ScrollingMovementMethod());
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
}
