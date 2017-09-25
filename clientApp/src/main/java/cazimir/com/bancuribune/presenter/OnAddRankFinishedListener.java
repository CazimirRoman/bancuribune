package cazimir.com.bancuribune.presenter;


import cazimir.com.bancuribune.model.Rank;

public interface OnAddRankFinishedListener {
    void OnAddRankSuccess(Rank rank);
    void OnAddRankFailure(String error);
}
