package cazimir.com.bancuribune.presenter.login;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import cazimir.com.bancuribune.presenter.auth.IAuthPresenter;
import cazimir.com.bancuribune.view.list.OnSaveInstanceIdToUserObjectCallback;
import cazimir.com.bancuribune.view.login.OnLoginWithEmailCallback;
import cazimir.com.bancuribune.callbacks.login.ILoginActivityView;
import timber.log.Timber;

/**
 * This class handles login logic
 */
public class LoginPresenter implements ILoginPresenter {

    private ILoginActivityView mView;
    private IAuthPresenter mAuthPresenter;

    public LoginPresenter(ILoginActivityView mView, IAuthPresenter mAuthPresenter) {
        this.mView = mView;
        this.mAuthPresenter = mAuthPresenter;
    }

    @Override
    public void performLogin(String email, String password) {
        Timber.i("Trying to log in with email: %s", email);
        mAuthPresenter.login(new OnLoginWithEmailCallback() {
            @Override
            public void onSuccess() {
                Timber.i("Login success!");
                mAuthPresenter.saveInstanceIdToUserObject(new OnSaveInstanceIdToUserObjectCallback() {
                    @Override
                    public void onSuccess() {
                        Timber.i("Saved instance id to user object");
                        mView.launchMainActivity();
                        mView.hideProgress();
                    }

                    @Override
                    public void onFailed(String error) {
                        Timber.e("Could not save instance id to user object. Reason: " + error);
                        mView.showToast(error);
                        mView.hideProgress();
                    }
                });

            }

            @Override
            public void onFailed(String error) {
                Timber.e("Login failed! Reason: %s", error);
                mView.showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
                mView.hideProgress();
            }
        }, email, password);
    }

    @Override
    public void performAnonymousLogin() {
        Timber.i("Trying to log in anonymously...");
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
}
