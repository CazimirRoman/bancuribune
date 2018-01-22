package cazimir.com.bancuribune.ui.forgotPassword;

import cazimir.com.bancuribune.base.IGeneralView;

/**
 * TODO: Add a class header comment!
 */
public interface IForgotPasswordActivityView extends IGeneralView {
    void showToast(String message);
    void showProgress();
    void hideProgress();
    void redirectToLogin();
}
