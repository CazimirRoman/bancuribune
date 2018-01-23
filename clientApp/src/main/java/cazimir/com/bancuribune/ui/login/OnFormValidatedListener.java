package cazimir.com.bancuribune.ui.login;

public interface OnFormValidatedListener {
    void onValidateSuccess(String email, String password);
    void onValidateFail(String what);
}
