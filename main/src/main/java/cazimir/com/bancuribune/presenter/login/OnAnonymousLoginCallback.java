package cazimir.com.bancuribune.presenter.login;

public interface OnAnonymousLoginCallback {
    void onSuccess();
    void onFailed(String message);
}
