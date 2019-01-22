package cazimir.com.bancuribune.view.admin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.callbacks.admin.OnAdminJokeItemClickListener;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.admin.AdminPresenter;
import cazimir.com.bancuribune.repository.DatabaseTypeSingleton;
import cazimir.com.bancuribune.repository.JokesRepository;

public class AdminActivityView extends BaseBackActivity implements IAdminActivityView {

    private AdminJokesAdapter mAdapter;
    private AdminPresenter mPresenter;
    private OnAdminJokeItemClickListener mOnAdminJokeItemClickListener;


    @BindView(R.id.jokesList)
    RecyclerView adminJokesListRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseTypeSingleton type = DatabaseTypeSingleton.getInstance();
        mPresenter = new AdminPresenter(this, new JokesRepository(type.isDebug()));
        initializeOnJokeItemClickListener();
        initRecycleView();
        getAllPendingJokes();
    }

    private void initializeOnJokeItemClickListener() {
        mOnAdminJokeItemClickListener = new OnAdminJokeItemClickListener() {
            @Override
            public void onItemApproved(String uid, String jokeText) {
                mPresenter.approveJoke(uid, jokeText);
            }

            @Override
            public void onItemDeleted(Joke joke) {
                mPresenter.deleteJoke(joke);
            }
        };
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
        mAdapter = new AdminJokesAdapter(mOnAdminJokeItemClickListener);
        adminJokesListRecyclerView.setAdapter(mAdapter);
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
        mPresenter.getAllPendingJokesData();
    }

    @Override
    public void refreshJokes(List<Joke> jokes) {

        mAdapter = new AdminJokesAdapter(mOnAdminJokeItemClickListener);

        adminJokesListRecyclerView.setAdapter(mAdapter);
        for (Joke joke : jokes) {
            mAdapter.add(joke);
        }

        mAdapter.notifyDataSetChanged();
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