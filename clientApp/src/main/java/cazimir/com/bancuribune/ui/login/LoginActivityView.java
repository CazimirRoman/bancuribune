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
import static cazimir.com.bancuribune.R.id.register_button;

public class LoginActivityView extends BaseActivity implements ILoginActivityView, OnClickListener {

    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.btnForgotPassword)
    Button btnForgotPassword;
    private MyAlertDialog alertDialog;
    private AuthPresenter authPresenter;
    private CallbackManager callbackManager;
    private CommonPresenter presenter;

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnLoginWithEmail)
    Button btnLoginWithEmail;

    @BindView(R.id.login_button)
    LoginButton facebookButton;

    @BindView(login_button_dummy)
    TextView loginButton;

    @BindView(register_button)
    TextView registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alertDialog = new MyAlertDialog(this);
        callbackManager = CallbackManager.Factory.create();
        authPresenter = new AuthPresenter(this);
        authPresenter.checkIfUserLoggedIn();
        presenter = new CommonPresenter(this);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        btnLoginWithEmail.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_view;
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
    public Activity getContext() {
        return this;
    }

    @OnClick(R.id.login_button)
    void performFacebookLogin() {

        facebookButton.setReadPermissions("email", "public_profile");
        facebookButton.registerCallback(callbackManager, authPresenter.loginWithFacebook());
        facebookButton.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        if (view == loginButton) {
            if (Utils.isInternetAvailable(this)) {
                facebookButton.performClick();
                loginButton.setText(getString(R.string.loading_data));
            } else {
                alertDialog.show(getString(R.string.no_internet));
            }
        } else if (view == registerButton) {
            startActivity(new Intent(LoginActivityView.this, RegisterActivityView.class));
        } else if (view == btnLoginWithEmail) {

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (isFormDataValid()) {
                presenter.performLogin(email, password);
                showProgress();
            }
        }
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
        alertDialog.show(message);
    }

    @OnClick(R.id.btnForgotPassword)
    public void onViewClicked() {
        startActivity(new Intent(LoginActivityView.this, ForgotPasswordActivityView.class));
    }
}
