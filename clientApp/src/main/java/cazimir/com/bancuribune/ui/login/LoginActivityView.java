package cazimir.com.bancuribune.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.presenter.AuthPresenter;
import cazimir.com.bancuribune.ui.list.JokesActivityView;

public class LoginActivityView extends BaseActivity implements ILoginActivityView {

    private AuthPresenter presenter;
    private CallbackManager callbackManager;

    @BindView(R.id.login_button)
    LoginButton facebookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();
        presenter = new AuthPresenter(this);
        presenter.checkIfUserLoggedIn();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_view;
    }

    @Override
    public void launchMainActivity() {
        startActivity(new Intent(this, JokesActivityView.class));
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
        facebookButton.registerCallback(callbackManager, presenter.loginWithFacebook());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
