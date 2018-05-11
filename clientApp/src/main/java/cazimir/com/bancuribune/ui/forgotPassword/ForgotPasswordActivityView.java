package cazimir.com.bancuribune.ui.forgotPassword;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.utils.UtilHelper;

public class ForgotPasswordActivityView extends BaseBackActivity implements IForgotPasswordActivityView {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.btnForgotPassword)
    TextView btnForgotPassword;
    @BindView(R.id.progress)
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isFormDataValid()){
                    String email = etEmail.getText().toString().trim();

                    if(isFormDataValid()){
                        if(isInternetAvailable()){
                            getPresenter().sendResetInstructions(email);
                        }else{
                            getAlertDialog().show(getString(R.string.no_internet), SweetAlertDialog.ERROR_TYPE);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forgot_password_view;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.forgot_password;
    }

    @Override
    public void showToast(String message) {
        buildToast(message).show();
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void redirectToLogin() {
        hideProgress();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

    private boolean isFormDataValid() {

        String email = etEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.email_missing));
            return false;
        } else {
            if (UtilHelper.isValidEmail(email)) {
                etEmail.setError(getString(R.string.email_invalid));
                return false;
            }
        }

        return true;
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }
}
