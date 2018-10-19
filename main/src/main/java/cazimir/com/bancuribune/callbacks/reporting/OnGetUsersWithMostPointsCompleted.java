package cazimir.com.bancuribune.callbacks.reporting;

import java.util.ArrayList;

import cazimir.com.bancuribune.model.Rank;

public interface OnGetUsersWithMostPointsCompleted {
    void onSuccess(ArrayList<Rank> rank);
    void onFailed(String error);
}
