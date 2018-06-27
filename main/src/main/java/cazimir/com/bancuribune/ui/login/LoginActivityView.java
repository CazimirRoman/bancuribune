package cazimir.com.bancuribune.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.presenter.authentication.AuthPresenter;
import cazimir.com.bancuribune.ui.forgotPassword.ForgotPasswordActivityView;
import cazimir.com.bancuribune.ui.list.MainActivityView;
import cazimir.com.bancuribune.ui.register.RegisterActivityView;
import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.interfaces.ui.login.ILoginActivityView;
import cazimir.com.interfaces.ui.login.OnFormValidatedListener;
import cazimir.com.utils.UtilHelper;

import static cazimir.com.bancuribune.R.id.login_button_dummy;
import static cazimir.com.constants.Constants.EMAIL_EMPTY;
import static cazimir.com.constants.Constants.EMAIL_INVALID;
import static cazimir.com.constants.Constants.PASSWORD_EMPTY;
import static cazimir.com.constants.Constants.PASSWORD_INVALID;
import static cazimir.com.constants.Constants.PASSWORD_MATCH_NA;
import static cazimir.com.constants.Constants.REGISTER_ACTIVITY_REQ_CODE;

public class LoginActivityView extends BaseActivity implements ILoginActivityView {

    private AuthPresenter mAuthPresenter;
    private CallbackManager mCallbackManager;

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnLoginWithEmail)
    TextView btnLoginWithEmail;
    @BindView(R.id.login_button)
    LoginButton facebookButton;
    @BindView(login_button_dummy)
    TextView loginButtonDummy;
    @BindView(R.id.btnRegister)
    TextView btnRegister;
    @BindView(R.id.btnForgotPassword)
    TextView btnForgotPassword;
    @BindView(R.id.progress)
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        mAuthPresenter = new AuthPresenter(this);
        mAuthPresenter.checkIfUserLoggedIn();
        setFacebookButtonClickListener();
    }

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_view;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.login;
    }

    @OnClick({R.id.btnLoginWithEmail, R.id.login_button_dummy, R.id.btnRegister, R.id.btnForgotPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLoginWithEmail:

                hideKeyboard();

                if (isInternetAvailable()) {
                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();
                    UtilHelper.validateFormData(new OnFormValidatedListener() {
                        @Override
                        public void onValidateSuccess(String email, String password) {
                            getPresenter().performLogin(email, password);
                            showProgress();
                        }

                        @Override
                        public void onValidateFail(String what) {
                            switch (what) {
                                case EMAIL_EMPTY:
                                    setEmailError(getString(R.string.email_missing));
                                    break;

                                case EMAIL_INVALID:
                                    setEmailError(getString(R.string.email_invalid));
                                    break;

                                case PASSWORD_EMPTY:
                                    setPasswordError(getString(R.string.password_missing));
                                    break;

                                case PASSWORD_INVALID:
                                    setPasswordError(getString(R.string.password_minimum));
                                    break;
                            }
                        }
                    }, email, password, PASSWORD_MATCH_NA);
                }

                break;
            case R.id.login_button_dummy:
                if (isInternetAvailable()) {
                    facebookButton.performClick();
                    loginButtonDummy.setText(getString(R.string.loading_data));
                }
                break;
            case R.id.btnRegister:
                startActivityForResult(new Intent(LoginActivityView.this, RegisterActivityView.class), REGISTER_ACTIVITY_REQ_CODE);
                break;

            case R.id.btnForgotPassword:
                startActivity(new Intent(LoginActivityView.this, ForgotPasswordActivityView.class));
                break;
        }
    }

    private void setFacebookButtonClickListener() {
        facebookButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookButton.setReadPermissions("email", "public_profile");
                facebookButton.registerCallback(mCallbackManager, mAuthPresenter.loginWithFacebook());
                facebookButton.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void launchMainActivity() {
        startActivity(new Intent(this, MainActivityView.class));
        this.finish();
    }

    @Override
    public void loginSuccess() {
        this.launchMainActivity();
    }

    @Override
    public void loginFailed(String message) {
        buildToast(message).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_ACTIVITY_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                String email = data.getStringExtra("email");
                if(email != null){
                    etEmail.setText(email);
                }
            }
        }
    }

    public void setEmailError(String error) {
        etEmail.setError(error);
    }

    @Override
    public void setPasswordError(String error) {
        etPassword.setError(error);
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
    public void showAlertDialog(String message, int type) {
        getAlertDialog().show(message, type);
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }
}