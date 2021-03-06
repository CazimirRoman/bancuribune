package cazimir.com.bancuribune.callbacks.admin;


import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface OnGetAllPendingJokesListener {
    void onGetAllPendingJokesSuccess(List<Joke> jokes);
    void onGetAllPendingJokesFailed(String message);

}
