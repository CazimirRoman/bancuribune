package cazimir.com.interfaces.authentication;

/**
 * TODO: Add a class header comment!
 */
public interface OnResetPasswordListener {
    void onResetPasswordSuccess(String message);
    void onResetPasswordFailed(String error);
}
