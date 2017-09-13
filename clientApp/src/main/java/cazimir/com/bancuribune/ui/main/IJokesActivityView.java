package cazimir.com.bancuribune.ui.main;

import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public interface IJokesActivityView {
    void populateList(List<Joke> jokes);
    void requestFailed(String error);
}
