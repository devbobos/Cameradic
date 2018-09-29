package io.github.devbobos.cameradic.presenter;

import java.util.List;

import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.helper.DatabaseHelper;
import io.github.devbobos.cameradic.model.History;
import io.github.devbobos.cameradic.model.HistoryDao;
import io.github.devbobos.cameradic.view.HistoryActivity;

/**
 * Created by devbobos on 2018. 8. 26..
 */
public class HistoryPresenter
{
    private HistoryActivity activity;

    public HistoryPresenter(HistoryActivity activity)
    {
        this.activity = activity;
    }
    public void setActionBar()
    {
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle(activity.getResources().getString(R.string.history_title));
        }
        else
        {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getActionBar().setTitle(activity.getResources().getString(R.string.history_title));
        }
    }
    public List<History> getHistoryList()
    {
        HistoryDao dao = DatabaseHelper.getInstance(activity.getApplicationContext()).getHistoryDao();
        List<History> list = dao.queryBuilder()
                .orderAsc(HistoryDao.Properties.UpdatedDate)
                .list();
        return list;
    }
    public List<History> getHistoryFavoriteList()
    {
        HistoryDao dao = DatabaseHelper.getInstance(activity.getApplicationContext()).getHistoryDao();
        List<History> list = dao.queryBuilder()
                .where(HistoryDao.Properties.IsFavorite.eq(true))
                .orderAsc(HistoryDao.Properties.UpdatedDate)
                .list();
        return list;
    }
}
