package cazimir.com.bancuribune.presenter.liked;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.auth.IAuthPresenter;

public interface ILikedJokesPresenter {
    void getLikedJokes();
    boolean isAdmin();
    IAuthPresenter getAuthPresenter();
    void removeJokeFromFavorites(Joke mJokeToBeRemoved);
}