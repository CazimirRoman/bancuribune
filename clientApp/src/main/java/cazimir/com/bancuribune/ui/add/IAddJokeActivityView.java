package cazimir.com.bancuribune.ui.add;

import cazimir.com.bancuribune.model.Joke;

public interface IAddJokeActivityView {
    boolean dataValid();
    void onDataValidated();
    void sendDataToDatabase(Joke joke);
    void onError(String error);
    void closeAdd();
}
