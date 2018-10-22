package cazimir.com.bancuribune.callbacks.repository;

import cazimir.com.bancuribune.model.Rank;

public interface OnCheckIfRankDataInDBListener {
    void rankDataIsInDB(Rank rank);
    void rankDataNotInDB();
}
