package cazimir.com.bancuribune.presenter.main;

import java.util.Date;

import cazimir.com.models.Joke;

/**
 * TODO: Add a class header comment!
 */
public interface IMainPresenter {
    boolean isAdmin();
    void showAdminButtonsIfAdmin();
    void checkNumberOfAddsLastWeek(Date lastCheckDate);
    void checkIfAlreadyVoted(Joke joke);
    void getAllJokesData(boolean reset, boolean shouldShowProgress);
    void checkNumberOfAdds(int addLimit);
    void increaseJokePointByOne(Joke joke);
    void writeVoteLogToDB(String uid);
    void addRankToDatabase();
    void addUserToDatabase(String currentUserID, String userName);
}
