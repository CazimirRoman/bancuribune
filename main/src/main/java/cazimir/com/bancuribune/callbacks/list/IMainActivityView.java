package cazimir.com.bancuribune.callbacks.list;

import java.util.Date;
import java.util.List;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.view.list.OnAddUserToDatabaseCallback;

public interface IMainActivityView extends IGeneralView {
    void getAllJokesData(boolean reset, boolean swipe, boolean mostVoted);
    void displayJokes(List<Joke> jokes);
    void requestFailed(String error);
    void navigateToAddJokeActivity();
    void isNotAllowedToAdd(int addLimit);
    void updateUIForAdmin();
    void showAddSuccessDialog();
    void showAddFailedDialog();
    void showToast(String message);
    void showProgressBar();
    void hideProgressBar();
    void showAdminButtons();
    void saveRankDataToSharedPreferences(Rank rank);
    void updateRemainingAdds(int remaining);
    void hideSwipeRefresh();
    void refreshAdapter(Joke joke);
    void addUserToDatabase(OnAddUserToDatabaseCallback callback);
    void refreshJokesListAdapter();
    void enableHeartIcon(int position);
    void showAlertDialog(String message, int type);
    Date addLastCheckDateToSharedPreferences();
    void playOnVotedAudio();
    void sendJokeToTrello(String joke);
    void goToMyJokesActivity();
    void writeToLog(String message);
    void logEvent(String event);
    void hideKeyboard();
}
