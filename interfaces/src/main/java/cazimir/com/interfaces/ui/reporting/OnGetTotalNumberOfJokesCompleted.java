package cazimir.com.interfaces.ui.reporting;

public interface OnGetTotalNumberOfJokesCompleted {
    void onSuccess(int size);
    void onFailed(String error);
}