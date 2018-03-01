package cazimir.com.bancuribune.presenter;

import android.util.Log;

import com.facebook.Profile;
import com.google.firebase.crash.FirebaseCrash;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.repository.IJokesRepository;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.repository.OnShowReminderToAddListener;
import cazimir.com.bancuribune.ui.add.IAddJokeActivityView;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.add.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.ui.admin.IAdminActivityView;
import cazimir.com.bancuribune.ui.admin.OnGetAllPendingJokesListener;
import cazimir.com.bancuribune.ui.admin.OnUpdateApproveStatusListener;
import cazimir.com.bancuribune.ui.forgotPassword.IForgotPasswordActivityView;
import cazimir.com.bancuribune.ui.likedJokes.ILikedJokesActivityView;
import cazimir.com.bancuribune.ui.likedJokes.OnGetLikedJokesListener;
import cazimir.com.bancuribune.ui.list.IMainActivityView;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnCheckIfVotedFinishedListener;
import cazimir.com.bancuribune.ui.list.OnGetJokesListener;
import cazimir.com.bancuribune.ui.list.OnUpdatePointsFinishedListener;
import cazimir.com.bancuribune.ui.list.OnUpdateVotedByFinishedListener;
import cazimir.com.bancuribune.ui.login.ILoginActivityView;
import cazimir.com.bancuribune.ui.myjokes.IMyJokesActivityView;
import cazimir.com.bancuribune.ui.myjokes.OnCalculatePointsListener;
import cazimir.com.bancuribune.ui.myjokes.OnFirebaseGetMyJokesListener;
import cazimir.com.bancuribune.ui.myjokes.OnGetFacebookNameListener;
import cazimir.com.bancuribune.ui.register.IRegisterActivityView;

public class CommonPresenter implements ICommonPresenter, OnShowReminderToAddListener, OnGetLikedJokesListener, OnLoginWithEmailFinishedListener, OnResetPasswordListener, OnRegistrationFinishedListener, OnAdminCheckFinishedListener, OnGetJokesListener, OnAddUserListener, OnGetAllPendingJokesListener, OnAddRankFinishedListener, OnUpdateRankPointsSuccess, OnCheckIfRankDataInDBListener, OnUpdateApproveStatusListener, OnFirebaseGetMyJokesListener, OnAddFinishedListener, OnUpdatePointsFinishedListener, OnUpdateVotedByFinishedListener, OnCheckIfVotedFinishedListener, OnAddJokeVoteFinishedListener, OnAllowedToAddFinishedListener {

    private static final String TAG = "CommonPresenter";
    private IGeneralView view;
    private IJokesRepository repository;
    private IAuthPresenter authPresenter;

    public String getCurrentUserID() {
        return currentUserID;
    }

    private String currentUserID;

    public CommonPresenter(IGeneralView view) {
        this.view = view;
        this.authPresenter = new AuthPresenter(view);
        repository = new JokesRepository();
        setCurrentLoggedInUserId();
    }

    private void setCurrentLoggedInUserId() {
        currentUserID = authPresenter.getCurrentUserID();
    }

    @Override
    public void registerUser(String email, String password) {
        getRegisterActivityView().showProgress();
        authPresenter.registerUser(this, email, password);
    }

    public void getAllJokesData(boolean reset, boolean shouldShowProgress) {

        if (reset) {
            getMainActivityView().refreshJokesListAdapter();
        }

        if (!shouldShowProgress) {
            getMainActivityView().showProgressBar();
        }

        repository.getAllJokes(this, reset);
    }

    @Override
    public void getAllPendingJokesData() {
        repository.getAllPendingJokes(this);
    }

    @Override
    public void getFilteredJokesData(String text) {
        repository.getAllFilteredJokes(this, text);
    }

    @Override
    public void getMyJokes() {
        repository.getMyJokes(this, currentUserID);
    }

    @Override
    public void getLikedJokes() {
        repository.getVotesForUser(this, currentUserID);
    }

    @Override
    public void addJoke(Joke joke, Boolean isAdmin) {
        joke.setCreatedBy(currentUserID);
        joke.setUserName(authPresenter.getCurrentUserName());

        if (isAdmin) {
            joke.setApproved(true);
        }
        repository.addJoke(this, joke);
    }

    @Override
    public void addRankToDatabase() {
        Rank rank = new Rank();
        rank.setUserId(currentUserID);
        rank.setUserName(authPresenter.getCurrentUserName());
        rank.setRank(Constants.HAMSIE);
        rank.setTotalPoints(0);
        repository.addRankToDB(this, rank);
    }

    @Override
    public void checkIfAdmin() {
        repository.checkIfAdmin(this, currentUserID);
    }

    @Override
    public void checkNumberOfAdds(int addLimit) {
        repository.getAllJokesAddedToday(this, currentUserID, addLimit);
    }

    @Override
    public void checkAndGetMyRank() {
        repository.checkIfRankDataInDB(this, currentUserID);
    }

