package cazimir.com.bancuribune.base;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);
        TypefaceProvider.registerDefaultIconSets();
    }
}
