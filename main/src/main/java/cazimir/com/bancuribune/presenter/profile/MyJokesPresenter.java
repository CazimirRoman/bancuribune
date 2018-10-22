package cazimir.com.bancuribune.presenter.profile;

import android.util.Log;

import com.facebook.Profile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import cazimir.com.bancuribune.view.profile.IMyJokesActivityView;
import cazimir.com.bancuribune.callbacks.myJokes.OnCalculatePointsListener;
import cazimir.com.bancuribune.callbacks.myJokes.OnGetFacebookNameListener;
import cazimir.com.bancuribune.callbacks.myJokes.OnGetMyJokesListener;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.auth.IAuthPresenter;
import cazimir.com.bancuribune.repository.IJokesRepository;
import cazimir.com.bancuribune.callbacks.repository.OnUpdateRankPointsSuccess;
import cazimir.com.bancuribune.view.profile.OnGetProfilePictureListener;

public class MyJokesPresenter implements IMyJokesPresenter {

    private static final String TAG = MyJokesPresenter.class.getSimpleName();

    private IMyJokesActivityView mView;
    private IAuthPresenter mAuthPresenter;
    private IJokesRepository mRepository;

    public MyJokesPresenter(IMyJokesActivityView view, IAuthPresenter authPresenter, IJokesRepository repository) {
        mView = view;
        mAuthPresenter = authPresenter;
        mRepository = repository;
    }

    @Override
    public void logUserOut() {
        mAuthPresenter.logUserOut(mView);
    }

    @Override
    public void getProfileName(OnGetFacebookNameListener listener) {
        if (mAuthPresenter.isLoggedInViaFacebook()) {
            String name = Profile.getCurrentProfile().getName();
            if (name != null) {
                listener.onGetFacebookNameSuccess(name);
            } else {
                listener.onGetFacebookNameFailed();
            }
        } else {
            listener.onGetFacebookNameSuccess(mAuthPresenter.getCurrentUserName());
        }
    }

    @Override
    public void getFacebookProfilePicture(final OnGetProfilePictureListener listener) throws IOException {
        if (mAuthPresenter.isLoggedInViaFacebook()) {
            String id = Profile.getCurrentProfile().getId();
            final URL imageURL = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
            listener.onGetProfilePictureSuccess(imageURL);
        } else {
            listener.onGetProfilePictureFailed();
        }
    }

    @Override
    public void getMyJokes() {
        mRepository.getMyJokes(new OnGetMyJokesListener() {
            @Override
            public void onGetMyJokesSuccess(List<Joke> jokes) {
                mView.showMyJokesList(jokes);
            }

            @Override
            public void onGetMyJokesError(String error) {
                mView.showToast(error);
            }
        }, mAuthPresenter.getCurrentUserID());
    }

    @Override
    public void calculateTotalPoints(OnCalculatePointsListener listener, List<Joke> jokes) {
        int points = 0;
        for (Joke joke : jokes) {
            points = points + joke.getPoints();
        }

        listener.onCalculateSuccess(points);
    }

    @Override
    public void updateRankPointsAndName(int points, String rankName, String rankId) {
        mRepository.updateRankPointsAndName(new OnUpdateRankPointsSuccess() {
            @Override
            public void onUpdateRankPointsSuccess() {
                Log.d(TAG, "Rank points updated");
            }
        }, rankName, points, mAuthPresenter.getCurrentUserID());
    }
}