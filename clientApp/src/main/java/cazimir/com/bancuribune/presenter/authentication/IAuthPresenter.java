package cazimir.com.bancuribune.presenter.authentication;


import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import cazimir.com.bancuribune.presenter.common.OnResendVerificationEmailListener;
import cazimir.com.bancuribune.ui.myJokes.IMyJokesActivityView;

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
    void performResendVerificationEmail(OnResendVerificationEmailListener listener, String email, String password);
}
