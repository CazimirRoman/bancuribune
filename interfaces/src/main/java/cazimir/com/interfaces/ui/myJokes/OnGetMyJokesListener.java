package cazimir.com.interfaces.ui.myJokes;

import java.util.List;

import cazimir.com.models.Joke;

public interface OnGetMyJokesListener {
    void onGetMyJokesSuccess(List<Joke> jokes);
    void onGetMyJokesError(String error);
}
