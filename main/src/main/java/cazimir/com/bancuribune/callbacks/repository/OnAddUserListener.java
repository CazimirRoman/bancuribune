package cazimir.com.bancuribune.callbacks.repository;

public interface OnAddUserListener {
    void onAddUserFailed(String message);
    void onAddUserSuccess();
}
