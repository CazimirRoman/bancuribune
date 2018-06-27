package cazimir.com.bancuribune.ui.forgotPassword;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.interfaces.ui.forgotPassword.IForgotPasswordActivityView;
import cazimir.com.utils.UtilHelper;

public class ForgotPasswordActivityView extends BaseBackActivity implements IForgotPasswordActivityView {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnForgotPassword)
    TextView btnForgotPassword;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.btnResendVerificationEmail)
    BootstrapButton btnResendVerificationEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                if (isFormDataValid(false)) {
                    if (isInternetAvailable()) {
                        getPresenter().sendResetInstructions(etEmail.getText().toString());
                    } else {
                        getAlertDialog().show(getString(R.string.no_internet), SweetAlertDialog.ERROR_TYPE);
                    }
                }
            }
        });

        btnResendVerificationEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (isFormDataValid(true)) {
                    if (isInternetAvailable()) {
                        getPresenter().resendVerificationEmail(etEmail.getText().toString(), etPassword.getText().toString());
                    } else {
                        getAlertDialog().show(getString(R.string.no_internet), SweetAlertDialog.ERROR_TYPE);
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

    private boolean isFormDataValid(Boolean isPasswordRequired) {

        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.email_missing));
            return false;
        } else {
            if (UtilHelper.isValidEmail(email)) {
                etEmail.setError(getString(R.string.email_invalid));
                return false;
            }
        }
        if (isPasswordRequired) {
            String password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                etPassword.setError(getString(R.string.password_missing));
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