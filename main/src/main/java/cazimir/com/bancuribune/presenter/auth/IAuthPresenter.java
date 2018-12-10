package cazimir.com.bancuribune.presenter.auth;


import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import cazimir.com.bancuribune.view.login.OnLoginWithEmailFinishedListener;
import cazimir.com.bancuribune.view.register.OnRegistrationFinishedListener;
import cazimir.com.bancuribune.view.forgotPassword.OnResendVerificationEmailListener;
import cazimir.com.bancuribune.view.forgotPassword.OnResetPasswordListener;
import cazimir.com.bancuribune.callbacks.myJokes.IMyJokesActivityView;

public interface IAuthPresenter {
    void login (OnLoginWithEmailFinishedListener listener, String email, String password);
    void registerUser(OnRegistrationFinishedListener listener, String email, String password);
    FacebookCallback<LoginResult> loginWithFacebook();
    void checkIfUserLoggedIn();
    void saveInstanceIdToUserObject();
    boolean isLoggedInViaFacebook();
    String getCurrentUserID();
    String getCurrentUserName();
    String getCurrentUserEmail();
    void logUserOut(IMyJokesActivityView view);
    void performPasswordReset(OnResetPasswordListener listener, String email);
    void performResendVerificationEmail(OnResendVerificationEmailListener listener, String email, String password);
    boolean isAdmin();
    boolean isLoggedInAnonymously();
    void signInAnonymously(OnLoginWithEmailFinishedListener onLoginWithEmailFinishedListener);
}
