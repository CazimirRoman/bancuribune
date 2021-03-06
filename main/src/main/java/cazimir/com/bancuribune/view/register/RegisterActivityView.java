package cazimir.com.bancuribune.view.register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.callbacks.login.OnFormValidatedListener;
import cazimir.com.bancuribune.callbacks.register.IRegisterActivityView;
import cazimir.com.bancuribune.constant.Constants;
import cazimir.com.bancuribune.presenter.auth.AuthPresenter;
import cazimir.com.bancuribune.presenter.register.IRegisterPresenter;
import cazimir.com.bancuribune.presenter.register.RegisterPresenter;
import cazimir.com.bancuribune.utils.UtilHelper;

public class RegisterActivityView extends BaseBackActivity implements IRegisterActivityView {

    private IRegisterPresenter mPresenter;

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etPasswordConfirm)
    EditText etPasswordConfirm;
    @BindView(R.id.btnRegister)
    BootstrapButton btnRegister;
    @BindView(R.id.progress)
    FrameLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new RegisterPresenter(this, new AuthPresenter(this, FirebaseAuth.getInstance()));
        btnRegister.setBootstrapBrand(getAuthenticationBrand());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void setBackArrowColour() {

        final Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_arrow_back, null);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @OnClick(R.id.btnRegister)
    public void register(View view) {

        if (isInternetAvailable()) {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String passwordConfirm = etPasswordConfirm.getText().toString();
            UtilHelper.validateFormData(new OnFormValidatedListener() {
                @Override
                public void onValidateSuccess(String email, String password) {
                    mPresenter.registerUser(email, password);
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
            }, email, password, passwordConfirm);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_view;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.nothing;
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
    public void showToast(String message) {
        buildToast(message).show();
    }

    @Override
    public void redirectToLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("email", etEmail.getText().toString());
                setResult(Activity.RESULT_OK, returnIntent);
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
    public IGeneralView getInstance() {
        return this;
    }
}
