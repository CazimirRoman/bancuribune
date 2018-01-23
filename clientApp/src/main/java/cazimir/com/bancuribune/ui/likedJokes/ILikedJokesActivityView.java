package cazimir.com.bancuribune.ui.likedJokes;

import java.util.List;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Joke;

public interface ILikedJokesActivityView extends IGeneralView {
    void addToLikedJokeList(Joke joke);
    void showToast(String message);
    void getLikedJokes();
}
