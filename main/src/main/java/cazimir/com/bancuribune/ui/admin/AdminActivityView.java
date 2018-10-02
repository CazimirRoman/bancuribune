package cazimir.com.bancuribune.ui.admin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.interfaces.ui.admin.IAdminActivityView;
import cazimir.com.interfaces.ui.admin.OnAdminJokeItemClickListener;
import cazimir.com.models.Joke;

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void setBackArrowColour() {
        final Drawable upArrow = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_arrow_back, null);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private void initRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adminJokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new AdminJokesAdapter(new OnAdminJokeItemClickListener() {
            @Override
            public void onItemApproved(String uid, String jokeText) {
                    getPresenter().approveJoke(uid, jokeText);
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
            public void onItemApproved(String uid, String jokeText) {
                getPresenter().approveJoke(uid, jokeText);
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