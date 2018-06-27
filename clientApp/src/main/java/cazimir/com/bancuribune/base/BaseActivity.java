package cazimir.com.bancuribune.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import butterknife.ButterKnife;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.presenter.common.CommonPresenter;
import cazimir.com.bancuribune.utils.MySweetAlertDialog;
import cazimir.com.bancuribune.utils.UtilHelper;

public abstract class BaseActivity extends AppCompatActivity implements IGeneralView {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private MySweetAlertDialog mAlertDialog;
    private CommonPresenter mPresenter;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mAlertDialog = new MySweetAlertDialog(this);
        mPresenter = new CommonPresenter(this);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(setActionBarTitle()));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected abstract int getLayoutId();

    protected abstract int setActionBarTitle();

    protected MySweetAlertDialog getAlertDialog() {
        return mAlertDialog;
    }

    protected Toast buildToast(String message) {

        Toast mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        View view = mToast.getView();
        view.setBackgroundResource(R.drawable.toast_background);
        TextView text = view.findViewById(android.R.id.message);
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        text.setTextColor(Color.WHITE);
        mToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 10, 230);
        return mToast;
    }

    protected CommonPresenter getPresenter() {
        return mPresenter;
    }

    protected boolean isInternetAvailable() {
        if (!UtilHelper.isInternetAvailable(this)) {
            getAlertDialog().show(getString(R.string.no_internet), SweetAlertDialog.ERROR_TYPE);
            return false;
        }

        return true;
    }

    protected void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED);
    }

    protected FirebaseAnalytics getFirebaseAnalytics() {
        if (mFirebaseAnalytics != null) {
            return mFirebaseAnalytics;
        }

        return FirebaseAnalytics.getInstance(this);
    }

    protected void logEvent(String event, Bundle bundle) {
        if (!isDeviceForTesting(this))
            getFirebaseAnalytics().logEvent(event, bundle);
    }

    private static String getDeviceID(Context c) {
        return Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static boolean isDeviceForTesting(Context c) {
        for (String testingID : Constants.TESTING_DEVICES)
            if (getDeviceID(c).equals(testingID)){
                Log.d(TAG, "isDeviceForTesting: true");
                return true;
            }

        Log.d(TAG, "isDeviceForTesting: false");
        return false;
    }
}
