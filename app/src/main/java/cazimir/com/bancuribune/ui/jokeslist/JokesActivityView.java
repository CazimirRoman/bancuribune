package cazimir.com.bancuribune.ui.jokeslist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.model.Joke;

public class JokesActivityView extends AppCompatActivity implements IJokesActivityView {

    JokesPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jokes_view);
        presenter = new JokesPresenter(this);
    }

    @Override
    public void addJokes(List<Joke> jokes) {
        //populate Recyclerview with data
    }
}
