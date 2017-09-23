package cazimir.com.bancuribune.ui.myjokes;

public interface OnCalculatePointsListener {
    void OnCalculateSuccess(int points);
    void OnCalculateFailed(String error);
}
