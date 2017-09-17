package cazimir.com.bancuribune.presenter;


import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;

public interface ILoginPresenter {
    FacebookCallback<LoginResult> loginWithFacebook();
    void checkIfUserLoggedIn();
}
