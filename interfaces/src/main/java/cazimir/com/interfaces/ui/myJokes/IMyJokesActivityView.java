package cazimir.com.interfaces.ui.myJokes;


import java.util.List;

import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.models.Joke;

public interface IMyJokesActivityView extends IGeneralView {
    void showMyJokesList(List<Joke> jokes);
    String getRankDataFromSharedPreferences();
    void showToast(String message);
    void redirectToLoginPage();
    void clearSharedPreferences();
    void showAlertDialog(String message, int type);
}
