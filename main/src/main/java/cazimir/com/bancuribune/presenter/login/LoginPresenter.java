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
            public void onLoginWithEmailSuccess() {
                mView.launchMainActivity();
                mView.hideProgress();
            }

            @Override
            public void onLoginWithEmailFailed(String error) {
                mView.showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
                mView.hideProgress();
            }
        }, email, password);
    }

    @Override
    public void checkIfUserLoggedIn() {
        mAuthPresenter.checkIfUserLoggedIn(new OnCheckIfLoggedInCallback() {
            @Override
            public void isLoggedIn() {
                mView.launchMainActivity();
            }
        });
    }
}
