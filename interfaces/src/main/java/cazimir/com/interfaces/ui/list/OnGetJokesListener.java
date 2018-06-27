package cazimir.com.interfaces.ui.list;

import java.util.List;

import cazimir.com.models.Joke;

public interface OnGetJokesListener {
    void onGetJokesSuccess(List<Joke> jokes);
    void onGetJokesFailed(String error);
    void onEndOfListReached();
}
