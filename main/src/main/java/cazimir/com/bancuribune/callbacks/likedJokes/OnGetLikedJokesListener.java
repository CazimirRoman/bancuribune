package cazimir.com.bancuribune.callbacks.likedJokes;

import cazimir.com.bancuribune.model.Joke;

public interface OnGetLikedJokesListener {
    void onGetLikedJokesSuccess(Joke joke);
    void onGetLikedJokesFailed(String error);
    void onNoLikedJokes();
}
