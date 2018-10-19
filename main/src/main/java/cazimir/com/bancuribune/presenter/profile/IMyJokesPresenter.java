package cazimir.com.bancuribune.presenter.profile;

import java.io.IOException;
import java.util.List;

import cazimir.com.bancuribune.callbacks.myJokes.OnCalculatePointsListener;
import cazimir.com.bancuribune.callbacks.myJokes.OnGetFacebookNameListener;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.view.profile.OnGetProfilePictureListener;

public interface IMyJokesPresenter {

    void getMyJokes();
    void logUserOut();
    void getProfileName(OnGetFacebookNameListener listener);
    void getFacebookProfilePicture(OnGetProfilePictureListener listener) throws IOException;
    void calculateTotalPoints(OnCalculatePointsListener listener, List<Joke> jokes);
    void updateRankPointsAndName(int points, String rankName, String rankId);
}
