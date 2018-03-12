package cazimir.com.bancuribune.ui.admin;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Joke;

public class AdminActivityView extends BaseBackActivity implements IAdminActivityView {

    private AdminJokesAdapter adapter;
    @BindView(R.id.jokesList)
    RecyclerView adminJokesListRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRecycleView();
        getAllPendingJokes();
    }

    private void initRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adminJokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new AdminJokesAdapter(new OnAdminJokeItemClickListener() {
            @Override
            public void onItemApproved(String uid) {
                getPresenter().approveJoke(uid);
            }
        });
        adminJokesListRecyclerView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_admin_view;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.admin_title;
    }

    @Override
    public void getAllPendingJokes() {
        getPresenter().getAllPendingJokesData();
    }

    @Override
    public void refreshJokes(List<Joke> jokes) {
        adapter = new AdminJokesAdapter(new OnAdminJokeItemClickListener() {
            @Override
            public void onItemApproved(String uid) {
                getPresenter().approveJoke(uid);
            }
        });
        adminJokesListRecyclerView.setAdapter(adapter);
        for (Joke joke : jokes) {
            adapter.add(joke);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String message) {
        buildToast(message).show();
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }
}