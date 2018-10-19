package cazimir.com.bancuribune.presenter.liked;

import cazimir.com.bancuribune.callbacks.likedJokes.ILikedJokesActivityView;
import cazimir.com.bancuribune.callbacks.likedJokes.OnGetLikedJokesListener;
import cazimir.com.bancuribune.constant.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.auth.IAuthPresenter;
import cazimir.com.bancuribune.repository.IJokesRepository;

public class LikedJokesPresenter implements ILikedJokesPresenter {

    private ILikedJokesActivityView mView;
    private IJokesRepository mRepository;
    private IAuthPresenter mAuthPresenter;

    public LikedJokesPresenter(ILikedJokesActivityView view, IAuthPresenter authPresenter, IJokesRepository repository) {
        mView = view;
        mRepository = repository;
        mAuthPresenter = authPresenter;
    }

    @Override
    public void getLikedJokes() {
        mRepository.getVotesForUser(new OnGetLikedJokesListener() {
            @Override
            public void onGetLikedJokesSuccess(Joke myLikedJoke) {
                mView.showMyLikedJokesList(myLikedJoke);
            }

            @Override
            public void onGetLikedJokesFailed(String error) {
                mView.showToast(error);
            }

            @Override
            public void onNoLikedJokes() {
                mView.showNoLikedJokesText();
            }
        }, mAuthPresenter.getCurrentUserID());
    }

    @Override
    public boolean isAdmin() {
        if(mAuthPresenter.getCurrentUserID().equals(Constants.CAZIMIR) ||
                mAuthPresenter.getCurrentUserID().equals(Constants.ANA_MARIA)){
            return true;
        }
        return false;
    }

    @Override
    public IAuthPresenter getAuthPresenter() {
        return mAuthPresenter;
    }
}
