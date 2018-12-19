package cazimir.com.bancuribune.repository;

import java.util.Date;

import cazimir.com.bancuribune.callbacks.admin.OnGetAllPendingJokesListener;
import cazimir.com.bancuribune.callbacks.admin.OnJokeApprovedListener;
import cazimir.com.bancuribune.callbacks.likedJokes.OnDeleteJokeVoteCallback;
import cazimir.com.bancuribune.callbacks.likedJokes.OnGetLikedJokesListener;
import cazimir.com.bancuribune.callbacks.list.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.callbacks.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.callbacks.list.OnCheckIfVotedFinishedListener;
import cazimir.com.bancuribune.callbacks.list.OnGetJokesListener;
import cazimir.com.bancuribune.callbacks.list.OnUpdatePointsFinishedListener;
import cazimir.com.bancuribune.callbacks.myJokes.OnGetMyJokesListener;
import cazimir.com.bancuribune.callbacks.reporting.OnGetTotalNumberOfJokesCompleted;
import cazimir.com.bancuribune.callbacks.reporting.OnGetUsersWithMostPointsCompleted;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.report.OnGetAllRanksListener;

public interface IJokesRepository {
    void getAllJokes(OnGetJokesListener listener, boolean reset);
    void getTotalNumberOfJokes(OnGetTotalNumberOfJokesCompleted listener);
    void getAllPendingJokes(OnGetAllPendingJokesListener listener);
    void getMyJokes(OnGetMyJokesListener listener, String userId, boolean getNewest);
    void getVotesForUser(OnGetLikedJokesListener listener, String userId);
    void addRankToDB(OnAddRankFinishedListener listener, Rank rank);
    void addJoke(cazimir.com.bancuribune.callbacks.add.OnAddJokeFinishedListener listener, Joke joke);
    void getAllJokesAddedToday(OnAllowedToAddFinishedListener listener, String userId, int addLimit);
    void getAllJokesAddedOverThePastWeek(OnShowReminderToAddListener listener, String userId, Date lastCheckDate);
    void updateJokePoints(OnUpdatePointsFinishedListener listener, Joke joke);
    void approveJoke(OnJokeApprovedListener listener, String uid, String text);
    void updateRankPointsAndName(OnUpdateRankPointsSuccess listener, String rankName, int points, String userId);
    void writeJokeVote(OnAddJokeVoteFinishedListener listener, Vote vote);
    void checkIfVoted(OnCheckIfVotedFinishedListener listener, Joke joke, String userId);
    void checkIfRankDataInDB(OnCheckIfRankDataInDBListener listener, String userId);
    void addUserToDatabase(OnAddUserListener listener, String userId, String userName);
    void checkIfAdmin(OnAdminCheckCallback listener, String userId);
    void getUsersWithMostPoints(OnGetUsersWithMostPointsCompleted listener);
    void deleteJokeVote(OnDeleteJokeVoteCallback onDeleteJokeVoteCallback, Joke mJokeToBeRemoved, String userId);
    void getAllRanks(OnGetAllRanksListener onGetAllRanksListener);
}
