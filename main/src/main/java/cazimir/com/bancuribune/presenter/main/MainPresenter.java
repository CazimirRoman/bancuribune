package cazimir.com.bancuribune.presenter.main;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cazimir.com.bancuribune.callbacks.admin.OnJokeApprovedListener;
import cazimir.com.bancuribune.callbacks.list.IMainActivityView;
import cazimir.com.bancuribune.callbacks.list.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.callbacks.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.callbacks.list.OnCheckIfVotedFinishedListener;
import cazimir.com.bancuribune.callbacks.list.OnGetJokesListener;
import cazimir.com.bancuribune.callbacks.list.OnUpdatePointsFinishedListener;
import cazimir.com.bancuribune.constant.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.presenter.auth.IAuthPresenter;
import cazimir.com.bancuribune.repository.IJokesRepository;
import cazimir.com.bancuribune.repository.OnAddRankFinishedListener;
import cazimir.com.bancuribune.repository.OnAddUserListener;
import cazimir.com.bancuribune.repository.OnCheckIfRankDataInDBListener;
import cazimir.com.bancuribune.repository.OnShowReminderToAddListener;

import static cazimir.com.bancuribune.constant.Constants.EVENT_VOTED;

public class MainPresenter implements IMainPresenter {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private IMainActivityView mMainActivityView;
    private IAuthPresenter mAuthPresenter;
    private IJokesRepository mJokesRepository;

    public MainPresenter(IMainActivityView view, IAuthPresenter authPresenter, IJokesRepository jokesRepository) {
        mMainActivityView = view;
        mAuthPresenter = authPresenter;
        mJokesRepository = jokesRepository;
    }

    public IAuthPresenter getAuthPresenter() {
        return mAuthPresenter;
    }

    @Override
    public boolean isAdmin() {
        List<String> list = Arrays.asList(Constants.ADMINS);
        return list.contains(mAuthPresenter.getCurrentUserID());
    }

    @Override
    public void showAdminButtonsIfAdmin() {

        if(isAdmin()){
            mMainActivityView.showAdminButtons();
        }
    }

    @Override
    public void approveJoke(String uid, String jokeText) {
        mJokesRepository.approveJoke(new OnJokeApprovedListener() {
            @Override
            public void onJokeApprovedSuccess(String message) {
                mMainActivityView.showToast(message);
                mMainActivityView.getAllJokesData(true, false);
                mMainActivityView.hideKeyboard();
            }

            @Override
            public void onJokeApprovedFailed(String error) {
                mMainActivityView.showToast(error);
            }
        }, uid, jokeText);
    }

    @Override
    public void checkNumberOfAddsLastWeek(Date lastCheckDate) {
        mJokesRepository.getAllJokesAddedOverThePastWeek(new OnShowReminderToAddListener() {
            @Override
            public void showAddReminderToUser() {
                mMainActivityView.showAlertDialog("Știi bancuri amuzante? Adaugă-le acum în aplicație și câștigă like-urile celorlalți useri", SweetAlertDialog.SUCCESS_TYPE);
                mMainActivityView.addLastCheckDateToSharedPreferences();
            }
        }, mAuthPresenter.getCurrentUserID(), lastCheckDate);
    }

    @Override
    public void checkIfAlreadyVoted(Joke joke, final int position) {
        mJokesRepository.checkIfVoted(new OnCheckIfVotedFinishedListener() {
            @Override
            public void onHasVotedTrue() {
                mMainActivityView.showToast("Ai votat deja!");
                mMainActivityView.enableHeartIcon(position);
            }

            @Override
            public void onHasVotedFalse(Joke joke) {
                mMainActivityView.logEvent(EVENT_VOTED);
                increaseJokePointByOne(joke, position);
            }
        }, joke, mAuthPresenter.getCurrentUserID());
    }

    @Override
    public void checkNumberOfAdds(int addLimit) {

        mJokesRepository.getAllJokesAddedToday(new OnAllowedToAddFinishedListener() {
            @Override
            public void isAllowedToAdd(int remainingAdds) {
                mMainActivityView.updateRemainingAdds(remainingAdds);
                mMainActivityView.navigateToAddJokeActivity();
            }

            @Override
            public void isNotAllowedToAdd(int addLimit) {
                mMainActivityView.isNotAllowedToAdd(addLimit);
            }
        }, mAuthPresenter.getCurrentUserID(), addLimit);
    }

