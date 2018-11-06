package cazimir.com.bancuribune.callbacks.likedJokes;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Joke;

public interface ILikedJokesActivityView extends IGeneralView {
    void showProgressBar();

    void hideProgressBar();

    void showMyLikedJokesList(Joke joke);

    void deleteLikedJokeFromAdapter(Joke joke);

    void showToast(String message);
    void getLikedJokes();
    void showNoLikedJokesText();
    void removeJokeFromFavorites();
}
