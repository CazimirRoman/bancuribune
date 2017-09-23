package cazimir.com.bancuribune.presenter;

import com.facebook.Profile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.repository.IJokesRepository;
import cazimir.com.bancuribune.ui.add.IAddJokeActivityView;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.add.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.ui.admin.IAdminActivityView;
import cazimir.com.bancuribune.ui.admin.OnGetAllPendingJokesListener;
import cazimir.com.bancuribune.ui.admin.OnUpdateApproveStatusListener;
import cazimir.com.bancuribune.ui.list.IMainActivityView;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnCheckIfAdminListener;
import cazimir.com.bancuribune.ui.list.OnCheckIfVotedFinishedListener;
import cazimir.com.bancuribune.ui.list.OnGetJokesListener;
import cazimir.com.bancuribune.ui.list.OnUpdatePointsFinishedListener;
import cazimir.com.bancuribune.ui.list.OnUpdateVotedByFinishedListener;
import cazimir.com.bancuribune.ui.myjokes.IMyJokesActivityView;
import cazimir.com.bancuribune.ui.myjokes.OnCalculatePointsListener;
import cazimir.com.bancuribune.ui.myjokes.OnFirebaseGetMyJokesListener;
import cazimir.com.bancuribune.ui.myjokes.OnGetFacebookNameListener;

public class CommonPresenter implements ICommonPresenter, OnGetJokesListener, OnGetAllPendingJokesListener, OnUpdateApproveStatusListener, OnFirebaseGetMyJokesListener, OnAddFinishedListener, OnUpdatePointsFinishedListener, OnUpdateVotedByFinishedListener, OnCheckIfVotedFinishedListener, OnAddJokeVoteFinishedListener, OnAllowedToAddFinishedListener {

    private IMainActivityView mainView;
    private IAddJokeActivityView addView;
    private IMyJokesActivityView myJokesView;
    private IAdminActivityView adminView;
    private IJokesRepository repository;
    private IAuthPresenter authPresenter;
    private String currentUserID;

    public CommonPresenter(IMainActivityView view, IJokesRepository repository) {
        this.mainView = view;
        this.repository = repository;
        this.authPresenter = new AuthPresenter(view);
        currentUserID = authPresenter.getCurrentUserID();
    }

    public CommonPresenter(IAddJokeActivityView view, IJokesRepository repository) {
        this.addView = view;
        this.repository = repository;
        this.authPresenter = new AuthPresenter(view);
        currentUserID = authPresenter.getCurrentUserID();
    }

    public CommonPresenter(IMyJokesActivityView view, IJokesRepository repository) {
        this.myJokesView = view;
        this.repository = repository;
        this.authPresenter = new AuthPresenter(myJokesView);
        currentUserID = authPresenter.getCurrentUserID();
    }

    public CommonPresenter(IAdminActivityView view, IJokesRepository repository) {
        this.adminView = view;
        this.repository = repository;
        this.authPresenter = new AuthPresenter(adminView);
        currentUserID = authPresenter.getCurrentUserID();
    }

    public void getAllJokesData(){
        repository.getAllJokes(this);
        mainView.showProgressBar();
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
    public void addJoke(Joke joke) {
        joke.setCreatedBy(currentUserID);
        joke.setUserName(authPresenter.getCurrentUserName());
        if (isAdmin()) {
            joke.setApproved(true);
        }
        repository.addJoke(this, joke);
    }

    private boolean isAdmin() {
        if(authPresenter.getCurrentUserID().equals(Constants.ADMIN_ID)){
            return true;
        }

        return false;
    }

    @Override
    public void checkIfAdmin(OnCheckIfAdminListener listener) {

        if(isAdmin()){
            listener.OnAdminTrue();
        }
    }

    @Override
    public void checkNumberOfAdds() {
        repository.getAllJokesAddedToday(this, currentUserID);
    }

    @Override
    public void isAllowedToAdd() {
        mainView.navigateToAddJokeActivity();
    }

    @Override
    public void isNotAllowedToAdd() {
        mainView.isNotAllowedToAdd();
    }

    @Override
    public void logOutUser() {
        authPresenter.logUserOut(mainView);
    }

    @Override
    public void checkIfAlreadyVoted(String uid) {
        repository.checkIfVoted(this, uid, currentUserID);
    }

    @Override
    public void updateJokePoints(String uid) {
        repository.updateJokePoints(this, uid);
        repository.updateVotedBy(this, uid, currentUserID);
        writeVoteLogToDB(uid);
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
        String id = Profile.getCurrentProfile().getId();
        final URL imageURL = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
        listener.OnGetProfilePictureSuccess(imageURL);
    }

    @Override
    public void getFacebookName(OnGetFacebookNameListener listener) {
        String name = Profile.getCurrentProfile().getName();
        if(name != null){
            listener.OnGetFacebookNameSuccess(name);
        }else{
            listener.OnGetFacebookNameFailed();
        }

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
    public void OnGetJokesSuccess(List<Joke> jokes) {
        mainView.refreshJokes(jokes);
    }

    @Override
    public void OnGetJokesFailed(String error) {
        mainView.requestFailed(error);
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
        myJokesView.showJokesList(jokes);
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
    public void OnUpdatePointsSuccess() {

    }

    @Override
    public void OnHasVotedTrue() {
        mainView.showTestToast("Bancul asta a fost deja votat de tine. Multumim!");
    }

    @Override
    public void OnHasVotedFalse(String uid) {
        updateJokePoints(uid);
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
}
