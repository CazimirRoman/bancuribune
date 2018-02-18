package cazimir.com.bancuribune.ui.list;

import java.util.List;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;

public interface IMainActivityView extends IGeneralView {
    void setAdmin(Boolean value);
    void displayJokes(List<Joke> jokes);
    void requestFailed(String error);
    void navigateToAddJokeActivity();
    void isNotAllowedToAdd(int addLimit);
    void checkIfAdmin();
    void showAddSuccessDialog();
    void showAddFailedDialog();
    void showToast(String message);
    void showProgressBar();
    void hideProgressBar();
    void showAdminButton();
    void saveRankDataToSharedPreferences (Rank rank);
    void updateCurrentRank(String rank);
    void updateRemainingAdds(int remaininigAdds);
    void hideSwipeRefresh();
    void refreshAdapter(Joke joke);
    void refreshJokesListAdapter();
    void showAlertDialog(String message);
}
