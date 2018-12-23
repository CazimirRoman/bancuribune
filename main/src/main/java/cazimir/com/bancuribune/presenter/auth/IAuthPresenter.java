package cazimir.com.bancuribune.presenter.auth;


import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import cazimir.com.bancuribune.presenter.login.OnAnonymousLoginCallback;
import cazimir.com.bancuribune.view.list.OnSaveInstanceIdToUserObjectCallback;
import cazimir.com.bancuribune.view.login.OnLoginWithEmailCallback;
import cazimir.com.bancuribune.view.register.OnRegistrationCallback;
import cazimir.com.bancuribune.view.forgotPassword.OnResendVerificationEmailListener;
import cazimir.com.bancuribune.view.forgotPassword.OnResetPasswordListener;
import cazimir.com.bancuribune.callbacks.myJokes.IMyJokesActivityView;

public interface IAuthPresenter {
    void login (OnLoginWithEmailCallback listener, String email, String password);
    void registerUser(OnRegistrationCallback listener, String email, String password);
    FacebookCallback<LoginResult> loginWithFacebook();
    void checkIfUserLoggedIn();
    void saveInstanceIdToUserObject(OnSaveInstanceIdToUserObjectCallback callback);
    boolean isLoggedInViaFacebook();
    String getCurrentUserID();
    String getCurrentUserName();
    void logUserOut(IMyJokesActivityView view);
    void performPasswordReset(OnResetPasswordListener listener, String email);
    void performResendVerificationEmail(OnResendVerificationEmailListener listener, String email, String password);
    boolean isAdmin();
    boolean isLoggedInAnonymously();
    void loginAnonymously(OnAnonymousLoginCallback onAnonymousLoginCallback);
}
