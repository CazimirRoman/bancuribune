package cazimir.com.bancuribune.view.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.BuildConfig;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.callbacks.login.ILoginActivityView;
import cazimir.com.bancuribune.callbacks.login.OnFormValidatedListener;
import cazimir.com.bancuribune.constant.Constants;
import cazimir.com.bancuribune.presenter.auth.AuthPresenter;
import cazimir.com.bancuribune.presenter.login.ILoginPresenter;
import cazimir.com.bancuribune.presenter.login.LoginPresenter;
import cazimir.com.bancuribune.repository.DatabaseTypeSingleton;
import cazimir.com.bancuribune.utils.UtilHelper;
import cazimir.com.bancuribune.view.forgotPassword.ForgotPasswordActivityView;
import cazimir.com.bancuribune.view.list.MainActivityView;
import cazimir.com.bancuribune.view.register.RegisterActivityView;
import timber.log.Timber;

import static cazimir.com.bancuribune.constant.Constants.EMAIL_EMPTY;
import static cazimir.com.bancuribune.constant.Constants.EMAIL_INVALID;
import static cazimir.com.bancuribune.constant.Constants.PASSWORD_EMPTY;
import static cazimir.com.bancuribune.constant.Constants.PASSWORD_INVALID;
import static cazimir.com.bancuribune.constant.Constants.PASSWORD_MATCH_NA;
import static cazimir.com.bancuribune.constant.Constants.REGISTER_ACTIVITY_REQ_CODE;

public class LoginActivityView extends BaseActivity implements ILoginActivityView {

    private ILoginPresenter mPresenter;
    private AuthPresenter mAuthPresenter;
    private CallbackManager mCallbackManager;
    private String mJokeIdExtra;
    private Boolean mIsNewRank = false;
    private Boolean mIsAnonymousLogin = false;

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnLoginWithEmail)
    BootstrapButton btnLoginWithEmail;
    @BindView(R.id.btnLoginWithFacebook)
    LoginButton facebookButton;
    @BindView(R.id.login_button_dummy)
    TextView loginButtonDummy;
    @BindView(R.id.btnGoToRegister)
    BootstrapButton btnGoToRegister;
    @BindView(R.id.btnForgotPassword)
    BootstrapButton btnForgotPassword;
    @BindView(R.id.progress)
    FrameLayout progress;
    @BindView(R.id.expandableLayout)
    ExpandableRelativeLayout expandableLayout;
    @BindView(R.id.btnSkipRegistration)
    BootstrapButton btnSkipRegistration;
    @BindView(R.id.scrollView)
    ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new LoginPresenter(this, new AuthPresenter(this));
        mAuthPresenter = new AuthPresenter(this);
        mCallbackManager = CallbackManager.Factory.create();
        setFacebookButtonClickListener();
        initUI();
        if (BuildConfig.DEBUG) {
            //startWithDebugDB();
        }
        mAuthPresenter.checkIfUserLoggedIn();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("loginRequired")) {
                showToast("Loghează-te pentru a putea benficia de toate funcțiile aplicației");
            }
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_view;
    }

    private void startWithDebugDB() {
        DatabaseTypeSingleton type = DatabaseTypeSingleton.getInstance();
        type.setType(true);
    }

    private void initUI() {
        btnLoginWithEmail.setBootstrapBrand(getAuthenticationBrand());
        btnGoToRegister.setBootstrapBrand(getAuthenticationBrand());
        btnLoginWithEmail.setBootstrapBrand(getAuthenticationBrand());
        btnForgotPassword.setBootstrapBrand(getAuthenticationBrand());
        btnSkipRegistration.setBootstrapBrand(getAuthenticationBrand());
        expandableLayout.collapse();
    }

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    public void showToast(String message) {
        buildToast(message).show();
    }

    @Override
    public void hideViewsAndButtons() {
        scrollView.setVisibility(View.GONE);
    }

    @Override
    public void showViewsAndButtons() {
        scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setAnonymousToTrue() {
        mIsAnonymousLogin = true;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.nothing;
    }

    @OnClick({R.id.btnLoginWithEmail, R.id.login_button_dummy, R.id.btnGoToRegister, R.id.btnForgotPassword, R.id.btnSkipRegistration})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLoginWithEmail:
                if (expandableLayout.isExpanded()) {
                    if (isInternetAvailable()) {
                        String email = etEmail.getText().toString();
                        String password = etPassword.getText().toString();
                        UtilHelper.validateFormData(new OnFormValidatedListener() {
                            @Override
                            public void onValidateSuccess(String email, String password) {
                                mPresenter.performLogin(email, password);
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

                } else {
                    expandableLayout.expand();
                }

                break;
            case R.id.login_button_dummy:
                if (isInternetAvailable()) {
                    facebookButton.performClick();
                    loginButtonDummy.setText(getString(R.string.loading_data));
                    showProgress();
                }
                break;
            case R.id.btnGoToRegister:
                startActivityForResult(new Intent(LoginActivityView.this, RegisterActivityView.class), REGISTER_ACTIVITY_REQ_CODE);
                break;
            case R.id.btnForgotPassword:
                startActivity(new Intent(LoginActivityView.this, ForgotPasswordActivityView.class));
                break;
            case R.id.btnSkipRegistration:
                showProgress();
                mPresenter.performAnonymousLogin();
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
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.getString("jokeId") != null) {
                //extra coming from push notification after joke approved
                mJokeIdExtra = getIntent().getStringExtra("jokeId");
                Timber.d("User clicked on push notification for joke approved with id: %d", mJokeIdExtra);
            }

            //it is a rank update. for rank updates jokeId is not present
            if(extras.getString("regards") != null && extras.getString("regards").equals("rank_updated")) {
                Timber.i("User clicked on push notification for rank update");
                mIsNewRank = true;
            }
        }
    }

    @Override
    public void launchMainActivity() {
        Timber.i("Launching main activity...");
        onNewIntent(getIntent());
        Intent i = new Intent(this, MainActivityView.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("jokeId", mJokeIdExtra);
        i.putExtra("regards", mIsNewRank);
        i.putExtra(Constants.ANONYMOUS_LOGIN, mIsAnonymousLogin);
        startActivity(i);
        this.finish();
    }

    @Override
    public void loginSuccess() {
        launchMainActivity();
    }

    @Override
    public void loginFailed(String message) {
        showToast(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_ACTIVITY_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                String email = data.getStringExtra("email");
                if (email != null) {
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
        Timber.i("Hiding progress bar");
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