package io.github.devbobos.cameradic.presenter;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.List;

import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.helper.DatabaseHelper;
import io.github.devbobos.cameradic.helper.PreferenceHelper;
import io.github.devbobos.cameradic.model.History;
import io.github.devbobos.cameradic.model.HistoryDao;
import io.github.devbobos.cameradic.view.ItemDetailActivity;

/**
 * Created by devbobos on 2018. 8. 26..
 */
public class ItemDetailPresenter
{
    private ItemDetailActivity activity;
    private History history;
    private boolean isSaved;

    public ItemDetailPresenter(ItemDetailActivity activity)
    {
        this.activity = activity;
    }
    public void setActionBar()
    {
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle(activity.getResources().getString(R.string.detail_title));
        }
        else
        {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getActionBar().setTitle(activity.getResources().getString(R.string.detail_title));
        }
    }
    public void setHistoryOnPresenter(String title, String description, String link, String thumbnail, int languageCode)
    {
        history = null;
        HistoryDao dao = DatabaseHelper.getInstance(activity.getApplicationContext()).getHistoryDao();
        List<History> list = dao.queryBuilder()
                .where(HistoryDao.Properties.Link.eq(link))
                .where(HistoryDao.Properties.Description.eq(description))
                .list();
        if(list.size()>0)
        {
            history = list.get(0);
            isSaved = true;
        }
        else
        {
            Date currentDate = new Date();
            history = new History(currentDate, currentDate, false, title, description, link, thumbnail, languageCode);
            if(PreferenceHelper.getInstance().getAutoRecording(activity.getApplicationContext()) == SystemConstant.AUTO_RECORDING_ENABLED)
            {
                dao.insert(history);
                isSaved = true;
            }
        }
        Logger.d("itemDetail "+history.toString());
    }
    public void saveHistory()
    {
        Date currentDate = new Date();
        HistoryDao dao = DatabaseHelper.getInstance(activity.getApplicationContext()).getHistoryDao();
        dao.insert(history);
        isSaved = true;
        Logger.d("itemDetail "+history.toString());
    }
    public void setTitle(String title, TextView view)
    {
        if(title.length() > 5)
        {
            view.setTextSize(activity.getResources().getDimension(R.dimen.textsize_h4)/activity.getResources().getDisplayMetrics().density);
            view.setText(title);
        }
        else
        {
            view.setTextSize(activity.getResources().getDimension(R.dimen.textsize_h3)/activity.getResources().getDisplayMetrics().density);
            view.setText(title);
        }
    }
    public void setImage(String link, ImageView view)
    {
        if(link!=null && link.length()>0)
        {
            view.setVisibility(View.VISIBLE);
            Glide.with(activity).load(link).into(view);
        }
        else
        {
            view.setVisibility(View.GONE);
        }
    }
    public void setLanguage(int languageCode, TextView view)
    {
        switch (languageCode)
        {
            case SystemConstant.LANGUAGE_TYPE_KOREAN:
                view.setText(SystemConstant.LANGUAGE_STRING_KOREAN);
                break;
            case SystemConstant.LANGUAGE_TYPE_ENGLISH:
                view.setText(SystemConstant.LANGUAGE_STRING_ENGLISH);
                break;
            case SystemConstant.LANGUAGE_TYPE_JAPANESE:
                view.setText(SystemConstant.LANGUAGE_STRING_JAPANESE);
                break;
            case SystemConstant.LANGUAGE_TYPE_CHINESE:
                view.setText(SystemConstant.LANGUAGE_STRING_CHINESE);
                break;
            case SystemConstant.LANGUAGE_TYPE_RUSSIAN:
                view.setText(SystemConstant.LANGUAGE_STRING_RUSSIAN);
                break;
        }
    }
    public void swapFavoriteOnDatabase(AppCompatImageView view)
    {
        HistoryDao dao = DatabaseHelper.getInstance(activity.getApplicationContext()).getHistoryDao();
        if(history.getIsFavorite())
        {
            history.setIsFavorite(false);
            dao.update(history);
            view.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
        }
        else
        {
            history.setIsFavorite(true);
            if(isSaved)
            {
                dao.update(history);
            }
            else
            {
                dao.insert(history);
                isSaved = true;
            }
            view.setImageResource(R.drawable.ic_bookmark_black_24dp);
        }
    }
    public void shareItem(String title, String description, String link)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        StringBuilder builder = new StringBuilder();
        builder.append(title);
        builder.append(", ");
        builder.append(description);
        builder.append("\n");
        builder.append(link);
        intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, "[CAMERADIC]");
        Intent chooser = Intent.createChooser(intent, activity.getResources().getString(R.string.menu_share));
        activity.startActivity(chooser);
    }
    public void showWebPage(String url)
    {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }
    public void deleteHistoryOnDatabase()
    {
        if(isSaved)
        {
            HistoryDao dao = DatabaseHelper.getInstance(activity.getApplicationContext()).getHistoryDao();
            dao.delete(getHistory());
            isSaved = false;
        }
    }

    public History getHistory()
    {
        return history;
    }

    public boolean isSaved()
    {
        return isSaved;
    }
}
