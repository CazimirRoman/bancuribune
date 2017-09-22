package cazimir.com.bancuribune.ui.list;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface OnFirebaseGetAllJokesListener {
    void OnGetAllJokesSuccess(List<Joke> jokes);
    void OnGetAllJokesFailed(String error);
}
