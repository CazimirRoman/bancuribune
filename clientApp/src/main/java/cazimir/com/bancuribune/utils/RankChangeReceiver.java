package cazimir.com.bancuribune.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Broadcast Receiver to handle rank changes (show popup)
 */
public class RankChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TestBroadcast", "RankChangeReceiver called!");
    }
}
