package cazimir.com.bancuribune.presenter.admin;

import java.util.List;

import cazimir.com.bancuribune.callbacks.admin.OnGetAllPendingJokesListener;
import cazimir.com.bancuribune.callbacks.admin.OnJokeApprovedListener;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.repository.IJokesRepository;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.view.admin.IAdminActivityView;

public class AdminPresenter implements IAdminPresenter {

    private IJokesRepository mRepository;
    private IAdminActivityView mView;

    public AdminPresenter(IAdminActivityView view, JokesRepository repository) {
        this.mView = view;
        this.mRepository = repository;
    }

    @Override
    public void approveJoke(String uid, String jokeText) {
        mRepository.approveJoke(new OnJokeApprovedListener() {
            @Override
            public void onJokeApprovedSuccess() {
                mView.showToast("Approved!");
                mView.getAllPendingJokes();
            }

            @Override
            public void onJokeApprovedFailed(String error) {
                mView.showToast(error);
            }
        }, uid, jokeText);
    }

    @Override
    public void getAllPendingJokesData() {
        mRepository.getAllPendingJokes(new OnGetAllPendingJokesListener() {
            @Override
            public void onGetAllPendingJokesSuccess(List<Joke> jokes) {
                mView.refreshJokes(jokes);
            }

            @Override
            public void onGetAllPendingJokesFailed(String message) {
                mView.showToast(message);
            }
        });
    }
}