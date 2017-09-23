package cazimir.com.bancuribune.ui.list;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;

public interface IMainActivityView {
    void refreshJokes(List<Joke> jokes);
    void requestFailed(String error);
    void navigateToAddJokeActivity();
    void isNotAllowedToAdd(int addLimit);
    void checkIfAdmin();
    void showAddSuccessDialog();
    void showAddFailedDialog();
    void showTestToast(String message);
    void redirectToLoginPage();
    void showProgressBar();
    void hideProgressBar();
    void showAdminButton();
    void showAlertDialog(String message);
    void saveRankDataToSharedPreferences (Rank rank);
    void updateCurrentRank(String rank);
    void updateRemainingAdds(int remaininigAdds);

}
