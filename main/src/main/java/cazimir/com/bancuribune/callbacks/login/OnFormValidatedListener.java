package cazimir.com.bancuribune.callbacks.login;

public interface OnFormValidatedListener {
    void onValidateSuccess(String email, String password);
    void onValidateFail(String what);
}