    @Override
    public void isAllowedToAdd(int remainingAdds) {
        getMainActivityView().updateRemainingAdds(remainingAdds);
        getMainActivityView().navigateToAddJokeActivity();
    }

    @Override
    public void isNotAllowedToAdd(int addLimit) {
        getMainActivityView().isNotAllowedToAdd(addLimit);
    }

    @Override
    public void showAddReminderToUser() {
        getMainActivityView().showAlertDialog("Se pare ca nu ai adaugat nici un banc saptamana asta.", SweetAlertDialog.WARNING_TYPE);
        getMainActivityView().addLastCheckDateToSharedPreferences();

    }

    @Override
    public void logOutUser() {
        authPresenter.logUserOut(getMyJokesActivityView());
    }

    @Override
    public void checkIfAlreadyVoted(Joke joke) {
        repository.checkIfVoted(this, joke, currentUserID);
    }

    @Override
    public void increaseJokePointByOne(Joke joke) {
        repository.updateJokePoints(this, joke);
        writeVoteLogToDB(joke.getUid());
    }

    @Override
    public void updateJokeApproval(String uid) {
        repository.updateApproveStatus(this, uid);
    }

    @Override
    public void writeVoteLogToDB(String uid) {
        Vote vote = new Vote();
        vote.setVotedBy(currentUserID);
        vote.setJokeId(uid);
        repository.writeJokeVote(this, vote);
    }

    @Override
    public void getFacebookProfilePicture(final OnGetProfilePictureListener listener) throws IOException {
        if (isLoggedInFacebook()) {
            String id = Profile.getCurrentProfile().getId();
            final URL imageURL = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
            listener.OnGetProfilePictureSuccess(imageURL);
        } else {
            listener.OnGetProfilePictureFailed();
        }
    }

    @Override
    public void getProfileName(OnGetFacebookNameListener listener) {

        if (isLoggedInFacebook()) {
            String name = Profile.getCurrentProfile().getName();
            if (name != null) {
                listener.OnGetFacebookNameSuccess(name);
            } else {
                listener.OnGetFacebookNameFailed();
            }
        } else {
            listener.OnGetFacebookNameSuccess(authPresenter.getCurrentUserName());
        }
    }

    private boolean isLoggedInFacebook() {
        if (Profile.getCurrentProfile() != null) {
            return true;
        }

        return false;
    }

    @Override
    public void calculateTotalPoints(OnCalculatePointsListener listener, List<Joke> jokes) {
        int points = 0;
        for (Joke joke : jokes) {
            points = points + joke.getPoints();
        }

        listener.OnCalculateSuccess(points);
    }

    @Override
    public void updateRankPointsAndName(int points, String rankName, String rankId) {
        repository.updateRankPointsAndName(this, rankName, points, rankId);
    }

    @Override
    public IAuthPresenter getAuthPresenter() {
        return authPresenter;
    }

    @Override
    public void checkNumberOfAddsLastWeek(Date lastCheckDate) {
        repository.getAllJokesAddedOverThePastWeek(this, currentUserID, lastCheckDate);
    }

    @Override
    public void OnGetJokesSuccess(List<Joke> jokes) {
        getMainActivityView().displayJokes(jokes);
        getMainActivityView().hideProgressBar();
        getMainActivityView().hideSwipeRefresh();
    }

    @Override
    public void OnGetJokesFailed(String error) {
        getMainActivityView().requestFailed(error);
        getMainActivityView().hideProgressBar();
        getMainActivityView().hideSwipeRefresh();
    }

    @Override
    public void OnEndOfListReached() {
        getMainActivityView().hideProgressBar();
        getMainActivityView().hideSwipeRefresh();
    }

    @Override
    public void OnAddSuccess() {
        getAddJokeActivityView().closeAdd();
    }

    @Override
    public void OnAddFailed() {
        getMainActivityView().showAddFailedDialog();
    }

    @Override
    public void onGetMyJokesSuccess(List<Joke> jokes) {
        getMyJokesActivityView().showMyJokesList(jokes);
    }

    @Override
    public void onGetMyJokesError(String error) {
        getMyJokesActivityView().showToast(error);
    }

    @Override
    public void onAddJokeVoteSuccess() {
        getMainActivityView().showToast("Multumim pentru vot!");
    }

    @Override
    public void onAddJokeVoteFailed() {
        getMainActivityView().showToast("Data could not be saved!");
    }

    @Override
    public void OnUpdatePointsFailed(String error) {
        getMainActivityView().showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
    }

    @Override
    public void OnUpdatePointsSuccess(Joke joke) {
        getMainActivityView().refreshAdapter(joke);
    }

    @Override
    public void OnHasVotedTrue() {
        getMainActivityView().showToast("Ai votat deja!");
    }

    @Override
    public void OnHasVotedFalse(Joke joke) {
        increaseJokePointByOne(joke);
    }

