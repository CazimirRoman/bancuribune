package cazimir.com.bancuribune.base;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import butterknife.ButterKnife;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.presenter.common.CommonPresenter;
import cazimir.com.bancuribune.utils.MySweetAlertDialog;
import cazimir.com.bancuribune.utils.UtilHelper;

public abstract class BaseActivity extends AppCompatActivity implements IGeneralView {

    private MySweetAlertDialog mAlertDialog;
    private CommonPresenter mPresenter;

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
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected abstract int getLayoutId();
    protected abstract int setActionBarTitle();

    protected MySweetAlertDialog getAlertDialog(){
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

    protected boolean isInternetAvailable(){
        if (!UtilHelper.isInternetAvailable(this)) {
            getAlertDialog().show(getString(R.string.no_internet), SweetAlertDialog.ERROR_TYPE);
            return false;
        }

        return true;
    }


}
