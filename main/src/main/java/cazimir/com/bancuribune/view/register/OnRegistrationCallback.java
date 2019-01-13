package cazimir.com.bancuribune.view.register;

/**
 * TODO: Add a class header comment!
 */
public interface OnRegistrationCallback {
    void onRegistrationSuccess(String message);
    void onRegistrationFailed(String error);
}
