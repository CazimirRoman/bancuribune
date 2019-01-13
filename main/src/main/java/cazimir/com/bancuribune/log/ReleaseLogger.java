package cazimir.com.bancuribune.log;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

/**
 * Release logger that is initialized in the {@link cazimir.com.bancuribune.base.BaseApplication class}
 */
public class ReleaseLogger extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        if (priority == Log.ERROR || priority == Log.WARN){
            Crashlytics.log(priority, tag, message);
        }
    }
}
