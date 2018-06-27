package cazimir.com.interfaces.repository;

public interface OnAddUserListener {
    void onAddUserFailed(String message);
    void onAddUserSuccess();
}
