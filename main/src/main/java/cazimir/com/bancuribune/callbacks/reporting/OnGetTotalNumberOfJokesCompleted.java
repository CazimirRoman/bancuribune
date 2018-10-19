package cazimir.com.bancuribune.callbacks.reporting;

public interface OnGetTotalNumberOfJokesCompleted {
    void onSuccess(int size);
    void onFailed(String error);
}