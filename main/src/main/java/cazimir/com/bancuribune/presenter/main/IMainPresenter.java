package cazimir.com.bancuribune.presenter.main;

import java.util.Date;

import cazimir.com.bancuribune.model.Joke;

public interface IMainPresenter {
    boolean isAdmin();
    void showAdminButtonsIfAdmin();
    void checkNumberOfAddsLastWeek(Date lastCheckDate);
    void checkIfAlreadyVoted(Joke joke, int position);
    void getAllJokesData(boolean reset, boolean shouldShowProgress);
    void checkNumberOfAdds(int addLimit);
    void increaseJokePointByOne(Joke joke, int position);
    void writeVoteLogToDB(String uid);
    void checkAndAddRankToDB();
    void addUserToDatabase(String currentUserID, String userName);
}
