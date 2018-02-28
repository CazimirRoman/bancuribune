package cazimir.com.bancuribune.ui.register;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.ui.login.OnFormValidatedListener;
import cazimir.com.bancuribune.utils.UtilHelperClass;

public class RegisterActivityView extends BaseBackActivity implements IRegisterActivityView, OnFormValidatedListener {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etPasswordConfirm)
    EditText etPasswordConfirm;
    @BindView(R.id.btnRegister)
    TextView btnRegister;
    @BindView(R.id.progress)
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.btnRegister)
    public void register(View view) {
        if(isInternetAvailable()){
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String passwordConfirm = etPasswordConfirm.getText().toString();
            UtilHelperClass.validateFormData(this, email, password, passwordConfirm);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_view;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.register;
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
    public void showAlertDialog(String message) {
        getAlertDialog().show(message);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void redirectToLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

    @Override
    public void setEmailError(String error) {
        etEmail.setError(error);
    }

    @Override
    public void setPasswordError(String error) {
        etPassword.setError(error);
    }

    @Override
    public void setPasswordConfirmError(String error) {
        etPasswordConfirm.setError(error);
    }

    @Override
    public void onValidateSuccess(String email, String password) {
        getPresenter().registerUser(email, password);
    }

    @Override
    public void onValidateFail(String what) {

        switch (what) {
            case Constants.EMAIL_EMPTY:
                setEmailError(getString(R.string.email_missing));
                break;

            case Constants.EMAIL_INVALID:
                setEmailError(getString(R.string.email_invalid));
                break;

            case Constants.PASSWORD_EMPTY:
                setPasswordError(getString(R.string.password_missing));
                break;

            case Constants.PASSWORD_INVALID:
                setPasswordError(getString(R.string.password_minimum));
                break;

            case Constants.PASSWORD_MATCH_ERROR:
                setPasswordConfirmError(getString(R.string.password_not_matching));
                break;
        }
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }
}
