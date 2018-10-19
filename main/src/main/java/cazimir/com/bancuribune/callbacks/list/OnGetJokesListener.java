package cazimir.com.bancuribune.callbacks.list;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface OnGetJokesListener {
    void onGetJokesSuccess(List<Joke> jokes);
    void onGetJokesFailed(String error);
    void onEndOfListReached();
}
