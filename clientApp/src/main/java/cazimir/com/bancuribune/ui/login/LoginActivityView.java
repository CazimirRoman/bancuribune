package cazimir.com.bancuribune.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.presenter.AuthPresenter;
import cazimir.com.bancuribune.ui.list.MainActivityView;
import cazimir.com.bancuribune.utils.MyAlertDialog;

import static cazimir.com.bancuribune.R.id.login_button_dummy;

public class LoginActivityView extends BaseActivity implements ILoginActivityView, OnClickListener {

    private MyAlertDialog alertDialog;
    private AuthPresenter presenter;
    private CallbackManager callbackManager;

    @BindView(R.id.login_button)
    LoginButton facebookButton;

    @BindView(login_button_dummy)
    TextView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alertDialog = new MyAlertDialog(this);
        callbackManager = CallbackManager.Factory.create();
        presenter = new AuthPresenter(this);
        presenter.checkIfUserLoggedIn();
        loginButton.setOnClickListener(this);
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
        facebookButton.registerCallback(callbackManager, presenter.loginWithFacebook());
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
            facebookButton.performClick();
            loginButton.setText(getString(R.string.loading_data));
        }
    }
}
