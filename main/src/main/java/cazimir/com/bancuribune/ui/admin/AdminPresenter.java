package cazimir.com.bancuribune.ui.admin;

import java.util.List;

import cazimir.com.interfaces.repository.IJokesRepository;
import cazimir.com.interfaces.ui.admin.IAdminActivityView;
import cazimir.com.interfaces.ui.admin.OnGetAllPendingJokesListener;
import cazimir.com.interfaces.ui.admin.OnJokeApprovedListener;
import cazimir.com.models.Joke;
import cazimir.com.repository.JokesRepository;

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