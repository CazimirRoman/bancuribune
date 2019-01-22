package cazimir.com.bancuribune.presenter.admin;

import cazimir.com.bancuribune.model.Joke;

/**
 * TODO: Add a class header comment!
 */
public interface IAdminPresenter {
    void approveJoke(String uid, String jokeUid);
    void getAllPendingJokesData();
    void deleteJoke(Joke joke);
}
