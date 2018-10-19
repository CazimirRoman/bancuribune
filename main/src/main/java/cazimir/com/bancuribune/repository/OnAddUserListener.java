package cazimir.com.bancuribune.repository;

public interface OnAddUserListener {
    void onAddUserFailed(String message);
    void onAddUserSuccess();
}
