package cazimir.com.bancuribune.callbacks.admin;

public interface OnJokeApprovedListener {
    void onJokeApprovedSuccess(String message);
    void onJokeApprovedFailed(String error);
}
