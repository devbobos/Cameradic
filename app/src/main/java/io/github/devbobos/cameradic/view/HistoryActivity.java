package io.github.devbobos.cameradic.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.adapter.HistoryAdapter;
import io.github.devbobos.cameradic.presenter.HistoryPresenter;

/**
 * Created by devbobos on 2018. 8. 17..
 */
public class HistoryActivity extends AppCompatActivity
{
     @BindView(R.id.history_linearlayout_list) LinearLayout linearLayoutList;
     @BindView(R.id.history_linearlayout_favorite) LinearLayout linearLayoutFavorite;
     @BindView(R.id.history_recyclerview) RecyclerView recyclerView;
     @BindView(R.id.history_linearlayout_information) LinearLayout linearLayoutInformation;
     @BindView(R.id.history_imageview_information) AppCompatImageView appCompatImageViewInformation;
     @BindView(R.id.history_textview_information) TextView textViewInformation;
    @BindColor(R.color.colorAvalonWhite) int colorAvalonWhite;
    @BindColor(R.color.colorAvalonWhitePressed) int colorAvalonWhitePressed;
    @BindColor(R.color.colorAvalonWhiteActionDown) int colorAvalonWhiteActionDown;
    private HistoryPresenter presenter;
    private int historyMode;
    public static final int HISTORY_MODE_LIST = 1;
    public static final int HISTORY_MODE_FAVORITE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        presenter = new HistoryPresenter(this);
        presenter.setActionBar();
        setContentView(R.layout.history_activity);
        ButterKnife.bind(this);
        historyMode = HISTORY_MODE_LIST;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        switch (historyMode)
        {
            case HISTORY_MODE_LIST:
                if(presenter.getHistoryList().size() > 0)
                {
                    linearLayoutInformation.setVisibility(View.GONE);
                    appCompatImageViewInformation.setImageResource(R.drawable.ic_sentiment_satisfied_black_24dp);
                    recyclerView.setAdapter(new HistoryAdapter(presenter.getHistoryList(), this));
                }
                else
                {
                    linearLayoutInformation.setVisibility(View.VISIBLE);
                }
                break;
            case HISTORY_MODE_FAVORITE:
                if(presenter.getHistoryList().size() > 0)
                {
                    linearLayoutInformation.setVisibility(View.GONE);
                    appCompatImageViewInformation.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
                    recyclerView.setAdapter(new HistoryAdapter(presenter.getHistoryFavoriteList(), this));
                }
                else
                {
                    linearLayoutInformation.setVisibility(View.VISIBLE);
                }
                break;
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
            case R.id.menu_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public int getHistoryMode()
    {
        return historyMode;
    }

    @OnClick({R.id.history_linearlayout_list, R.id.history_linearlayout_favorite}) void onMenuClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.history_linearlayout_list:
                historyMode = HISTORY_MODE_LIST;
                recyclerView.setAdapter(new HistoryAdapter(presenter.getHistoryList(), this));
                break;
            case R.id.history_linearlayout_favorite:
                historyMode = HISTORY_MODE_FAVORITE;
                recyclerView.setAdapter(new HistoryAdapter(presenter.getHistoryFavoriteList(), this));
                break;
        }
    }
    @OnTouch({R.id.history_linearlayout_list, R.id.history_linearlayout_favorite}) boolean onMenuTouched(View view, MotionEvent event)
    {
        switch (view.getId())
        {
            case R.id.history_linearlayout_list:
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        linearLayoutList.setBackgroundColor(colorAvalonWhiteActionDown);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        linearLayoutList.setBackgroundColor(colorAvalonWhitePressed);
                        linearLayoutFavorite.setBackgroundColor(colorAvalonWhite);
                        break;
                }
                break;
            case R.id.history_linearlayout_favorite:
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        linearLayoutFavorite.setBackgroundColor(colorAvalonWhiteActionDown);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        linearLayoutList.setBackgroundColor(colorAvalonWhite);
                        linearLayoutFavorite.setBackgroundColor(colorAvalonWhitePressed);
                        break;
                }
                break;
        }
        return false;
    }
}
