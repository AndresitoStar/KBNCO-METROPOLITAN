package cu.tko.kbnco_metro.logica;

import android.app.Application;
import android.content.Context;

/**
 * Created by a.guerra on 3/22/2018.
 */

public class BanmetApp extends Application{
    private static Context context;

    public void onCreate() {
        super.onCreate();
        BanmetApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return BanmetApp.context;
    }
}
