package cazimir.com.interfaces.ui.forgotPassword;

import cazimir.com.interfaces.base.IGeneralView;

public interface IForgotPasswordActivityView extends IGeneralView {
    void showToast(String message);
    void showProgress();
    void hideProgress();
    void redirectToLogin();
}
