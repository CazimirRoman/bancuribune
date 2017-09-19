package cazimir.com.bancuribune.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.facebook.Profile;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.ui.add.AddJokeActivityView;
import cazimir.com.bancuribune.ui.login.LoginActivityView;
import cazimir.com.bancuribune.ui.login.OnAuthStateListenerRegister;
import cazimir.com.bancuribune.ui.myjokes.MyJokesActivityView;
import cazimir.com.bancuribune.utils.MyAlertDialog;

import static cazimir.com.bancuribune.R.id.addJokeButtonFAB;
import static cazimir.com.bancuribune.constants.Constants.ADD_JOKE_REQUEST;

public class MainActivityView extends BaseActivity implements IMainActivityView, ItemClickListener, OnAuthStateListenerRegister {

    private MyAlertDialog alertDialog;
    private CommonPresenter presenter;
    @BindView(R.id.jokesList)
    RecyclerView jokesListRecyclerView;
    @BindView(addJokeButtonFAB)
    FloatingActionButton addJokeFAB;
    @BindView(R.id.myJokesButtonFAB)
    FloatingActionButton logoutFAB;
    @BindView(R.id.logoutButtonFAB)
    FloatingActionButton myJokesFAB;
    @BindView(R.id.progress_bar)
    ProgressBar progress;
    private JokesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        alertDialog = new MyAlertDialog(this);
        initRecycleView();
        presenter = new CommonPresenter(this, new JokesRepository());
        presenter.getAllJokesData();
        progress.setVisibility(View.VISIBLE);
    }

    private void initRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        jokesListRecyclerView.setLayoutManager(layoutManager);
        jokesListRecyclerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        adapter = new JokesAdapter(this);
        jokesListRecyclerView.setAdapter(adapter);


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_view;
    }

    @Override
    public void refreshJokes(List<Joke> jokes) {
        adapter = new JokesAdapter(this);
        jokesListRecyclerView.setAdapter(adapter);
        for (Joke joke : jokes) {
            adapter.add(joke);
        }

        adapter.notifyDataSetChanged();
        progress.setVisibility(View.GONE);
    }

    @Override
    public void requestFailed(String error) {
        //TODO : refactor
        progress.setVisibility(View.GONE);
        if(Profile.getCurrentProfile() != null){
            alertDialog.getAlertDialog().setMessage(error);
            if (!alertDialog.getAlertDialog().isShowing()) alertDialog.getAlertDialog().show();
        }
    }

    @OnClick(addJokeButtonFAB)
    public void checkIfAllowedToAdd() {
        presenter.checkNumberOfAdds();
    }

    @OnClick(R.id.myJokesButtonFAB)
    public void startMyJokesActivity() {
        startActivity(new Intent(this, MyJokesActivityView.class));
    }

    @OnClick(R.id.logoutButtonFAB)
    public void logoutUser() {
        presenter.logOutUser();
    }

    @Override
    public void navigateToAddJokeActivity() {
        Intent addJokeIntent = new Intent(this, AddJokeActivityView.class);
        startActivityForResult(addJokeIntent, ADD_JOKE_REQUEST);
    }

    @Override
    public void isNotAllowedToAdd() {
        showAlertDialog(R.string.add_limit_reached);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_JOKE_REQUEST) {
            if (resultCode == RESULT_OK) {
                showAddConfirmationDialog();
            }
        }
    }

    public void showAddConfirmationDialog() {
        showAlertDialog(R.string.add_confirmation);
    }

    @Override
    public void redirectToLoginPage() {
        this.finish();
        startActivity(new Intent(this, LoginActivityView.class));
    }

    private void showAlertDialog(int message) {
        alertDialog.getAlertDialog().setMessage(getString(message));
        if (!alertDialog.getAlertDialog().isShowing()) alertDialog.getAlertDialog().show();
    }


    @Override
    public void onItemClicked(Joke joke) {
        shareJoke(joke.getJokeText());
    }

    private void shareJoke(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_text), getString(R.string.app_name)) + "\n\n" + text);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share"));
    }

    @Override
    public void onAuthListenerSuccess() {

    }
}
