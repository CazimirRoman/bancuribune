package cazimir.com.bancuribune.ui.login;


import android.app.Activity;

public interface ILoginActivityView {
    void launchMainActivity();
    void loginSucces();
    void loginFailed(String message);
    Activity getContext();
}
