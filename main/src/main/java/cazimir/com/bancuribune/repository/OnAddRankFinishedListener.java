package cazimir.com.bancuribune.repository;

import cazimir.com.bancuribune.model.Rank;

public interface OnAddRankFinishedListener {
    void onAddRankSuccess(Rank rank);
    void onAddRankFailed(String error);
}
