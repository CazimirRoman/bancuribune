package cazimir.com.bancuribune.ui.register;

/**
 * TODO: Add a class header comment!
 */
public interface IRegisterActivityView {
    boolean isFormDataValid();
    void showProgress();
    void hideProgress();
    void showAlertDialog(String message);
    void showToast(String message);
    void redirectToLogin();
}
