package cazimir.com.interfaces.common;

import java.io.IOException;
import java.util.List;

import cazimir.com.interfaces.authentication.IAuthPresenter;
import cazimir.com.interfaces.ui.myJokes.OnCalculatePointsListener;
import cazimir.com.interfaces.ui.myJokes.OnGetFacebookNameListener;
import cazimir.com.models.Joke;

public interface ICommonPresenter {
    void performLogin(String email, String password);
    void registerUser(String email, String password);
    void getMyJokes();
    void getLikedJokes();
    void logOutUser();
    void getFacebookProfilePicture(OnGetProfilePictureListener listener) throws IOException;
    void getProfileName(OnGetFacebookNameListener listener);
    void calculateTotalPoints(OnCalculatePointsListener listener, List<Joke> jokes);
    void updateRankPointsAndName(int points, String rankName, String rankId);
    IAuthPresenter getAuthPresenter();
    String getCurrentUserID();
}
