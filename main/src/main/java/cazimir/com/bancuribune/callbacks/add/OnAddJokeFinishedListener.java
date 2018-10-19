package cazimir.com.bancuribune.callbacks.add;

public interface OnAddJokeFinishedListener {
    void onAddSuccess();
    void onAddFailed(String message);
}
