package cazimir.com.bancuribune.view.register;

import cazimir.com.bancuribune.base.IGeneralView;

public interface IRegisterActivityView extends IGeneralView {
    void showProgress();
    void hideProgress();
    void showAlertDialog(String message, int type);
    void showToast(String message);
    void redirectToLogin();
    void setEmailError(String error);
    void setPasswordError(String error);
    void setPasswordConfirmError(String error);
}
