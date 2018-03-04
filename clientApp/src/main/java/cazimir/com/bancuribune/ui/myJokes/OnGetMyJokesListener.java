package cazimir.com.bancuribune.ui.myJokes;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface OnGetMyJokesListener {
    void onGetMyJokesSuccess(List<Joke> jokes);
    void onGetMyJokesError(String error);
}
