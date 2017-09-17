package cazimir.com.bancuribune.presenter;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.repository.IJokesRepository;
import cazimir.com.bancuribune.ui.add.IAddJokeActivityView;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.IJokesActivityView;
import cazimir.com.bancuribune.ui.list.OnRequestAllFinishedListener;

public class CommonPresenter implements ICommonPresenter, OnRequestAllFinishedListener, OnAddFinishedListener, OnAllowedToAddFinishedListener {

    private IJokesActivityView mainView;
    private IAddJokeActivityView addView;
    private IJokesRepository repository;
    private IAuthPresenter authPresenter;

    public CommonPresenter(IJokesActivityView view, IJokesRepository repository) {
        this.mainView = view;
        this.repository = repository;
        this.authPresenter = new AuthPresenter(view);
    }

    public CommonPresenter(IAddJokeActivityView view, IJokesRepository repository) {
        this.addView = view;
        this.repository = repository;
        this.authPresenter = new AuthPresenter(view);
    }

    public void getAllJokesData(){
        repository.getAllJokes(this);
    }

    @Override
    public void addJoke(Joke joke) {
        joke.setCreatedBy(authPresenter.getCurrentUserID());
        joke.setUserName(authPresenter.getCurrentUserName());
        repository.addJoke(this, joke);
    }

    @Override
    public void checkNumberOfAdds() {
        repository.getAllJokesAddedToday(this, authPresenter.getCurrentUserID());
    }

    @Override
    public void isAllowedToAdd() {
        mainView.navigateToAddJokeActivity();
    }

    @Override
    public void isNotAllowedToAdd() {
        mainView.isNotAllowedToAdd();
    }

    @Override
    public void logOutUser() {
        authPresenter.logUserOut();
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
        addView.closeAdd();
    }
}
