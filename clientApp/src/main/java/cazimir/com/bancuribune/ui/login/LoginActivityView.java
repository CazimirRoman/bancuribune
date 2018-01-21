package cazimir.com.bancuribune.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.presenter.AuthPresenter;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.ui.forgotPassword.ForgotPasswordActivityView;
import cazimir.com.bancuribune.ui.list.MainActivityView;
import cazimir.com.bancuribune.ui.register.RegisterActivityView;
import cazimir.com.bancuribune.utils.MyAlertDialog;
import cazimir.com.bancuribune.utils.Utils;

import static cazimir.com.bancuribune.R.id.login_button_dummy;

public class LoginActivityView extends BaseActivity implements ILoginActivityView {

    private MyAlertDialog mAlertDialog;
    private AuthPresenter mAuthPresenter;
    private CallbackManager mCallbackManager;
    private CommonPresenter mPresenter;

    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.btnForgotPassword)
    TextView btnForgotPassword;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlertDialog = new MyAlertDialog(this);
        mCallbackManager = CallbackManager.Factory.create();
        mAuthPresenter = new AuthPresenter(this);
        mAuthPresenter.checkIfUserLoggedIn();
        mPresenter = new CommonPresenter(this);
        setFacebookClickListener();
    }

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_view;
    }

    @OnClick({R.id.btnLoginWithEmail, R.id.login_button_dummy, R.id.btnRegister, R.id.btnForgotPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLoginWithEmail:
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (isFormDataValid()) {
                    mPresenter.performLogin(email, password);
                    showProgress();
                }
                break;
            case R.id.login_button_dummy:
                if (Utils.isInternetAvailable(this)) {
                    facebookButton.performClick();
                    loginButtonDummy.setText(getString(R.string.loading_data));
                } else {
                    mAlertDialog.show(getString(R.string.no_internet));
                }
                break;
            case R.id.btnRegister:
                startActivity(new Intent(LoginActivityView.this, RegisterActivityView.class));
                break;

            case R.id.btnForgotPassword:
                startActivity(new Intent(LoginActivityView.this, ForgotPasswordActivityView.class));
                break;
        }
    }

    private void setFacebookClickListener() {
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
    public void loginSucces() {
        this.launchMainActivity();
    }

    @Override
    public void loginFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isFormDataValid() {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.email_missing));
            return false;
        } else {
            if (!Utils.isValidEmail(email)) {
                etEmail.setError(getString(R.string.email_invalid));
                return false;
            }
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.password_missing));
            return false;
        } else {
            if (password.length() < 6) {
                etPassword.setError(getString(R.string.minimum_password));
                return false;
            }
        }

        return true;
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
        mAlertDialog.show(message);
    }
}
