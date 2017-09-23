package cazimir.com.bancuribune.ui.admin;


import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface OnGetAllPendingJokesListener {
    void OnGetAllPendingJokesSuccess(List<Joke> jokes);
    void OnGetAllPendingJokesFailed(String message);

}
