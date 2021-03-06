package cazimir.com.bancuribune.callbacks.login;


import android.app.Activity;

import cazimir.com.bancuribune.base.IGeneralView;

public interface ILoginActivityView extends IGeneralView {
    void showProgress();
    void hideProgress();
    void showAlertDialog(String message, int type);
    void launchMainActivity();
    void loginSuccess();
    void loginFailed(String message);
    void setEmailError(String error);
    void setPasswordError(String error);
    Activity getContext();
    void showToast(String message);
    void hideViewsAndButtons();
    void showViewsAndButtons();
    void setAnonymousToTrue();
}
