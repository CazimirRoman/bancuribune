package cazimir.com.bancuribune.ui.list;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface IMainActivityView {
    void refreshJokes(List<Joke> jokes);
    void requestFailed(String error);
    void navigateToAddJokeActivity();
    void isNotAllowedToAdd();
    void showAddSuccessDialog();
    void showAddFailedDialog();
    void showTestToast(String message);
    void redirectToLoginPage();
    void showProgressBar();
    void hideProgressBar();
    void showAlertDialog(String message);

}
