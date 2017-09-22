package cazimir.com.bancuribune.presenter;

import cazimir.com.bancuribune.model.Joke;

public interface ICommonPresenter {
    void getAllJokesData();
    void getFilteredJokesData(String text);
    void getMyJokes();
    void addJoke(Joke joke);
    void checkNumberOfAdds();
    void isAllowedToAdd();
    void isNotAllowedToAdd();
    void logOutUser();
    void checkIfAlreadyVoted(String uid);
    void updateJokePoints(String uid);
    void writeVoteLogToDB(String uid);


}
