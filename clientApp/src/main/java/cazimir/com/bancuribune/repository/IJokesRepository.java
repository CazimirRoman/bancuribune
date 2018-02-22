package cazimir.com.bancuribune.repository;


import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.presenter.OnAddRankFinishedListener;
import cazimir.com.bancuribune.presenter.OnAddUserListener;
import cazimir.com.bancuribune.presenter.OnAdminCheckFinishedListener;
import cazimir.com.bancuribune.presenter.OnCheckIfRankDataInDBListener;
import cazimir.com.bancuribune.presenter.OnUpdateRankPointsSuccess;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.add.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.ui.admin.OnGetAllPendingJokesListener;
import cazimir.com.bancuribune.ui.admin.OnUpdateApproveStatusListener;
import cazimir.com.bancuribune.ui.likedJokes.OnGetLikedJokesListener;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnCheckIfVotedFinishedListener;
import cazimir.com.bancuribune.ui.list.OnGetJokesListener;
import cazimir.com.bancuribune.ui.list.OnUpdatePointsFinishedListener;
import cazimir.com.bancuribune.ui.list.OnUpdateVotedByFinishedListener;
import cazimir.com.bancuribune.ui.myjokes.OnFirebaseGetMyJokesListener;

public interface IJokesRepository {
    void getAllJokes(OnGetJokesListener listener, boolean reset);
    void getAllFilteredJokes(OnGetJokesListener listener, String text);
    void getAllPendingJokes(OnGetAllPendingJokesListener listener);
    void getMyJokes(OnFirebaseGetMyJokesListener listener, String userId);
    void getVotesForUser(OnGetLikedJokesListener listener, String userId);
    void addRankToDB(OnAddRankFinishedListener listener, Rank rank);
    void addJoke(OnAddFinishedListener listener, Joke joke);
    void getAllJokesAddedToday(OnAllowedToAddFinishedListener listener, String userId, int addLimit);
    void getAllJokesAddedThisWeek(OnShowReminderToAddListener listener, String userId);
    void updateJokePoints(OnUpdatePointsFinishedListener listener, Joke joke);
    void updateApproveStatus(OnUpdateApproveStatusListener listener, String uid);
    void updateVotedBy(OnUpdateVotedByFinishedListener listener, String uid, String userId);
    void updateRankPointsAndName(OnUpdateRankPointsSuccess listener, String rankName, int points, String userId);
    void writeJokeVote(OnAddJokeVoteFinishedListener listener, Vote vote);
    void checkIfVoted(OnCheckIfVotedFinishedListener listener, Joke joke, String userId);
    void checkIfRankDataInDB(OnCheckIfRankDataInDBListener listener, String userId);
    void addUserToDatabase(OnAddUserListener listener, String userId, String userName);
    void checkIfAdmin(OnAdminCheckFinishedListener listener, String userId);
}
