package cazimir.com.interfaces.ui.likedJokes;

import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.models.Joke;

public interface ILikedJokesActivityView extends IGeneralView {
    void showMyLikedJokesList(Joke joke);
    void showToast(String message);
    void getLikedJokes();
    void showNoLikedJokesText();
}
