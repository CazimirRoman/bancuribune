package cazimir.com.bancuribune.view.forgotPassword;

public interface OnResendVerificationEmailListener {
    void onResendEmailSuccess(String message);
    void onResendEmailFailed(String error);
}
