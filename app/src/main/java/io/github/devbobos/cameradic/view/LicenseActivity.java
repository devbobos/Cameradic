package io.github.devbobos.cameradic.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.adapter.LicenseAdapter;
import io.github.devbobos.cameradic.helper.DatabaseHelper;
import io.github.devbobos.cameradic.presenter.LicensePresenter;

/**
 * Created by devbobos on 2018. 9. 2..
 */
public class LicenseActivity extends AppCompatActivity
{
     @BindView(R.id.license_recyclerview) RecyclerView recyclerView;
    private LicensePresenter presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        presenter = new LicensePresenter(this);
        presenter.setActionBar();
        setContentView(R.layout.license_activity);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LicenseAdapter(this, DatabaseHelper.getInstance(getApplicationContext()).getLicenseDao().loadAll()));
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
