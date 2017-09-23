package cazimir.com.bancuribune.presenter;

import cazimir.com.bancuribune.model.Rank;

public interface OnCheckIfRankDataInDBListener {
    void RankDataIsInDB(Rank rank);
    void RankDataNotInDB();
}
