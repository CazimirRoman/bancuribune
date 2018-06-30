package cazimir.com.interfaces.reporting;

import java.util.ArrayList;

import cazimir.com.models.Rank;

public interface OnGetUsersWithMostPointsCompleted {
    void onSuccess(ArrayList<Rank> rank);
    void onFailed(String error);
}