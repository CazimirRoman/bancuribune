package cazimir.com.bancuribune.ui.main;


import cazimir.com.bancuribune.model.Joke;

interface IJokesRepository {
    void getAllJokes(OnRequestFinishedListener listener);
    void addJoke(OnAddFinishedListener listener, Joke joke);
}
