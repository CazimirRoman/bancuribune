package cazimir.com.bancuribune.presenter;


import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

import cazimir.com.bancuribune.ui.list.IMainActivityView;
import cazimir.com.bancuribune.ui.login.OnAuthStateListenerRegister;

public interface IAuthPresenter {
    FacebookCallback<LoginResult> loginWithFacebook();
    void checkIfUserLoggedIn();
    String getCurrentUserID();
    String getCurrentUserName();
    void logUserOut(IMainActivityView view);
    void registerAuthChangeListener(OnAuthStateListenerRegister listener);
}
