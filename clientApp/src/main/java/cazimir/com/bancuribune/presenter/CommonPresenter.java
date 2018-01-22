package cazimir.com.bancuribune.presenter;

import com.facebook.Profile;
import com.google.firebase.crash.FirebaseCrash;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.repository.IJokesRepository;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.ui.add.IAddJokeActivityView;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.add.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.ui.admin.IAdminActivityView;
import cazimir.com.bancuribune.ui.admin.OnGetAllPendingJokesListener;
import cazimir.com.bancuribune.ui.admin.OnUpdateApproveStatusListener;
import cazimir.com.bancuribune.ui.forgotPassword.IForgotPasswordActivityView;
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

public class CommonPresenter implements ICommonPresenter, OnLoginWithEmailFinishedListener, OnResetPasswordListener, OnRegistrationFinishedListener, OnAdminCheckFinishedListener, OnGetJokesListener, OnAddUserListener, OnGetAllPendingJokesListener, OnAddRankFinishedListener, OnUpdateRankPointsSuccess, OnCheckIfRankDataInDBListener, OnUpdateApproveStatusListener, OnFirebaseGetMyJokesListener, OnAddFinishedListener, OnUpdatePointsFinishedListener, OnUpdateVotedByFinishedListener, OnCheckIfVotedFinishedListener, OnAddJokeVoteFinishedListener, OnAllowedToAddFinishedListener {

    private ILoginActivityView loginView;
    private IRegisterActivityView registerView;
    private IForgotPasswordActivityView forgotPasswordView;
    private IMainActivityView mainView;
    private IAddJokeActivityView addView;
    private IMyJokesActivityView myJokesView;
    private IAdminActivityView adminView;
    private IJokesRepository repository;
    private IAuthPresenter authPresenter;
    private String currentUserID;

    public CommonPresenter(IRegisterActivityView view) {
        this.registerView = view;
        this.authPresenter = new AuthPresenter(view);
        setCurrentLoggedInUserId();
    }

    public CommonPresenter(IMainActivityView view) {
        this.mainView = view;
        this.repository = new JokesRepository();
        this.authPresenter = new AuthPresenter(view);
        setCurrentLoggedInUserId();
    }

    public CommonPresenter(IAddJokeActivityView view) {
        this.addView = view;
        this.repository = new JokesRepository();
        this.authPresenter = new AuthPresenter(view);
        setCurrentLoggedInUserId();
    }

    public CommonPresenter(IMyJokesActivityView view, IJokesRepository repository) {
        this.myJokesView = view;
        this.repository = repository;
        this.authPresenter = new AuthPresenter(myJokesView);
        setCurrentLoggedInUserId();
    }

    public CommonPresenter(IAdminActivityView view, IJokesRepository repository) {
        this.adminView = view;
        this.repository = repository;
        this.authPresenter = new AuthPresenter(adminView);
        setCurrentLoggedInUserId();
    }

    public CommonPresenter(ILoginActivityView view) {
        this.loginView = view;
        this.authPresenter = new AuthPresenter(view);
    }

    public CommonPresenter(IForgotPasswordActivityView view) {
        this.forgotPasswordView = view;
        this.authPresenter = new AuthPresenter(view);
    }

    private void setCurrentLoggedInUserId() {
        currentUserID = authPresenter.getCurrentUserID();
    }

    @Override
    public void registerUser(String email, String password) {
        registerView.showProgress();
        authPresenter.registerUser(this, email, password);
    }

