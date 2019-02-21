package cazimir.com.bancuribune.callbacks.list;

import java.util.ArrayList;

import cazimir.com.bancuribune.model.Joke;

/**
 * TODO: Add a class header comment!
 */
public interface OnGetMostVotedJokesListener {
    void onSuccess(ArrayList<Joke> jokes);
    void onFailed(String error);
}
