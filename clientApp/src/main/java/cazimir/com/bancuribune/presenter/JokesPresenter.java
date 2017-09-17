package cazimir.com.bancuribune.presenter;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.add.IAddJokeActivityView;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.repository.IJokesRepository;
import cazimir.com.bancuribune.ui.list.IJokesActivityView;
import cazimir.com.bancuribune.ui.list.OnRequestFinishedListener;

public class JokesPresenter implements IJokesPresenter, OnRequestFinishedListener, OnAddFinishedListener {

    private IJokesActivityView mainView;
    private IAddJokeActivityView addView;
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
