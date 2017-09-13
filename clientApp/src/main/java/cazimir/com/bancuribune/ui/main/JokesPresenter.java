package cazimir.com.bancuribune.ui.main;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public class JokesPresenter implements OnRequestFinishedListener {

    private IJokesActivityView view;
    private JokesInteractor interactor;

    public JokesPresenter(IJokesActivityView view) {
        this.view = view;
        this.interactor = new JokesInteractor();
    }

    public void initData(){
        interactor.getJokes(this);
    }

    @Override
    public void onSuccess(List<Joke> jokes) {
        view.populateList(jokes);
    }

    @Override
    public void onError(String error) {
        view.requestFailed(error);
    }
}
