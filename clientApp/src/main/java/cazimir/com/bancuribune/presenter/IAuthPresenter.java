package cazimir.com.bancuribune.presenter;


import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

public interface IAuthPresenter {
    FacebookCallback<LoginResult> loginWithFacebook();
    void checkIfUserLoggedIn();
    String getCurrentUserID();
}
