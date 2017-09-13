package cazimir.com.bancuribune.ui.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.base.BaseActivity;
import cazimir.com.bancuribune.ui.main.adapter.JokesAdapter;

public class JokesActivityView extends BaseActivity implements IJokesActivityView {

    private JokesPresenter presenter;
    @BindView(R.id.jokesList) RecyclerView jokesListRecyclerView;
    private JokesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        jokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new JokesAdapter();
        jokesListRecyclerView.setAdapter(adapter);
        presenter = new JokesPresenter(this);
        presenter.initData();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_jokes_view;
    }

    @Override
    public void populateList(List<Joke> jokes) {

        for(Joke joke : jokes) {
            adapter.add(joke);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void requestFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
