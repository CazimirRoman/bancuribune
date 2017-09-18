package cazimir.com.bancuribune.ui.myjokes;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.ui.list.ItemClickListener;
import cazimir.com.bancuribune.utils.MyAlertDialog;

public class MyJokesActivityView extends BaseActivity implements IMyJokesActivityView, ItemClickListener {

    private MyAlertDialog alertDialog;
    private CommonPresenter presenter;
    @BindView(R.id.myJokesList)
    RecyclerView myJokesListRecyclerView;
    private MyJokesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.my_jokes_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initRecycleView();
        presenter = new CommonPresenter(this, new JokesRepository());
        presenter.getMyJokes();

    }

    private void initRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        myJokesListRecyclerView.setLayoutManager(layoutManager);
        myJokesListRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        adapter = new MyJokesAdapter(this);
        myJokesListRecyclerView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_jokes_view;
    }


    @Override
    public void showJokesList(List<Joke> jokes) {
        adapter = new MyJokesAdapter(this);
        myJokesListRecyclerView.setAdapter(adapter);
        for (Joke joke : jokes) {
            adapter.add(joke);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(Joke data) {

    }

    private void showAlertDialog(int message) {
        alertDialog.getAlertDialog().setMessage(getString(message));
        if (!alertDialog.getAlertDialog().isShowing()) alertDialog.getAlertDialog().show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
