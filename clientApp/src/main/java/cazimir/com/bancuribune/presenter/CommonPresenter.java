package cazimir.com.bancuribune.presenter;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.repository.IJokesRepository;
import cazimir.com.bancuribune.ui.add.IAddJokeActivityView;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.add.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.ui.list.IMainActivityView;
import cazimir.com.bancuribune.ui.list.OnAllowedToAddFinishedListener;
import cazimir.com.bancuribune.ui.list.OnCheckIfVotedFinishedListener;
import cazimir.com.bancuribune.ui.list.OnGetJokesListener;
import cazimir.com.bancuribune.ui.list.OnUpdatePointsFinishedListener;
import cazimir.com.bancuribune.ui.list.OnUpdateVotedByFinishedListener;
import cazimir.com.bancuribune.ui.myjokes.IMyJokesActivityView;
import cazimir.com.bancuribune.ui.myjokes.OnFirebaseGetMyJokesListener;

public class CommonPresenter implements ICommonPresenter, OnGetJokesListener, OnFirebaseGetMyJokesListener, OnAddFinishedListener, OnUpdatePointsFinishedListener, OnUpdateVotedByFinishedListener, OnCheckIfVotedFinishedListener, OnAddJokeVoteFinishedListener, OnAllowedToAddFinishedListener {

    private IMainActivityView mainView;
    private IAddJokeActivityView addView;
    private IMyJokesActivityView myJokesView;
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

    public void getAllJokesData(){
        repository.getAllJokes(this);
        mainView.showProgressBar();
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
        repository.addJoke(this, joke);
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
    public void writeVoteLogToDB(String uid) {
        Vote vote = new Vote();
        vote.setVotedBy(currentUserID);
        vote.setJokeId(uid);
        repository.writeJokeVote(this, vote);
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
}
