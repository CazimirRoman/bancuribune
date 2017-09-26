package cazimir.com.bancuribune.ui.list;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Vote;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.repository.IJokesRepository;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.add.OnAddJokeVoteFinishedListener;
import cazimir.com.bancuribune.ui.myjokes.OnFirebaseGetMyJokesListener;

public class JokesPresenterTest {

    @Test
    public void shouldPassJokesToView() {

        IMainActivityView view = new MockJokesView();
        IJokesRepository jokesRepository = new MockJokesRepository(true);
        CommonPresenter jokesPresenter = new CommonPresenter(view, jokesRepository);
        jokesPresenter.getAllJokesData();
        Assert.assertEquals(true, ((MockJokesView) view).displayJokesCalled);

    }

    @Test
    public void shouldFailWhenNoJokesFound() {
        IMainActivityView view = new MockJokesView();
        IJokesRepository jokesRepository = new MockJokesRepository(false);
        CommonPresenter jokesPresenter = new CommonPresenter(view, jokesRepository);
        jokesPresenter.getAllJokesData();
        Assert.assertEquals(true, ((MockJokesView) view).requestFailedCalled);
    }

    private class MockJokesView implements IMainActivityView {

        boolean displayJokesCalled;
        boolean requestFailedCalled;

        @Override
        public void displayJokes(List<Joke> jokes) {
            displayJokesCalled = true;
        }

        @Override
        public void requestFailed(String error) {
            requestFailedCalled = true;
        }

        @Override
        public void navigateToAddJokeActivity() {

        }

        @Override
        public void isNotAllowedToAdd() {

        }

        @Override
        public void showAddSuccessDialog() {

        }

        @Override
        public void showAddFailedDialog() {

        }

        @Override
        public void showTestToast(String message) {

        }

        @Override
        public void redirectToLoginPage() {

        }

        @Override
        public void showProgressBar() {

        }

        @Override
        public void hideProgressBar() {

        }

        @Override
        public void showAlertDialog(String message) {

        }
    }

    private class MockJokesRepository implements IJokesRepository {

        private boolean returnSomeJokes;

        public MockJokesRepository(boolean returnSomeJokes) {
            this.returnSomeJokes = returnSomeJokes;
        }

        @Override
        public void getAllJokes(OnGetJokesListener listener) {

            List<Joke> jokes;
            if (returnSomeJokes) {
                jokes = Arrays.asList(new Joke("Joke Test 234"), new Joke("Joke Test 239"), new Joke("Joke Test 235"));
                listener.OnGetJokesSuccess(jokes);
            } else {
                listener.OnGetJokesFailed("No jokes");
            }

        }

        @Override
        public void getAllFilteredJokes(OnGetJokesListener listener, String text) {

        }

        @Override
        public void getMyJokes(OnFirebaseGetMyJokesListener listener, String userID) {

        }

        @Override
        public void addJoke(OnAddFinishedListener listener, Joke joke) {

        }

        @Override
        public void getAllJokesAddedToday(OnAllowedToAddFinishedListener listener, String userID) {

        }

        @Override
        public void updateJokePoints(OnUpdatePointsFinishedListener listener, String uid) {

        }

        @Override
        public void updateVotedBy(OnUpdateVotedByFinishedListener listener, String uid, String userId) {

        }

        @Override
        public void writeJokeVote(OnAddJokeVoteFinishedListener listener, Vote vote) {

        }

        @Override
        public void checkIfVoted(OnCheckIfVotedFinishedListener listener, String uid, String userId) {

        }
    }

}