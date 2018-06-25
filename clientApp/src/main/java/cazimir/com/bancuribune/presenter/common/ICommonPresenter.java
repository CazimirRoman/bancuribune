package cazimir.com.bancuribune.presenter.common;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.authentication.IAuthPresenter;
import cazimir.com.bancuribune.ui.myJokes.OnCalculatePointsListener;
import cazimir.com.bancuribune.ui.myJokes.OnGetFacebookNameListener;

public interface ICommonPresenter {
    void sendResetInstructions(String email);
    void performLogin(String email, String password);
    void registerUser(String email, String password);
    void getAllJokesData(boolean reset, boolean swipe);
    void getAllPendingJokesData();
    void getMyJokes();
    void getLikedJokes();
    void addJoke(Joke joke, Boolean isAdmin);
    void addRankToDatabase();
    void checkIfAdmin();
    void checkNumberOfAdds(int addLimit);
    void checkAndGetMyRank();
    void logOutUser();
    void checkIfAlreadyVoted(Joke joke);
    void increaseJokePointByOne(Joke joke);
    void approveJoke(String uid);
    void writeVoteLogToDB(String uid);
    void getFacebookProfilePicture(OnGetProfilePictureListener listener) throws IOException;
    void getProfileName(OnGetFacebookNameListener listener);
    void calculateTotalPoints(OnCalculatePointsListener listener, List<Joke> jokes);
    void updateRankPointsAndName(int points, String rankName, String rankId);
    IAuthPresenter getAuthPresenter();
    void checkNumberOfAddsLastWeek(Date lastCheckDate);
    void resendVerificationEmail(String email, String password);
}
