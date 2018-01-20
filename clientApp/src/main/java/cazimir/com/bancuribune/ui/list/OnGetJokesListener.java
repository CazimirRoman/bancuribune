package cazimir.com.bancuribune.ui.list;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface OnGetJokesListener {
    void OnGetJokesSuccess(List<Joke> jokes);
    void OnGetJokesFailed(String error);
    void OnEndOfListReached();
}
