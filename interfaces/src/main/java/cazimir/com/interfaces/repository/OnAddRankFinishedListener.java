package cazimir.com.interfaces.repository;

import cazimir.com.models.Rank;

public interface OnAddRankFinishedListener {
    void onAddRankSuccess(Rank rank);
    void onAddRankFailed(String error);
}
