package cazimir.com.bancuribune.ui.jokeslist;

import java.util.ArrayList;
import java.util.List;

import cazimir.com.bancuribune.model.Joke;

public class JokesPresenter {

    private IJokesActivityView view;


    public JokesPresenter(IJokesActivityView view) {
        this.view = view;
    }

    public void initData(){
        List<Joke> jokes = new ArrayList<>();
        jokes.add(new Joke());
        jokes.add(new Joke());
        jokes.add(new Joke());
        view.addJokes(jokes);

    }
}
