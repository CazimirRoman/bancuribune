package cazimir.com.bancuribune.presenter.register;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import cazimir.com.bancuribune.presenter.auth.IAuthPresenter;
import cazimir.com.bancuribune.view.register.OnRegistrationCallback;
import cazimir.com.bancuribune.callbacks.register.IRegisterActivityView;

public class RegisterPresenter implements IRegisterPresenter {

    private IRegisterActivityView mView;
    private IAuthPresenter mAuthPresenter;

    public RegisterPresenter(IRegisterActivityView mView, IAuthPresenter mAuthPresenter) {
        this.mView = mView;
        this.mAuthPresenter = mAuthPresenter;
    }

    @Override
    public void registerUser(String email, String password) {
        mView.showProgress();
        mAuthPresenter.registerUser(new OnRegistrationCallback() {
            @Override
            public void onRegistrationSuccess(String message) {
                mView.showToast(message);
                mView.hideProgress();
                mView.redirectToLogin();
            }

            @Override
            public void onRegistrationFailed(String error) {
                mView.showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
                mView.hideProgress();
            }
        }, email, password);
    }
}