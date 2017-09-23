package cazimir.com.bancuribune.ui.add;

import cazimir.com.bancuribune.model.Joke;

public interface IAddJokeActivityView {
    void showAlertDialog(String message);
    boolean dataValid();
    void onDataValidated();
    void sendDataToDatabase(Joke joke);
    void onError();
    void closeAdd();
}
