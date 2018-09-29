package io.github.devbobos.cameradic.helper;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.model.DaoMaster;
import io.github.devbobos.cameradic.model.DaoSession;
import io.github.devbobos.cameradic.model.HistoryDao;
import io.github.devbobos.cameradic.model.LicenseDao;

/**
 * Created by devbobos on 2018. 8. 26..
 */
public class DatabaseHelper
{
    private static DatabaseHelper instance = new DatabaseHelper();
    private static DaoMaster.DevOpenHelper helper;
    private static Database database;
    private static DaoSession daoSession;

    private DatabaseHelper() { }

    public static DatabaseHelper getInstance(Context context)
    {
        if(helper == null || database == null || daoSession == null)
        {
            helper = new DaoMaster.DevOpenHelper(context, SystemConstant.VERSION_DATABASE);
            database = helper.getWritableDb();
            daoSession = new DaoMaster(database).newSession();
        }
        return instance;
    }
    public HistoryDao getHistoryDao()
    {
        return daoSession.getHistoryDao();
    }
    public LicenseDao getLicenseDao()
    {
        return daoSession.getLicenseDao();
    }
    public void resetDatabase()
    {
        DaoMaster.dropAllTables(database, true);
        DaoMaster.createAllTables(database, true);
    }
    public void resetHistoryDatabase()
    {
        getHistoryDao().deleteAll();
    }
}
