package cazimir.com.interfaces.ui.list;

import java.util.Date;
import java.util.List;

import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.models.Joke;
import cazimir.com.models.Rank;

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
    void showReportButton();
    void saveRankDataToSharedPreferences(Rank rank);
    void updateCurrentRank(String rank);
    void updateRemainingAdds(int remaining);
    void hideSwipeRefresh();
    void refreshAdapter(Joke joke);
    void refreshJokesListAdapter();
    void showAlertDialog(String message, int type);
    Date addLastCheckDateToSharedPreferences();
    void checkIfNewRank(String rank);
    void playOnVotedAudio();
    void sendJokeToTrello(String joke);
    void goToMyJokesActivity();
}
