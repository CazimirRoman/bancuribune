package cazimir.com.bancuribune.ui.main;

import cazimir.com.bancuribune.model.Joke;

interface IAddJokeActivityView {
    boolean dataValid();
    void onDataValidated();
    void sendDataToDatabase(Joke joke);
    void onError(String error);
    void closeAdd();
}
