package cazimir.com.bancuribune.presenter.login;

import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

/**
 * TODO: Add a class header comment!
 */
public interface ILoginPresenter {
    void performLogin(String email, String password);
    void performAnonymousLogin();
    void checkIfUserLoggedIn();
    FacebookCallback<LoginResult> loginWithFacebook();
}
