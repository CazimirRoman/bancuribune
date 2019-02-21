package cazimir.com.bancuribune.presenter.login;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import cazimir.com.bancuribune.callbacks.login.ILoginActivityView;
import cazimir.com.bancuribune.presenter.auth.IAuthPresenter;
import cazimir.com.bancuribune.view.list.OnSaveInstanceIdToUserObjectCallback;
import cazimir.com.bancuribune.view.login.OnLoginWithEmailCallback;

/**
 * This class handles login logic
 */
public class LoginPresenter implements ILoginPresenter {

    private ILoginActivityView mView;
    private IAuthPresenter mAuthPresenter;

    public LoginPresenter(ILoginActivityView view, IAuthPresenter authPresenter) {
        mView = view;
        mAuthPresenter = authPresenter;
    }

    @Override
    public void performLogin(String email, String password) {
        mAuthPresenter.login(new OnLoginWithEmailCallback() {
            @Override
            public void onSuccess() {
                mView.launchMainActivity();
                mView.hideProgress();
            }

            @Override
            public void onFailed(String error) {
                mView.showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
                mView.hideProgress();
            }
        }, email, password);
    }

    @Override
    public void performAnonymousLogin() {
        mAuthPresenter.loginAnonymously(new OnAnonymousLoginCallback() {
            @Override
            public void onSuccess() {
                mView.hideProgress();
                mView.setAnonymousToTrue();
                mView.launchMainActivity();
            }

            @Override
            public void onFailed(String message) {
                mView.hideProgress();
                mView.showToast(message);
            }
        });
    }

    @Override
    public void checkIfUserLoggedIn() {
        mAuthPresenter.checkIfUserLoggedIn(new OnCheckIfLoggedInCallback() {
            @Override
            public void isLoggedIn() {
                mAuthPresenter.saveInstanceIdToUserObject(new OnSaveInstanceIdToUserObjectCallback() {
                    @Override
                    public void onSuccess() {
                        mView.launchMainActivity();
                        mView.hideProgress();
                    }

                    @Override
                    public void onFailed(String error) {
                        mView.showToast(error);
                        mView.hideProgress();
                    }
                });
            }

            @Override
            public void isNotLoggedIn() {
                mView.showViewsAndButtons();
                mView.hideProgress();
            }
        });
    }

    @Override
    public FacebookCallback<LoginResult> loginWithFacebook() {
        return mAuthPresenter.loginWithFacebook(new OnLoginWithFacebookCallback() {
            @Override
            public void onLoginWithFacebookSuccess() {
                mAuthPresenter.saveInstanceIdToUserObject(new OnSaveInstanceIdToUserObjectCallback() {
                    @Override
                    public void onSuccess() {
                        mView.loginSuccess();
                        mView.hideProgress();
                    }

                    @Override
                    public void onFailed(String error) {
                        mView.loginFailed(error);
                        mView.hideProgress();
                    }
                });

            }

            @Override
            public void onLoginWithFacebookFailed(String error) {
                mView.loginFailed(error);
                mView.hideProgress();
            }
        });
    }
}