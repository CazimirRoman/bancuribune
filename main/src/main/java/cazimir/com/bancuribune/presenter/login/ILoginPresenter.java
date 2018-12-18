package cazimir.com.bancuribune.presenter.login;

import cazimir.com.bancuribune.view.login.OnLoginWithEmailFinishedListener;

/**
 * TODO: Add a class header comment!
 */
public interface ILoginPresenter {
    void performLogin(String email, String password);
    void performAnonymousLogin();
}