    @Override
    public void OnUpdateVotedByFailed(String error) {
        getMainActivityView().showToast(error);
    }

    @Override
    public void OnUpdateVotedBySuccess() {

    }

    @Override
    public void OnGetAllPendingJokesSuccess(List<Joke> jokes) {
        getAdminActivityView().refreshJokes(jokes);
    }

    @Override
    public void OnGetAllPendingJokesFailed(String message) {
        getAdminActivityView().showToast(message);
    }

    @Override
    public void OnUpdateApproveStatusSuccess() {
        getAdminActivityView().showToast("Approved!");
        getAdminActivityView().getAllPendingJokes();
    }

    @Override
    public void OnUpdateApproveStatusFailed(String error) {
        getAdminActivityView().showToast(error);
    }

    @Override
    public void RankDataIsInDB(Rank rank) {
        getMainActivityView().checkIfNewRank(rank.getRank());
        getMainActivityView().saveRankDataToSharedPreferences(rank);
        getMainActivityView().checkIfAdmin();
    }

    @Override
    public void RankDataNotInDB() {
        addRankToDatabase();
        addUserToDatabase(currentUserID, authPresenter.getCurrentUserName());
    }

    private void addUserToDatabase(String currentUserID, String userName) {
        repository.addUserToDatabase(this, currentUserID, userName);
    }

    @Override
    public void OnUpdateRankPointsSuccess() {
        Log.d(TAG, "Rank points updated");
    }

    @Override
    public void OnAddRankSuccess(Rank rank) {
        getMainActivityView().saveRankDataToSharedPreferences(rank);
        getMainActivityView().showAlertDialog("In momentul de fata ai rangul de hamsie. Poti adauga 2 bancuri pe zi", SweetAlertDialog.WARNING_TYPE);
    }

    @Override
    public void OnAddRankFailure(String error) {
        getMainActivityView().showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
    }

    @Override
    public void OnAddUserFailed(String message) {
        FirebaseCrash.log(message);
    }

    @Override
    public void OnAddUserSuccess() {
        FirebaseCrash.log("User added successfully!");
    }

    @Override
    public void OnAdminCheckTrue() {
        getMainActivityView().showAdminButton();
        getMainActivityView().setAdmin(true);
    }

    @Override
    public void OnAdminCheckFalse() {
        getMainActivityView().setAdmin(false);
    }

    @Override
    public void onRegistrationSuccess(String message) {
        getRegisterActivityView().showToast(message);
        getRegisterActivityView().hideProgress();
        getRegisterActivityView().redirectToLogin();
    }

    @Override
    public void onRegistrationFailed(String error) {
        getRegisterActivityView().showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
        getRegisterActivityView().hideProgress();
    }

    @Override
    public void sendResetInstructions(String email) {
        getForgotPasswordActivityView().showProgress();
        authPresenter.performPasswordReset(this, email);
    }

    @Override
    public void performLogin(String email, String password) {
        authPresenter.login(this, email, password);
    }

    @Override
    public void onLoginWithEmailSuccess() {
        ILoginActivityView login = (ILoginActivityView) this.view.getInstance();
        login.launchMainActivity();
        login.hideProgress();
    }

    @Override
    public void onLoginWithEmailFailed(String error) {
        getLoginActivityView().showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
        getLoginActivityView().hideProgress();
    }

    @Override
    public void onResetPasswordSuccess(String message) {
        getForgotPasswordActivityView().showToast(message);
        getForgotPasswordActivityView().redirectToLogin();
        getForgotPasswordActivityView().hideProgress();
    }

    @Override
    public void onResetPasswordFailed(String error) {
        getForgotPasswordActivityView().showToast(error);
    }

    @Override
    public void onGetLikedJokesSuccess(Joke joke) {
        getLikedJokesActivityView().addToLikedJokeList(joke);
    }

    @Override
    public void onGetLikedJokesFailed(String error) {

    }

    //get view

    private ILoginActivityView getLoginActivityView() {
        return (ILoginActivityView) this.view.getInstance();
    }

    private IRegisterActivityView getRegisterActivityView() {
        return (IRegisterActivityView) this.view.getInstance();
    }

    private IForgotPasswordActivityView getForgotPasswordActivityView() {
        return (IForgotPasswordActivityView) this.view.getInstance();
    }

    private IMainActivityView getMainActivityView() {
        return (IMainActivityView) this.view.getInstance();
    }

    private IAddJokeActivityView getAddJokeActivityView() {
        return (IAddJokeActivityView) this.view.getInstance();
    }

    private IMyJokesActivityView getMyJokesActivityView() {
        return (IMyJokesActivityView) this.view.getInstance();
    }

    private ILikedJokesActivityView getLikedJokesActivityView() {
        return (ILikedJokesActivityView) this.view.getInstance();
    }

    private IAdminActivityView getAdminActivityView() {
        return (IAdminActivityView) this.view.getInstance();
    }
}