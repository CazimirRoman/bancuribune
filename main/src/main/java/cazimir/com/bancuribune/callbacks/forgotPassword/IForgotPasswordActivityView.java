package cazimir.com.bancuribune.callbacks.forgotPassword;

import cazimir.com.bancuribune.base.IGeneralView;

public interface IForgotPasswordActivityView extends IGeneralView {
    void showToast(String message);
    void showProgress();
    void hideProgress();
    void redirectToLogin();
}
