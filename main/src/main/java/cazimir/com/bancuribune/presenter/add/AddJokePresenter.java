package cazimir.com.bancuribune.presenter.add;

import cazimir.com.bancuribune.callbacks.add.OnAddJokeFinishedListener;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.auth.IAuthPresenter;
import cazimir.com.bancuribune.repository.IJokesRepository;
import cazimir.com.bancuribune.view.add.IAddJokeActivityView;

public class AddJokePresenter implements IAddJokePresenter {

    private IAddJokeActivityView mView;
    private IAuthPresenter mAuthPresenter;
    private IJokesRepository mRepository;

    public AddJokePresenter(IAddJokeActivityView view, IAuthPresenter authPresenter, IJokesRepository repository) {
        mView = view;
        mAuthPresenter = authPresenter;
        mRepository = repository;
    }

    @Override
    public void addJoke(final Joke joke, final Boolean isAdmin) {

        joke.setCreatedBy(mAuthPresenter.getCurrentUserID());
        joke.setUserName(mAuthPresenter.getCurrentUserName());

        if (isAdmin) {
            joke.setApproved(true);
        }

        mRepository.addJoke(new OnAddJokeFinishedListener() {
            @Override
            public void onAddSuccess() {
                if (!isAdmin) {
                    mView.populateIntent(joke.getJokeText());
                }

                mView.closeAdd();

            }

            @Override
            public void onAddFailed(String message) {
                mView.onError();
            }
        }, joke);
    }
}