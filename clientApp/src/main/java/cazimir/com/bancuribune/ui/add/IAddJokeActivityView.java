package cazimir.com.bancuribune.ui.add;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Joke;

public interface IAddJokeActivityView extends IGeneralView {
    boolean dataValid();
    void sendDataToDatabase(Joke joke);
    void onError();
    void closeAdd();
    void populateIntent(String jokeText);
}
