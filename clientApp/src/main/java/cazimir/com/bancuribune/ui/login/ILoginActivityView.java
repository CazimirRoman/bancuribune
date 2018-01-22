package cazimir.com.bancuribune.ui.login;


import android.app.Activity;

public interface ILoginActivityView {
    void showProgress();
    void hideProgress();
    void showAlertDialog(String message);
    void launchMainActivity();
    void loginSuccess();
    void loginFailed(String message);
    void setEmailError(String error);
    void setPasswordError(String error);
    Activity getContext();
}
