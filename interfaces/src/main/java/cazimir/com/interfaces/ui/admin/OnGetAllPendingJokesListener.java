package cazimir.com.interfaces.ui.admin;


import java.util.List;

import cazimir.com.models.Joke;

public interface OnGetAllPendingJokesListener {
    void onGetAllPendingJokesSuccess(List<Joke> jokes);
    void onGetAllPendingJokesFailed(String message);

}
