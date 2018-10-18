package cazimir.com.bancuribune.ui.forgotPassword;

import cazimir.com.interfaces.authentication.IAuthPresenter;
import cazimir.com.interfaces.authentication.OnResendVerificationEmailListener;
import cazimir.com.interfaces.authentication.OnResetPasswordListener;
import cazimir.com.interfaces.ui.forgotPassword.IForgotPasswordActivityView;

public class ForgotPasswordPresenter implements IForgotPasswordPresenter {

    private final IForgotPasswordActivityView mView;
    private final IAuthPresenter mAuthPresenter;

    ForgotPasswordPresenter(IForgotPasswordActivityView view, IAuthPresenter authPresenter) {
        this.mView = view;
        this.mAuthPresenter = authPresenter;
    }

    @Override
    public void resendVerificationEmail(String email, String password) {
        mView.showProgress();
        mAuthPresenter.performResendVerificationEmail(new OnResendVerificationEmailListener() {

            @Override
            public void onResendEmailSuccess(String message) {
                mView.showToast(message);
                mView.redirectToLogin();
                mView.hideProgress();
            }

            @Override
            public void onResendEmailFailed(String error) {
                mView.hideProgress();
                mView.showToast(error);
            }
        }, email, password);
    }

    @Override
    public void sendResetInstructions(String email) {
        mView.showProgress();
        mAuthPresenter.performPasswordReset(new OnResetPasswordListener() {
            @Override
            public void onResetPasswordSuccess(String message) {
                mView.showToast(message);
                mView.redirectToLogin();
                mView.hideProgress();
            }

            @Override
            public void onResetPasswordFailed(String error) {
                mView.showToast(error);
                mView.hideProgress();
            }
        }, email);
    }
}
