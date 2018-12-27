package cazimir.com.bancuribune.presenter.forgot;

import cazimir.com.bancuribune.presenter.auth.IAuthPresenter;
import cazimir.com.bancuribune.view.forgotPassword.OnResendVerificationEmailListener;
import cazimir.com.bancuribune.view.forgotPassword.OnResetPasswordListener;
import cazimir.com.bancuribune.callbacks.forgotPassword.IForgotPasswordActivityView;

public class ForgotPasswordPresenter implements IForgotPasswordPresenter {

    private final IForgotPasswordActivityView mView;
    private final IAuthPresenter mAuthPresenter;

    public ForgotPasswordPresenter(IForgotPasswordActivityView view, IAuthPresenter authPresenter) {
        mView = view;
        mAuthPresenter = authPresenter;
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