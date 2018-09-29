package io.github.devbobos.cameradic.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by devbobos on 2018. 9. 22..
 */
public class NetworkChecker
{
    private static NetworkChecker instance = new NetworkChecker();

    private NetworkChecker() { }

    public static NetworkChecker getInstance()
    {
        return instance;
    }
    public boolean isNetworkOnline(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(mobile.isConnected() || wifi.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
