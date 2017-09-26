package cazimir.com.bancuribune.ui.myjokes;


import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface IMyJokesActivityView {
    void showMyJokesList(List<Joke> jokes);
    String getRankDataFromSharedPreferences();
}
