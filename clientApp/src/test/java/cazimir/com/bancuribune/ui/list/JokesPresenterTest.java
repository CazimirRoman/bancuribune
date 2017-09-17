package cazimir.com.bancuribune.ui.list;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.add.OnAddFinishedListener;
import cazimir.com.bancuribune.ui.common.IJokesRepository;
import cazimir.com.bancuribune.presenter.JokesPresenter;

public class JokesPresenterTest {

    @Test
    public void shouldPassJokesToView() {

        IJokesActivityView view = new MockJokesView();
        IJokesRepository jokesRepository = new MockJokesRepository(true);
        JokesPresenter jokesPresenter = new JokesPresenter(view, jokesRepository);
        jokesPresenter.getAllJokesData();
        Assert.assertEquals(true, ((MockJokesView) view).displayJokesCalled);

    }

    @Test
    public void shouldFailWhenNoJokesFound() {
        IJokesActivityView view = new MockJokesView();
        IJokesRepository jokesRepository = new MockJokesRepository(false);
        JokesPresenter jokesPresenter = new JokesPresenter(view, jokesRepository);
        jokesPresenter.getAllJokesData();
        Assert.assertEquals(true, ((MockJokesView) view).requestFailedCalled);
    }

    private class MockJokesView implements IJokesActivityView {

        boolean displayJokesCalled;
        boolean requestFailedCalled;

        @Override
        public void refreshJokes(List<Joke> jokes) {
            displayJokesCalled = true;
        }

        @Override
        public void requestFailed(String error) {
            requestFailedCalled = true;
        }
    }

    private class MockJokesRepository implements IJokesRepository {

        private boolean returnSomeJokes;

        public MockJokesRepository(boolean returnSomeBooks) {
            this.returnSomeJokes = returnSomeBooks;
        }

        @Override
        public void getAllJokes(OnRequestFinishedListener listener) {

            List<Joke> jokes;
            if (returnSomeJokes) {
                jokes = Arrays.asList(new Joke("Joke Test 234"), new Joke("Joke Test 239"), new Joke("Joke Test 235"));
                listener.onSuccess(jokes);
            } else {
                listener.onError("No jokes");
            }

        }

        @Override
        public void addJoke(OnAddFinishedListener listener, Joke joke) {

        }
    }

}