    @Override
    public void increaseJokePointByOne(Joke joke, final int position) {
        mJokesRepository.updateJokePoints(new OnUpdatePointsFinishedListener() {
            @Override
            public void OnUpdatePointsFailed(String error) {
                mMainActivityView.showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
                mMainActivityView.enableHeartIcon(position);
            }

            @Override
            public void OnUpdatePointsSuccess(Joke joke) {
                mMainActivityView.refreshAdapter(joke);
                mMainActivityView.enableHeartIcon(position);
            }
        }, joke);

        writeVoteLogToDB(joke.getUid());
    }

    @Override
    public void writeVoteLogToDB(String uid) {
        Vote vote = new Vote();
        vote.setVotedBy(mAuthPresenter.getCurrentUserID());
        vote.setJokeId(uid);
        mJokesRepository.writeJokeVote(new OnAddJokeVoteFinishedListener() {
            @Override
            public void onAddJokeVoteSuccess() {
                mMainActivityView.writeToLog("Vote successfully written to DB");
            }

            @Override
            public void onAddJokeVoteFailed(String databaseError) {
                mMainActivityView.showAlertDialog(databaseError, SweetAlertDialog.ERROR_TYPE);
            }
        }, vote);
    }

    @Override
    public void checkAndAddRankToDB() {

        mJokesRepository.checkIfRankDataInDB(new OnCheckIfRankDataInDBListener() {
            @Override
            public void rankDataIsInDB(Rank rank) {
                Log.d(TAG, "Rank already in DB");
                mMainActivityView.saveRankDataToSharedPreferences(rank);
            }

            @Override
            public void rankDataNotInDB() {
                mJokesRepository.addRankToDB(new OnAddRankFinishedListener() {
                    @Override
                    public void onAddRankSuccess(Rank rank) {
                        mMainActivityView.addUserToDatabase();
                        mMainActivityView.saveRankDataToSharedPreferences(rank);
                        //replace this behaviour with a trigger on the server when a new rank is added.
                        //mMainActivityView.showAlertDialog("În momentul de față ai rangul de Hamsie. Poți adăuga 2 bancuri pe zi", Constants.LEVEL_UP);
                    }

                    @Override
                    public void onAddRankFailed(String error) {
                        mMainActivityView.showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
                    }
                }, constructRankObject());
            }
        }, mAuthPresenter.getCurrentUserID());
    }

    private Rank constructRankObject() {
        Rank rank = new Rank();
        rank.setUserId(mAuthPresenter.getCurrentUserID());
        rank.setUserName(mAuthPresenter.getCurrentUserName());
        rank.setRank(Constants.HAMSIE);
        rank.setTotalPoints(0);
        return rank;
    }

    @Override
    public void getAllJokesData(boolean reset, boolean shouldShowProgress) {

        if (reset) {
            mMainActivityView.refreshJokesListAdapter();
        }

        if (!shouldShowProgress) {
            mMainActivityView.showProgressBar();
        }

        mJokesRepository.getAllJokes(new OnGetJokesListener() {
            @Override
            public void onGetJokesSuccess(List<Joke> jokes) {
                mMainActivityView.displayJokes(jokes);
                mMainActivityView.hideProgressBar();
                mMainActivityView.hideSwipeRefresh();
            }

            @Override
            public void onGetJokesFailed(String error) {
                mMainActivityView.requestFailed(error);
                mMainActivityView.hideProgressBar();
                mMainActivityView.hideSwipeRefresh();
            }

            @Override
            public void onEndOfListReached() {
                mMainActivityView.hideProgressBar();
                mMainActivityView.hideSwipeRefresh();
            }
        }, reset);
    }

    @Override
    public void addUserToDatabase(String currentUserID, String userName) {
        mJokesRepository.addUserToDatabase(new OnAddUserListener() {
            @Override
            public void onAddUserFailed(String message) {
                FirebaseCrash.log(message);
            }

            @Override
            public void onAddUserSuccess() {
                FirebaseCrash.log("User added successfully!");
            }
        }, currentUserID, userName);
    }

    @Override
    public boolean isLoggedInAnonymously() {
        return mAuthPresenter.isLoggedInAnonymously();
    }
}
