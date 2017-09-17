package cazimir.com.bancuribune.ui.list;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface IJokesActivityView {
    void refreshJokes(List<Joke> jokes);
    void requestFailed(String error);
}
