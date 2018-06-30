package cazimir.com.interfaces.repository;

import cazimir.com.models.Rank;

public interface OnCheckIfRankDataInDBListener {
    void rankDataIsInDB(Rank rank);
    void rankDataNotInDB();
}
