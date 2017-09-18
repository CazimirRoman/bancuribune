package cazimir.com.bancuribune.ui.myjokes;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface OnFirebaseGetMyJokesListener {
    void onGetMyJokesSuccess(List<Joke> jokes);
    void onGetMyJokesError(String error);
}
