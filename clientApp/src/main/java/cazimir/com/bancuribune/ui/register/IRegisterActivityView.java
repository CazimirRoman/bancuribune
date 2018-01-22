package cazimir.com.bancuribune.ui.register;

/**
 * TODO: Add a class header comment!
 */
public interface IRegisterActivityView {
    void showProgress();
    void hideProgress();
    void showAlertDialog(String message);
    void showToast(String message);
    void redirectToLogin();
    void setEmailError(String error);
    void setPasswordError(String error);
    void setPasswordConfirmError(String error);
}
