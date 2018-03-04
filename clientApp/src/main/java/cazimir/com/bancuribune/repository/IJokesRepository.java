package cazimir.com.bancuribune.repository;


import java.util.Date;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.ui.add.OnAddJokeFinishedListener;
import cazimir.com.bancuribune.ui.list.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.ui.admin.OnGetAllPendingJokesListener;
import cazimir.com.bancuribune.ui.admin.OnJokeApprovedListener;
import cazimir.com.bancuribune.ui.likedJokes.OnGetLikedJokesListener;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnCheckIfVotedFinishedListener;
import cazimir.com.bancuribune.ui.list.OnGetJokesListener;
import cazimir.com.bancuribune.ui.list.OnUpdatePointsFinishedListener;
import cazimir.com.bancuribune.ui.myJokes.OnGetMyJokesListener;

public interface IJokesRepository {
    void getAllJokes(OnGetJokesListener listener, boolean reset);
    void getAllFilteredJokes(OnGetJokesListener listener, String text);
    void getAllPendingJokes(OnGetAllPendingJokesListener listener);
    void getMyJokes(OnGetMyJokesListener listener, String userId);
    void getVotesForUser(OnGetLikedJokesListener listener, String userId);
    void addRankToDB(OnAddRankFinishedListener listener, Rank rank);
    void addJoke(OnAddJokeFinishedListener listener, Joke joke);
    void getAllJokesAddedToday(OnAllowedToAddFinishedListener listener, String userId, int addLimit);
    void getAllJokesAddedOverThePastWeek(OnShowReminderToAddListener listener, String userId, Date lastCheckDate);
    void updateJokePoints(OnUpdatePointsFinishedListener listener, Joke joke);
    void setApprovedStatusToTrue(OnJokeApprovedListener listener, String uid);
    void updateRankPointsAndName(OnUpdateRankPointsSuccess listener, String rankName, int points, String userId);
    void writeJokeVote(OnAddJokeVoteFinishedListener listener, Vote vote);
    void checkIfVoted(OnCheckIfVotedFinishedListener listener, Joke joke, String userId);
    void checkIfRankDataInDB(OnCheckIfRankDataInDBListener listener, String userId);
    void addUserToDatabase(OnAddUserListener listener, String userId, String userName);
    void checkIfAdmin(OnAdminCheckFinishedListener listener, String userId);
}
