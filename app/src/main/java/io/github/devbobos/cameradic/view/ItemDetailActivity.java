package io.github.devbobos.cameradic.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.helper.NetworkChecker;
import io.github.devbobos.cameradic.helper.TextHelper;
import io.github.devbobos.cameradic.presenter.ItemDetailPresenter;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class ItemDetailActivity extends AppCompatActivity
{
     @BindView(R.id.itemdetail_linearlayout_container) LinearLayout linearLayoutContainer;
     @BindView(R.id.itemdetail_imageview) ImageView imageView;
     @BindView(R.id.itemdetail_textview_title) TextView textViewTitle;
     @BindView(R.id.itemdetail_textview_description) TextView textViewDescription;
     @BindView(R.id.itemdetail_textview_language) TextView textViewLanguage;
     @BindView(R.id.itemdetail_textview_favorite) TextView textViewFavorite;
     @BindView(R.id.itemdetail_imageview_favorite) AppCompatImageView imageViewFavorite;
     @BindView(R.id.itemdetail_textview_link) TextView textViewLink;
     @BindView(R.id.itemdetail_imageview_link) AppCompatImageView imageViewLink;
     @BindView(R.id.itemdetail_textview_date) TextView textViewDate;
     @BindView(R.id.itemdetail_textview_action) TextView textViewAction;
    @BindColor(R.color.colorAvalonWhite) int colorAvalonWhite;
    @BindColor(R.color.colorAvalonWhitePressed) int colorAvalonWhitePressed;
    @BindColor(R.color.colorAvalonWhiteActionDown) int colorAvalonWhiteActionDown;
    @BindColor(R.color.colorAvalonDeepYellow) int colorAvalonDeepYellow;
    @BindColor(R.color.colorAvalonYellow) int colorAvalonYellow;
    @BindString(R.string.common_network_unavailable) String stringNetworkUnavailable;

    private ItemDetailPresenter presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        presenter = new ItemDetailPresenter(this);
        presenter.setActionBar();
        setContentView(R.layout.itemdetail_activity);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        presenter.setHistoryOnPresenter(
                getIntent().getStringExtra(SystemConstant.KEY_TITLE),
                getIntent().getStringExtra(SystemConstant.KEY_DESCRIPTION),
                getIntent().getStringExtra(SystemConstant.KEY_LINK),
                getIntent().getStringExtra(SystemConstant.KEY_THUMBNAIL),
                getIntent().getIntExtra(SystemConstant.KEY_LANGUAGE,
                        SystemConstant.LANGUAGE_TYPE_KOREAN));
        presenter.setTitle(presenter.getHistory().getTitle(), textViewTitle);
        textViewDescription.setText(presenter.getHistory().getDescription());
        presenter.setImage(presenter.getHistory().getThumbnail(), imageView);
        presenter.setLanguage(presenter.getHistory().getLanguageCode(), textViewLanguage);
        textViewDate.setText(TextHelper.getInstance().getHistoryDateString(presenter.getHistory().getUpdatedDate()));
        if(presenter.getHistory().getIsFavorite())
        {
            imageViewFavorite.setImageResource(R.drawable.ic_bookmark_black_24dp);
        }
        else
        {
            imageViewFavorite.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
        }
        if(presenter.isSaved())
        {
            textViewAction.setText(getResources().getString(R.string.itemdetail_delete));
        }
        else
        {
            textViewAction.setText(getResources().getString(R.string.itemdetail_insert));
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
            case R.id.menu_share:
                presenter.shareItem(getIntent().getStringExtra(SystemConstant.KEY_TITLE), getIntent().getStringExtra(SystemConstant.KEY_DESCRIPTION), getIntent().getStringExtra(SystemConstant.KEY_LINK));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @OnClick({R.id.itemdetail_textview_favorite, R.id.itemdetail_textview_link, R.id.itemdetail_imageview_favorite, R.id.itemdetail_imageview_link, R.id.itemdetail_textview_action}) void onTextClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.itemdetail_imageview_favorite:
            case R.id.itemdetail_textview_favorite:
                presenter.swapFavoriteOnDatabase(imageViewFavorite);
                break;
            case R.id.itemdetail_imageview_link:
            case R.id.itemdetail_textview_link:
                if(NetworkChecker.getInstance().isNetworkOnline(getApplicationContext()))
                {
                    presenter.showWebPage(presenter.getHistory().getLink());
                }
                else
                {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.itemdetail_linearlayout_container), stringNetworkUnavailable, Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                break;
            case R.id.itemdetail_textview_action:
                if(presenter.isSaved())
                {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.itemdetail_linearlayout_container), R.string.itemdetail_popup_delete_description, Snackbar.LENGTH_LONG);
                    mySnackbar.setAction(R.string.itemdetail_popup_delete, new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            presenter.deleteHistoryOnDatabase();
                            textViewAction.setText(getResources().getString(R.string.itemdetail_insert));
                        }
                    });
                    mySnackbar.show();
                }
                else
                {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.itemdetail_linearlayout_container), R.string.itemdetail_popup_insert, Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                    presenter.saveHistory();
                    textViewAction.setText(getResources().getString(R.string.itemdetail_delete));
                }
                break;
        }
    }
    @OnTouch({R.id.itemdetail_textview_favorite, R.id.itemdetail_textview_link, R.id.itemdetail_textview_action}) boolean onTextTouched(View view, MotionEvent event)
    {
        switch (view.getId())
        {
            case R.id.itemdetail_textview_favorite:
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        imageViewFavorite.setColorFilter(colorAvalonDeepYellow);
                        textViewFavorite.setTextColor(colorAvalonWhitePressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        imageViewFavorite.setColorFilter(colorAvalonWhite);
                        textViewFavorite.setTextColor(colorAvalonWhite);
                        break;
                }
                break;
            case R.id.itemdetail_textview_link:
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        imageViewLink.setColorFilter(colorAvalonDeepYellow);
                        textViewLink.setTextColor(colorAvalonWhitePressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        imageViewLink.setColorFilter(colorAvalonWhite);
                        textViewLink.setTextColor(colorAvalonWhite);
                        break;
                }
                break;
            case R.id.itemdetail_textview_action:
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        textViewAction.setTextColor(colorAvalonDeepYellow);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        textViewAction.setTextColor(colorAvalonYellow);
                        break;
                }
                break;
        }
        return false;
    }
}
