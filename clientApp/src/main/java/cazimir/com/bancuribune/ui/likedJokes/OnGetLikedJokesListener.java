package cazimir.com.bancuribune.ui.likedJokes;

import java.util.ArrayList;

import cazimir.com.bancuribune.model.Joke;

public interface OnGetLikedJokesListener {
    void onGetLikedJokesSuccess(Joke joke);
    void onGetLikedJokesFailed(String error);
    void onNoLikedJokes();
}
