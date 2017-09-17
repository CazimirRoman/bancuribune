package cazimir.com.bancuribune.repository;


import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnRequestFinishedListener;

public interface IJokesRepository {
    void getAllJokes(OnRequestFinishedListener listener);
    void addJoke(OnAddFinishedListener listener, Joke joke);
}
