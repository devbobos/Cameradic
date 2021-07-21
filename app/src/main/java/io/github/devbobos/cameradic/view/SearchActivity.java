package io.github.devbobos.cameradic.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTouch;
import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.adapter.SearchAdapter;
import io.github.devbobos.cameradic.helper.NetworkChecker;
import io.github.devbobos.cameradic.helper.PreferenceHelper;
import io.github.devbobos.cameradic.model.History;
import io.github.devbobos.cameradic.presenter.SearchPresenter;

/**
 * Created by devbobos on 2018. 9. 1..
 */
public class SearchActivity extends AppCompatActivity
{
     @BindView(R.id.search_linearlayout_container) LinearLayout linearLayoutContainer;
     @BindView(R.id.search_appcompatedittext) AppCompatEditText appCompatEditText;
     @BindView(R.id.search_imageview_search) ImageView imageViewSearch;
     @BindView(R.id.search_progressbar) ProgressBar progressBar;
     @BindView(R.id.search_relativelayout_search) RelativeLayout relativeLayoutSearch;
     @BindView(R.id.search_textview_hint) TextView textViewHint;
     @BindView(R.id.search_linearlayout_information) LinearLayout linearLayoutInformation;
     @BindView(R.id.search_imageview_information) AppCompatImageView imageViewInformation;
     @BindView(R.id.search_textview_information) TextView textViewInformation;
     @BindView(R.id.search_recyclerView) RecyclerView recyclerView;
     @BindView(R.id.search_view_bottomSpace) View viewBottomSpace;
     @BindView(R.id.search_linearlaout_informationmenu) LinearLayout linearLayoutInformationMenu;
     @BindView(R.id.search_imageview_informationRetry) AppCompatImageView appCompatImageViewInformationRetry;
     @BindView(R.id.search_imageview_informationChangeDictionary) AppCompatImageView appCompatImageViewChangeDictionary;
     @BindView(R.id.search_textview_informationRetry) TextView textViewInformationRetry;
     @BindView(R.id.search_textview_informationChangeDictionary) TextView textViewInformationChangeDictionary;
    @BindColor(R.color.colorAvalonWhite) int colorAvalonWhite;
    @BindColor(R.color.colorAvalonWhiteActionDown) int colorAvalonWhiteActionDown;
    @BindColor(R.color.colorAvalonDeepYellow) int colorAvalonDeepYellow;
    @BindString(R.string.search_information) String stringSearchInformation;
    @BindString(R.string.search_information_nothing) String stringSearchInformationNothing;
    @BindString(R.string.search_edittext_hint_empty) String stringSearchHintEmpty;
    @BindString(R.string.common_network_unavailable) String stringNetworkUnavailable;

    private SearchPresenter presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        presenter = new SearchPresenter(this);
        presenter.setActionBar();
        setContentView(R.layout.search_acitivity);
        ButterKnife.bind(this);
        appCompatEditText.requestFocus();
        presenter.setBottomSpace(viewBottomSpace);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_information:
                if(recyclerView.getAdapter()!=null && recyclerView.getAdapter().getItemCount() > 0)
                {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.search_linearlayout_container), stringSearchInformation, Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                else
                {
                    imageViewInformation.setImageResource(R.drawable.ic_sentiment_satisfied_black_24dp);
                    textViewInformation.setText(stringSearchInformation);
                    linearLayoutInformation.setVisibility(View.VISIBLE);
                    linearLayoutInformationMenu.setVisibility(View.GONE);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_information, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @OnClick(R.id.search_relativelayout_search) void onSearchClicked()
    {
        if(NetworkChecker.getInstance().isNetworkOnline(getApplicationContext()))
        {
            if(appCompatEditText.getText().toString().trim().length() > 0)
            {
                imageViewSearch.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                textViewHint.setVisibility(View.GONE);
                linearLayoutInformation.setVisibility(View.GONE);
                presenter.getResultList(appCompatEditText.getText().toString().trim());
                presenter.hideKeyboard(linearLayoutContainer);
            }
            else
            {
                textViewHint.setVisibility(View.VISIBLE);
                textViewHint.setText(stringSearchHintEmpty);
                presenter.hideKeyboard(linearLayoutContainer);
                linearLayoutInformation.setVisibility(View.GONE);
            }
        }
        else
        {
            linearLayoutInformation.setVisibility(View.VISIBLE);
            linearLayoutInformationMenu.setVisibility(View.GONE);
            imageViewInformation.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
            textViewInformation.setText(stringNetworkUnavailable);
            presenter.hideKeyboard(linearLayoutContainer);
        }
    }
    @OnTouch(R.id.search_relativelayout_search) boolean onSearchTouched(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                relativeLayoutSearch.setBackgroundColor(colorAvalonWhiteActionDown);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                relativeLayoutSearch.setBackgroundColor(colorAvalonWhite);
                break;
        }
        return false;
    }
    @OnClick({R.id.search_textview_informationRetry, R.id.search_textview_informationChangeDictionary, R.id.search_imageview_informationRetry, R.id.search_imageview_informationChangeDictionary})
    void onMenuClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.search_textview_informationRetry:
            case R.id.search_imageview_informationRetry:
                onSearchClicked();
                break;
            case R.id.search_textview_informationChangeDictionary:
            case R.id.search_imageview_informationChangeDictionary:
                presenter.showDictionaryTypeSelector(PreferenceHelper.getInstance().getDictionaryType(getApplicationContext()));
                break;
        }
    }
    @OnTouch({R.id.search_textview_informationRetry, R.id.search_textview_informationChangeDictionary, R.id.search_imageview_informationRetry, R.id.search_imageview_informationChangeDictionary})
    boolean onMenuTouched(View view, MotionEvent event)
    {
        switch (view.getId())
        {
            case R.id.search_textview_informationRetry:
            case R.id.search_imageview_informationRetry:
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        textViewInformationRetry.setTextColor(colorAvalonWhiteActionDown);
                        appCompatImageViewInformationRetry.setColorFilter(colorAvalonDeepYellow);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        textViewInformationRetry.setTextColor(colorAvalonWhite);
                        appCompatImageViewInformationRetry.setColorFilter(colorAvalonWhite);
                        break;
                }
                break;
            case R.id.search_textview_informationChangeDictionary:
            case R.id.search_imageview_informationChangeDictionary:
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        textViewInformationChangeDictionary.setTextColor(colorAvalonWhiteActionDown);
                        appCompatImageViewChangeDictionary.setColorFilter(colorAvalonDeepYellow);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        textViewInformationChangeDictionary.setTextColor(colorAvalonWhite);
                        appCompatImageViewChangeDictionary.setColorFilter(colorAvalonWhite);
                        break;
                }
                break;
        }
        return false;
    }
    @OnEditorAction(R.id.search_appcompatedittext) boolean onEditText(TextView view, int actionId, KeyEvent event)
    {
        switch (actionId)
        {
            case EditorInfo.IME_ACTION_SEARCH:
            case EditorInfo.IME_ACTION_DONE:
                onSearchClicked();
                break;
        }
        return false;
    }
    public void setResultView(List<History> list)
    {
        if(list.size()>0)
        {
            imageViewSearch.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(new SearchAdapter(this, list));
        }
        else
        {
            imageViewSearch.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            imageViewInformation.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
            textViewInformation.setText(stringSearchInformationNothing);
            linearLayoutInformation.setVisibility(View.VISIBLE);
            linearLayoutInformationMenu.setVisibility(View.VISIBLE);
        }
    }
}
