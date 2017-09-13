package cazimir.com.bancuribune.ui.main;


import cazimir.com.bancuribune.model.Joke;

interface IJokesInteractor {
    void getAllJokes(OnRequestFinishedListener listener);
    void addJoke(OnRequestFinishedListener listener, Joke joke);
}
