package cazimir.com.bancuribune.presenter;


import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import cazimir.com.bancuribune.ui.list.IMainActivityView;
import cazimir.com.bancuribune.ui.myjokes.IMyJokesActivityView;
import cazimir.com.bancuribune.ui.register.OnRegisterListener;

public interface IAuthPresenter {
    void login (OnLoginWithEmailFinishedListener listener, String email, String password);
    void registerUser(OnRegistrationFinishedListener listener, String email, String password);
    FacebookCallback<LoginResult> loginWithFacebook();
    void checkIfUserLoggedIn();
    String getCurrentUserID();
    String getCurrentUserName();
    String getCurrrentUserEmail();
    void logUserOut(IMyJokesActivityView view);
    void performPasswordReset(OnResetPasswordListener listener, String email);
}
