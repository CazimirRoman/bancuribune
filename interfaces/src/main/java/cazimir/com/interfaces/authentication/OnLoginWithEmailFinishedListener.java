package cazimir.com.interfaces.authentication;

/**
 * TODO: Add a class header comment!
 */
public interface OnLoginWithEmailFinishedListener {
    void onLoginWithEmailSuccess();
    void onLoginWithEmailFailed(String error);
}
