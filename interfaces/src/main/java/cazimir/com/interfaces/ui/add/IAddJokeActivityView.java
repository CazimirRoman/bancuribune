package cazimir.com.interfaces.ui.add;

import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.models.Joke;

public interface IAddJokeActivityView extends IGeneralView {
    boolean dataValid();
    void sendDataToDatabase(Joke joke);
    void onError();
    void closeAdd();
    void populateIntent(String jokeText);
}