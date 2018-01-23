package cazimir.com.bancuribune.ui.likedJokes;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.base.EmptyRecyclerView;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.ui.myjokes.MyJokesAdapter;

public class MyLikedJokesActivityView extends BaseBackActivity implements ILikedJokesActivityView {

    private LikedJokesAdapter adapter;

    @BindView(R.id.myLikedJokeList)
    EmptyRecyclerView likedJokesListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRecycleView();
        getLikedJokes();
    }

    private void initRecycleView() {
        EmptyRecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        likedJokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new LikedJokesAdapter();
        likedJokesListRecyclerView.setAdapter(adapter);
        likedJokesListRecyclerView.setEmptyView(findViewById(R.id.empty_view));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_liked_jokes_view;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.liked_jokes_title;
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }

    @Override
    public void addToLikedJokeList(Joke joke) {
        adapter.add(joke);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void getLikedJokes() {
        getPresenter().getLikedJokes();
    }
}
