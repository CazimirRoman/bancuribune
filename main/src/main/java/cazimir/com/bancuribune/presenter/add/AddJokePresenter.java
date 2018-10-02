package cazimir.com.bancuribune.presenter.add;

import cazimir.com.interfaces.authentication.IAuthPresenter;
import cazimir.com.interfaces.repository.IJokesRepository;
import cazimir.com.interfaces.ui.add.IAddJokeActivityView;
import cazimir.com.interfaces.ui.add.OnAddJokeFinishedListener;
import cazimir.com.models.Joke;

/**
 * TODO: Add a class header comment!
 */
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
