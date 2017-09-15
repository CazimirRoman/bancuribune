package cazimir.com.bancuribune.ui.main;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public class JokesPresenter implements IJokesPresenter, OnRequestFinishedListener, OnAddFinishedListener {

    private IJokesActivityView mainView;
    private  IAddJokeActivityView addView;
    private IJokesRepository repository;

    public JokesPresenter(IJokesActivityView view, IJokesRepository repository) {
        this.mainView = view;
        this.repository = repository;
    }

    public JokesPresenter(IAddJokeActivityView view, IJokesRepository repository) {
        this.addView = view;
        this.repository = repository;
    }

    public void getAllJokesData(){
        repository.getAllJokes(this);
    }

    @Override
    public void addJoke(Joke joke) {
        repository.addJoke(this, joke);
    }

    @Override
    public void onSuccess(List<Joke> jokes) {
        mainView.refreshJokes(jokes);
    }

    @Override
    public void onError(String error) {
        mainView.requestFailed(error);
    }

    @Override
    public void OnAddSuccess() {
        //getAllJokesData();
        addView.closeAdd();
    }
}
