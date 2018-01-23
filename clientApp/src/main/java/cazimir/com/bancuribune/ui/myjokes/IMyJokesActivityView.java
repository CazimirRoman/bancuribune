package cazimir.com.bancuribune.ui.myjokes;


import java.util.List;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Joke;

public interface IMyJokesActivityView extends IGeneralView {
    void showMyJokesList(List<Joke> jokes);
    String getRankDataFromSharedPreferences();
}
