package cazimir.com.bancuribune.presenter;

import java.io.IOException;
import java.util.List;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.myjokes.OnCalculatePointsListener;
import cazimir.com.bancuribune.ui.myjokes.OnGetFacebookNameListener;

public interface ICommonPresenter {
    void sendResetInstructions(String email);
    void performLogin(String email, String password);
    void registerUser(String email, String password);
    void getAllJokesData(boolean reset, boolean swipe);
    void getFilteredJokesData(String text);
    void getAllPendingJokesData();
    void getMyJokes();
    void getLikedJokes();
    void addJoke(Joke joke, Boolean isAdmin);
    void addRankToDatabase();
    void checkIfAdmin();
    void checkNumberOfAdds(int addLimit);
    void checkAndGetMyRank();
    void isAllowedToAdd(int remainingAdds);
    void isNotAllowedToAdd(int addLimit);
    void logOutUser();
    void checkIfAlreadyVoted(Joke joke);
    void increaseJokePointByOne(Joke joke);
    void updateJokeApproval(String uid);
    void writeVoteLogToDB(String uid);
    void getFacebookProfilePicture(OnGetProfilePictureListener listener) throws IOException;
    void getProfileName(OnGetFacebookNameListener listener);
    void calculateTotalPoints(OnCalculatePointsListener listener, List<Joke> jokes);
    void updateRankPointsAndName(int points, String rankName, String rankId);


}
