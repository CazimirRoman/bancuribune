package cazimir.com.interfaces.ui.likedJokes;

import cazimir.com.models.Joke;

public interface OnGetLikedJokesListener {
    void onGetLikedJokesSuccess(Joke joke);
    void onGetLikedJokesFailed(String error);
    void onNoLikedJokes();
}
