package cazimir.com.bancuribune.repository;


import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.add.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnCheckIfVotedFinishedListener;
import cazimir.com.bancuribune.ui.list.OnFirebaseGetAllJokesListener;
import cazimir.com.bancuribune.ui.list.OnUpdatePointsFinishedListener;
import cazimir.com.bancuribune.ui.myjokes.OnFirebaseGetMyJokesListener;

public interface IJokesRepository {
    void getAllJokes(OnFirebaseGetAllJokesListener listener);
    void getMyJokes(OnFirebaseGetMyJokesListener listener, String userID);
    void addJoke(OnAddFinishedListener listener, Joke joke);
    void getAllJokesAddedToday(OnAllowedToAddFinishedListener listener, String userID);
    void updateJokePoints(OnUpdatePointsFinishedListener listener, String uid);
    void writeJokeVote(OnAddJokeVoteFinishedListener listener, Vote vote);
    void checkIfVoted(OnCheckIfVotedFinishedListener listener, String uid, String userId);
}
