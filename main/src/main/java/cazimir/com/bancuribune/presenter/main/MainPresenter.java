package cazimir.com.bancuribune.presenter.main;

import com.google.firebase.crash.FirebaseCrash;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.Date;
import java.util.List;

import cazimir.com.bancuribune.view.list.IMainActivityView;
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
import cazimir.com.bancuribune.callbacks.repository.OnAddRankFinishedListener;
import cazimir.com.bancuribune.callbacks.repository.OnAddUserListener;
import cazimir.com.bancuribune.callbacks.repository.OnShowReminderToAddListener;

public class MainPresenter implements IMainPresenter {

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
        if(mAuthPresenter.getCurrentUserID().equals(Constants.CAZIMIR) ||
                mAuthPresenter.getCurrentUserID().equals(Constants.ANA_MARIA)){
            return true;
        }
        return false;
    }

    @Override
    public void showAdminButtonsIfAdmin() {

        if(isAdmin()){
            mMainActivityView.showAdminButtons();
        }

//        mJokesRepository.checkIfAdmin(new OnAdminCheckCallback() {
//            @Override
//            public void onIsAdmin() {
//                mMainActivityView.showAdminButtons();
//            }
//        }, mAuthPresenter.getCurrentUserID());
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
    public void checkIfAlreadyVoted(Joke joke) {
        mJokesRepository.checkIfVoted(new OnCheckIfVotedFinishedListener() {
            @Override
            public void onHasVotedTrue() {
                mMainActivityView.showToast("Ai votat deja!");
            }

            @Override
            public void onHasVotedFalse(Joke joke) {
                increaseJokePointByOne(joke);
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
    public void increaseJokePointByOne(Joke joke) {
        mJokesRepository.updateJokePoints(new OnUpdatePointsFinishedListener() {
            @Override
            public void OnUpdatePointsFailed(String error) {
                mMainActivityView.showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
            }

            @Override
            public void OnUpdatePointsSuccess(Joke joke) {
                mMainActivityView.refreshAdapter(joke);
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
                mMainActivityView.playOnVotedAudio();
            }

            @Override
            public void onAddJokeVoteFailed(String databaseError) {
                mMainActivityView.showAlertDialog(databaseError, SweetAlertDialog.ERROR_TYPE);
            }
        }, vote);
    }

    @Override
    public void addRankToDatabase() {

        mJokesRepository.addRankToDB(new OnAddRankFinishedListener() {
            @Override
            public void onAddRankSuccess(Rank rank) {
                mMainActivityView.saveRankDataToSharedPreferences(rank);
                mMainActivityView.showAlertDialog("În momentul de față ai rangul de Hamsie. Poți adăuga 2 bancuri pe zi", Constants.LEVEL_UP);
            }

            @Override
            public void onAddRankFailed(String error) {
                mMainActivityView.showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
            }
        }, constructRankObject());
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
}
