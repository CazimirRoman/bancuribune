package cazimir.com.bancuribune.base;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.appevents.AppEventsLogger;

import cazimir.com.bancuribune.BuildConfig;
import io.fabric.sdk.android.Fabric;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);
        TypefaceProvider.registerDefaultIconSets();
        setUpCrashlytics();
    }

    private void setUpCrashlytics() {
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        Fabric.with(this, crashlyticsKit);
    }
}
