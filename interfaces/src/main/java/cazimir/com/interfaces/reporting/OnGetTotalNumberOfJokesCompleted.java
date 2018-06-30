package cazimir.com.interfaces.reporting;

public interface OnGetTotalNumberOfJokesCompleted {
    void onSuccess(int size);
    void onFailed(String error);
}