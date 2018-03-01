package cazimir.com.bancuribune.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import butterknife.ButterKnife;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.utils.MySweetAlertDialog;
import cazimir.com.bancuribune.utils.UtilHelperClass;

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
        return this.mAlertDialog;
    }

    protected CommonPresenter getPresenter() {
        return mPresenter;
    }

    protected boolean isInternetAvailable(){
        if (!UtilHelperClass.isInternetAvailable(this)) {
            getAlertDialog().show(getString(R.string.no_internet), SweetAlertDialog.ERROR_TYPE);
            return false;
        }

        return true;
    }
}
