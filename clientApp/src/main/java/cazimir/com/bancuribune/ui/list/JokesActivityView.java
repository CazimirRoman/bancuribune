package cazimir.com.bancuribune.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.ui.add.AddJokeActivityView;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.repository.JokesRepository;

public class JokesActivityView extends BaseActivity implements IJokesActivityView {

    private CommonPresenter presenter;
    @BindView(R.id.jokesList) RecyclerView jokesListRecyclerView;
    @BindView(R.id.addJokeButtonFAB) FloatingActionButton addJokeFAB;
    private JokesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRecycleView();
        presenter = new CommonPresenter(this, new JokesRepository());
        presenter.getAllJokesData();
    }

    private void initRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        jokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new JokesAdapter();
        jokesListRecyclerView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_jokes_view;
    }

    @Override
    public void refreshJokes(List<Joke> jokes) {
        adapter = new JokesAdapter();
        jokesListRecyclerView.setAdapter(adapter);
        for(Joke joke : jokes) {
            adapter.add(joke);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void requestFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @OnClick (R.id.addJokeButtonFAB)
    public void checkIfAllowedToAdd(){
        presenter.checkNumberOfAdds();
    }

    @Override
    public void navigateToAddJokeActivity(){
        startActivity(new Intent(this, AddJokeActivityView.class));
    }

    @Override
    public void isNotAllowedToAdd() {
        Toast.makeText(this, "Limit reached", Toast.LENGTH_SHORT).show();
    }
}
