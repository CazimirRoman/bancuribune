package cazimir.com.interfaces.authentication;

/**
 * TODO: Add a class header comment!
 */
public interface OnRegistrationFinishedListener {
    void onRegistrationSuccess(String message);
    void onRegistrationFailed(String error);
}
