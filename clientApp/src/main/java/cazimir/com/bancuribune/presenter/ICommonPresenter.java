package cazimir.com.bancuribune.presenter;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.list.OnCheckIfAdminListener;

public interface ICommonPresenter {
    void getAllJokesData();
    void getFilteredJokesData(String text);
    void getAllPendingJokesData();
    void getMyJokes();
    void addJoke(Joke joke);
    void checkIfAdmin(OnCheckIfAdminListener listener);
    void checkNumberOfAdds();
    void isAllowedToAdd();
    void isNotAllowedToAdd();
    void logOutUser();
    void checkIfAlreadyVoted(String uid);
    void updateJokePoints(String uid);
    void updateJokeApproval(String uid);
    void writeVoteLogToDB(String uid);


}