    public void getAllJokesData(boolean reset, boolean shouldShowProgress){
        if(reset){
            mainView.refreshJokesListAdapter();
        }

        if(!shouldShowProgress){
            mainView.showProgressBar();
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
        mainView.updateRemainingAdds(remainingAdds);
        mainView.navigateToAddJokeActivity();
    }

    @Override
    public void isNotAllowedToAdd(int addLimit) {
        mainView.isNotAllowedToAdd(addLimit);
    }

    @Override
    public void logOutUser() {
        authPresenter.logUserOut(mainView);
    }

    @Override
    public void checkIfAlreadyVoted(Joke joke) {
        repository.checkIfVoted(this, joke, currentUserID);
    }

    @Override
    public void increaseJokePointByOne(Joke joke) {
        repository.updateJokePoints(this, joke);
        //repository.updateVotedBy(this, uid, currentUserID);
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
        if(loggedInViaFacebook()){
            String id = Profile.getCurrentProfile().getId();
            final URL imageURL = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
            listener.OnGetProfilePictureSuccess(imageURL);
        }else{
            listener.OnGetProfilePictureFailed();
        }


    }

    @Override
    public void getProfileName(OnGetFacebookNameListener listener) {

        if(loggedInViaFacebook()){
            String name = Profile.getCurrentProfile().getName();
            if(name != null){
                listener.OnGetFacebookNameSuccess(name);
            }else{
                listener.OnGetFacebookNameFailed();
            }
        }else{
            listener.OnGetFacebookNameSuccess(authPresenter.getCurrentUserName());
        }
    }

    private boolean loggedInViaFacebook() {
        if(Profile.getCurrentProfile() != null){
            return true;
        }

        return false;
    }

    @Override
    public void calculateTotalPoints(OnCalculatePointsListener listener, List<Joke> jokes) {
        int points = 0;
        for (Joke joke : jokes){
            points = points + joke.getPoints();
        }

        listener.OnCalculateSuccess(points);
    }

    @Override
    public void updateRankPointsAndName(int points, String rankName, String rankId) {
        repository.updateRankPointsAndName(this, rankName, points, rankId);
    }


    @Override
    public void OnGetJokesSuccess(List<Joke> jokes) {
        mainView.displayJokes(jokes);
        mainView.hideProgressBar();
        mainView.hideSwipeRefresh();
    }

    @Override
    public void OnGetJokesFailed(String error) {
        mainView.requestFailed(error);
        mainView.hideProgressBar();
        mainView.hideSwipeRefresh();
    }

    @Override
    public void OnEndOfListReached() {
        mainView.hideProgressBar();
        mainView.hideSwipeRefresh();
    }

    @Override
    public void OnAddSuccess() {
        addView.closeAdd();
    }

    @Override
    public void OnAddFailed() {
        mainView.showAddFailedDialog();
    }

    @Override
    public void onGetMyJokesSuccess(List<Joke> jokes) {
        myJokesView.showMyJokesList(jokes);
    }

    @Override
    public void onGetMyJokesError(String error) {

    }

    @Override
    public void onAddJokeVoteSuccess() {
        mainView.showTestToast("Multumim pentru vot!");
    }

    @Override
    public void onAddJokeVoteFailed() {
        mainView.showTestToast("Data could not be saved!");
    }

    @Override
    public void OnUpdatePointsFailed(String error) {
        mainView.showAlertDialog(error);
    }

    @Override
    public void OnUpdatePointsSuccess(Joke joke) {
        mainView.refreshAdapter(joke);
    }

    @Override
    public void OnHasVotedTrue() {
//        mainView.changeColour();
//        mainView.decreasePoints();
        mainView.showTestToast("Ai votat deja!");
    }

    @Override
    public void OnHasVotedFalse(Joke joke) {
        increaseJokePointByOne(joke);
    }

    @Override
    public void OnUpdateVotedByFailed(String error) {
        mainView.showTestToast(error);
    }

    @Override
    public void OnUpdateVotedBySuccess() {

    }

    @Override
    public void OnGetAllPendingJokesSuccess(List<Joke> jokes) {
        adminView.refreshJokes(jokes);
    }

    @Override
    public void OnGetAllPendingJokesFailed(String message) {


    }

    @Override
    public void OnUpdateApproveStatusSuccess() {
        adminView.showToast("Approved!");
        adminView.getAllPendingJokes();
    }

    @Override
    public void OnUpdateApproveStatusFailed(String error) {
        adminView.showToast(error);
    }

    @Override
    public void RankDataIsInDB(Rank rank) {
        mainView.saveRankDataToSharedPreferences(rank);
        mainView.updateCurrentRank(rank.getRank());
        mainView.checkIfAdmin();
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
        //ok:)
    }

    @Override
    public void OnAddRankSuccess(Rank rank) {
        mainView.saveRankDataToSharedPreferences(rank);
        mainView.updateCurrentRank(rank.getRank());
    }

    @Override
    public void OnAddRankFailure(String error) {
        mainView.showAlertDialog(error);
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
        mainView.showAdminButton();
        mainView.setAdmin(true);
    }

    @Override
    public void OnAdminCheckFalse() {
        mainView.setAdmin(false);

    }

    @Override
    public void onRegistrationSuccess(String message) {
        registerView.showToast(message);
        registerView.hideProgress();
        registerView.redirectToLogin();
    }

    @Override
    public void onRegistrationFailed(String error) {
        registerView.showAlertDialog(error);
        registerView.hideProgress();
    }

    @Override
    public void sendResetInstructions(String email) {
        forgotPasswordView.showProgress();
        authPresenter.performPasswordReset(this, email);
    }

    @Override
    public void performLogin(String email, String password) {
        authPresenter.login(this, email, password);
    }

    @Override
    public void onLoginWithEmailSuccess() {
        loginView.launchMainActivity();
        loginView.hideProgress();
    }

    @Override
    public void onLoginWithEmailFailed(String error) {
        loginView.showAlertDialog(error);
        loginView.hideProgress();
    }

    @Override
    public void onResetPasswordSuccess(String message) {
        forgotPasswordView.showToast(message);
        forgotPasswordView.redirectToLogin();
        forgotPasswordView.hideProgress();
    }

    @Override
    public void onResetPasswordFailed(String error) {
        forgotPasswordView.showToast(error);
    }
}
