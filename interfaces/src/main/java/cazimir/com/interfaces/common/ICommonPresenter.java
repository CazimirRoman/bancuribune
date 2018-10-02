package cazimir.com.interfaces.common;

import java.io.IOException;
import java.util.List;

import cazimir.com.interfaces.authentication.IAuthPresenter;
import cazimir.com.interfaces.ui.myJokes.OnCalculatePointsListener;
import cazimir.com.interfaces.ui.myJokes.OnGetFacebookNameListener;
import cazimir.com.models.Joke;

public interface ICommonPresenter {
    void sendResetInstructions(String email);
    void performLogin(String email, String password);
    void registerUser(String email, String password);
    void getAllPendingJokesData();
    void getMyJokes();
    void getLikedJokes();
    void logOutUser();
    void approveJoke(String uid, String jokeUid);
    void getFacebookProfilePicture(OnGetProfilePictureListener listener) throws IOException;
    void getProfileName(OnGetFacebookNameListener listener);
    void calculateTotalPoints(OnCalculatePointsListener listener, List<Joke> jokes);
    void updateRankPointsAndName(int points, String rankName, String rankId);
    IAuthPresenter getAuthPresenter();
    void resendVerificationEmail(String email, String password);
}
