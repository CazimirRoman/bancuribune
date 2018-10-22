package cazimir.com.bancuribune.presenter.auth;


import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import cazimir.com.bancuribune.callbacks.login.OnLoginWithFacebookCallback;
import cazimir.com.bancuribune.view.profile.IMyJokesActivityView;
import cazimir.com.bancuribune.callbacks.login.OnCheckIfLoggedInCallback;
import cazimir.com.bancuribune.view.forgotPassword.OnResendVerificationEmailListener;
import cazimir.com.bancuribune.view.forgotPassword.OnResetPasswordListener;
import cazimir.com.bancuribune.view.login.OnLoginWithEmailFinishedListener;
import cazimir.com.bancuribune.callbacks.register.OnRegistrationFinishedListener;

public interface IAuthPresenter {
    void login (OnLoginWithEmailFinishedListener listener, String email, String password);
    void registerUser(OnRegistrationFinishedListener listener, String email, String password);
    FacebookCallback<LoginResult> loginWithFacebook(OnLoginWithFacebookCallback callback);
    void checkIfUserLoggedIn(OnCheckIfLoggedInCallback callback);
    boolean isLoggedInViaFacebook();
    String getCurrentUserID();
    String getCurrentUserName();
    String getCurrentUserEmail();
    void logUserOut(IMyJokesActivityView view);
    void performPasswordReset(OnResetPasswordListener listener, String email);
    void performResendVerificationEmail(OnResendVerificationEmailListener listener, String email, String password);
}
