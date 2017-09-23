package cazimir.com.bancuribune.ui.admin;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.repository.JokesRepository;

public class AdminActivityView extends BaseActivity implements IAdminActivityView, OnAdminJokeItemClickListener {

    private CommonPresenter presenter;
    private AdminJokesAdapter adapter;
    @BindView(R.id.jokesList)
    RecyclerView adminJokesListRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.admin_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initRecycleView();

        presenter = new CommonPresenter(this, new JokesRepository());

        getAllPendingJokes();

    }

    private void initRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adminJokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new AdminJokesAdapter(this);
        adminJokesListRecyclerView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_admin_view;
    }

    @Override
    public void getAllPendingJokes() {
        presenter.getAllPendingJokesData();
    }

    @Override
    public void refreshJokes(List<Joke> jokes) {
        adapter = new AdminJokesAdapter(this);
        adminJokesListRecyclerView.setAdapter(adapter);
        for (Joke joke : jokes) {
            adapter.add(joke);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnItemApproved(String uid) {
        presenter.updateJokeApproval(uid);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
