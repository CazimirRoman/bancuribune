package cazimir.com.bancuribune.presenter.common;

public interface OnResendVerificationEmailListener {
    void onResendEmailSuccess(String message);
    void onResendEmailFailed(String error);
}