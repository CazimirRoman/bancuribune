package cazimir.com.bancuribune.repository;


import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnRequestAllFinishedListener;

public interface IJokesRepository {
    void getAllJokes(OnRequestAllFinishedListener listener);
    void addJoke(OnAddFinishedListener listener, Joke joke);
    void getAllJokesAddedToday(OnAllowedToAddFinishedListener listener, String userID);
}
