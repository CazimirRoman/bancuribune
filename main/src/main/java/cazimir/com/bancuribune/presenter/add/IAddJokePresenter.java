package cazimir.com.bancuribune.presenter.add;

import cazimir.com.bancuribune.model.Joke;

public interface IAddJokePresenter {
    void addJoke(Joke joke, Boolean isAdmin);
    boolean isAdmin();
}
