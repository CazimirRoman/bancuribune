package cazimir.com.bancuribune.view.forgotPassword;

import cazimir.com.bancuribune.base.IGeneralView;

public interface IForgotPasswordActivityView extends IGeneralView {
    void showToast(String message);
    void showProgress();
    void hideProgress();
    void redirectToLogin();
}
