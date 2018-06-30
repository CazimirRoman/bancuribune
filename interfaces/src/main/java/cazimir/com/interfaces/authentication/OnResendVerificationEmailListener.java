package cazimir.com.interfaces.authentication;

public interface OnResendVerificationEmailListener {
    void onResendEmailSuccess(String message);
    void onResendEmailFailed(String error);
}
