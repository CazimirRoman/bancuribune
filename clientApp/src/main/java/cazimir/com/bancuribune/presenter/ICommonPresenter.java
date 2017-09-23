package cazimir.com.bancuribune.presenter;

import java.io.IOException;
import java.util.List;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.list.OnCheckIfAdminListener;
import cazimir.com.bancuribune.ui.myjokes.OnCalculatePointsListener;
import cazimir.com.bancuribune.ui.myjokes.OnGetFacebookNameListener;

public interface ICommonPresenter {
    void getAllJokesData();
    void getFilteredJokesData(String text);
    void getAllPendingJokesData();
    void getMyJokes();
    void addJoke(Joke joke);
    void addRankToDatabase();
    void checkIfAdmin(OnCheckIfAdminListener listener);
    void checkNumberOfAdds(int addLimit);
    void checkAndGetMyRank();
    void isAllowedToAdd(int remainingAdds);
    void isNotAllowedToAdd(int addLimit);
    void logOutUser();
    void checkIfAlreadyVoted(String uid);
    void updateJokePoints(String uid);
    void updateJokeApproval(String uid);
    void writeVoteLogToDB(String uid);
    void getFacebookProfilePicture(OnGetProfilePictureListener listener) throws IOException;
    void getFacebookName(OnGetFacebookNameListener listener);
    void calculateTotalPoints(OnCalculatePointsListener listener, List<Joke> jokes);
    void updateRankPointsAndName(int points, String rankName, String rankId);


}
