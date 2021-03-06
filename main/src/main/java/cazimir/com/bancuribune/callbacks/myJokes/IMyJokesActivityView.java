package cazimir.com.bancuribune.callbacks.myJokes;


import java.util.List;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Joke;

public interface IMyJokesActivityView extends IGeneralView {
    void showMyJokesList(List<Joke> jokes);
    String getRankDataFromSharedPreferences();
    void showToast(String message);
    void redirectToLoginPage();
    void clearSharedPreferences();
    void scrollToJokeIfFromPushNotification();
    void showAlertDialog(String message, int type);
    void showProgressBarForNewestMostVoted();
    void hideProgressBarForNewestMostVotedButton();
    void setMostPoints(boolean mostPoints);
}
