package cazimir.com.bancuribune.base;

import android.support.multidex.MultiDexApplication;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.appevents.AppEventsLogger;

import cazimir.com.bancuribune.BuildConfig;
import cazimir.com.bancuribune.log.ReleaseLogger;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class BaseApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);
        TypefaceProvider.registerDefaultIconSets();
        setUpCrashlytics();

        //initialize Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseLogger());
        }
    }

    private void setUpCrashlytics() {
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        Fabric.with(this, crashlyticsKit);
    }
}
