package cazimir.com.interfaces.repository;

import java.util.Date;

import cazimir.com.interfaces.reporting.OnGetTotalNumberOfJokesCompleted;
import cazimir.com.interfaces.reporting.OnGetUsersWithMostPointsCompleted;
import cazimir.com.interfaces.ui.admin.OnGetAllPendingJokesListener;
import cazimir.com.interfaces.ui.admin.OnJokeApprovedListener;
import cazimir.com.interfaces.ui.likedJokes.OnGetLikedJokesListener;
import cazimir.com.interfaces.ui.list.OnAddJokeVoteFinishedListener;
import cazimir.com.interfaces.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.interfaces.ui.list.OnCheckIfVotedFinishedListener;
import cazimir.com.interfaces.ui.list.OnGetJokesListener;
import cazimir.com.interfaces.ui.list.OnUpdatePointsFinishedListener;
import cazimir.com.interfaces.ui.myJokes.OnGetMyJokesListener;
import cazimir.com.models.Joke;
import cazimir.com.models.Rank;
import cazimir.com.models.Vote;

public interface IJokesRepository {
    void getAllJokes(OnGetJokesListener listener, boolean reset);
    void getTotalNumberOfJokes(OnGetTotalNumberOfJokesCompleted listener);
    void getAllPendingJokes(OnGetAllPendingJokesListener listener);
    void getMyJokes(OnGetMyJokesListener listener, String userId);
    void getVotesForUser(OnGetLikedJokesListener listener, String userId);
    void addRankToDB(OnAddRankFinishedListener listener, Rank rank);
    void addJoke(cazimir.com.interfaces.ui.add.OnAddJokeFinishedListener listener, Joke joke);
    void getAllJokesAddedToday(OnAllowedToAddFinishedListener listener, String userId, int addLimit);
    void getAllJokesAddedOverThePastWeek(OnShowReminderToAddListener listener, String userId, Date lastCheckDate);
    void updateJokePoints(OnUpdatePointsFinishedListener listener, Joke joke);
    void setApprovedStatusToTrue(OnJokeApprovedListener listener, String uid);
    void updateRankPointsAndName(OnUpdateRankPointsSuccess listener, String rankName, int points, String userId);
    void writeJokeVote(OnAddJokeVoteFinishedListener listener, Vote vote);
    void checkIfVoted(OnCheckIfVotedFinishedListener listener, Joke joke, String userId);
    void checkIfRankDataInDB(OnCheckIfRankDataInDBListener listener, String userId);
    void addUserToDatabase(OnAddUserListener listener, String userId, String userName);
    void checkIfAdmin(OnAdminCheckCallback listener, String userId);
    void getUsersWithMostPoints(OnGetUsersWithMostPointsCompleted listener);
}
