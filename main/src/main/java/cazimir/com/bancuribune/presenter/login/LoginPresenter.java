package cazimir.com.bancuribune.presenter.login;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import cazimir.com.bancuribune.presenter.auth.IAuthPresenter;
import cazimir.com.bancuribune.view.login.OnLoginWithEmailFinishedListener;
import cazimir.com.bancuribune.callbacks.login.ILoginActivityView;

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
        mAuthPresenter.login(new OnLoginWithEmailFinishedListener() {
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
        mAuthPresenter.signInAnonymously(new OnAnonymousLoginCallback() {
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
