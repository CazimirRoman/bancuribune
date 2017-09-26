package cazimir.com.bancuribune.ui.list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.Profile;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.ui.MyRecylerScroll;
import cazimir.com.bancuribune.ui.add.AddJokeActivityView;
import cazimir.com.bancuribune.ui.admin.AdminActivityView;
import cazimir.com.bancuribune.ui.login.LoginActivityView;
import cazimir.com.bancuribune.ui.myjokes.MyJokesActivityView;
import cazimir.com.bancuribune.utils.MyAlertDialog;

import static cazimir.com.bancuribune.R.id.addJokeButtonFAB;
import static cazimir.com.bancuribune.R.id.adminFAB;
import static cazimir.com.bancuribune.constants.Constants.ADD_JOKE_REQUEST;

public class MainActivityView extends BaseActivity implements IMainActivityView, OnJokeItemClickListener {

    private MyAlertDialog alertDialog;
    private CommonPresenter presenter;
    private JokesAdapter adapter;
    @BindView(R.id.jokesList)
    RecyclerView jokesListRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(addJokeButtonFAB)
    FloatingActionButton addJokeFAB;
    @BindView(R.id.myJokesButtonFAB)
    FloatingActionButton logoutFAB;
    @BindView(R.id.admin)
    FrameLayout admin;
    @BindView(R.id.logoutButtonFAB)
    FloatingActionButton myJokesFAB;
    @BindView(R.id.progress_bar)
    ProgressBar progressMain;
    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.fab)
    LinearLayout fab;
    private String currentRank;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("  " + getString(R.string.app_name));
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        }

        alertDialog = new MyAlertDialog(this);

        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllJokesData();
            }
        });

        initRecycleView();
        initSearch();
        presenter = new CommonPresenter(this, new JokesRepository());
        getMyRank();
        getAllJokesData();
    }

    private void getMyRank() {
        presenter.checkAndGetMyRank();
    }

    private void getAllJokesData() {
        presenter.getAllJokesData();
    }

    private void initSearch() {

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().isEmpty()) {
                    presenter.getAllJokesData();
                } else {
                    if (editable.toString().length() >= Constants.FILTER_MINIMUM_CHARACTERS) {
                        showProgressBar();
                        presenter.getFilteredJokesData(editable.toString());
                    }
                }

            }

        });
    }

    private void initRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        jokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new JokesAdapter(this);

        jokesListRecyclerView.setOnScrollListener(new MyRecylerScroll() {

            @Override
            public void show() {
                fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                fab.animate().translationY(fab.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        });

        jokesListRecyclerView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_view;
    }

    @Override
    public void displayJokes(List<Joke> jokes) {
        adapter = new JokesAdapter(this);
        jokesListRecyclerView.setAdapter(adapter);
        for (Joke joke : jokes) {
            adapter.add(joke);
        }

        adapter.notifyDataSetChanged();
        hideProgressBar();
    }

    @Override
    public void requestFailed(String error) {
        //TODO : refactor
        hideProgressBar();
        if (Profile.getCurrentProfile() != null) {
            alertDialog.getAlertDialog().setMessage(error);
            if (!alertDialog.getAlertDialog().isShowing()) alertDialog.getAlertDialog().show();
        }
    }

    @OnClick(addJokeButtonFAB)
    public void checkIfAllowedToAdd() {

        Boolean isAdmin = getAdminDataFromSharedPreferences();

        if (!isAdmin) {

            if (currentRank.equals(Constants.MORMOLOC)) {
                presenter.checkNumberOfAdds(Constants.ADD_JOKE_LIMIT_MORMOLOC);
            } else if (currentRank.equals(Constants.HAMSIE)) {
                presenter.checkNumberOfAdds(Constants.ADD_JOKE_LIMIT_HAMSIE);
            } else if (currentRank.equals(Constants.HERING)) {
                presenter.checkNumberOfAdds(Constants.ADD_JOKE_LIMIT_HERING);
            } else if (currentRank.equals(Constants.SOMON)) {
                presenter.checkNumberOfAdds(Constants.ADD_JOKE_LIMIT_SOMON);
            } else if (currentRank.equals(Constants.SOMON)) {
                presenter.checkNumberOfAdds(Constants.ADD_JOKE_LIMIT_STIUCA);
            }

        } else {
            navigateToAddJokeActivity();
        }

    }

    @OnClick(R.id.myJokesButtonFAB)
    public void startMyJokesActivity() {
        Intent i = new Intent(this, MyJokesActivityView.class);
        startActivity(i);
    }

    @OnClick(adminFAB)
    public void startAdminJokesActivity() {
        startActivity(new Intent(this, AdminActivityView.class));
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
    public void isNotAllowedToAdd(int addLimit) {
        showAlertDialog(String.format(getString(R.string.add_limit_reached), String.valueOf(addLimit)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_JOKE_REQUEST) {
            if (resultCode == RESULT_OK) {
                showAddSuccessDialog();
                getAllJokesData();
            }
        }
    }

    public void showAddSuccessDialog() {
        showAlertDialog(getString(R.string.add_success));
    }

    @Override
    public void showAddFailedDialog() {
        showAlertDialog(getString(R.string.add_failed));
    }

    @Override
    public void showTestToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void redirectToLoginPage() {
        this.finish();
        startActivity(new Intent(this, LoginActivityView.class));
    }

    @Override
    public void showProgressBar() {
        progressMain.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressMain.setVisibility(View.GONE);
    }

    @Override
    public void showAdminButton() {
        admin.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAlertDialog(String message) {
        alertDialog.showAlertDialog(message);
    }

    @Override
    public void saveRankDataToSharedPreferences(Rank rank) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.RANK, MODE_PRIVATE).edit();
        editor.putString(Constants.RANK, rank.getUid());
        editor.apply();
    }

    @Override
    public void saveAdminDataToSharedPreferences(Boolean isAdmin) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.ADMIN, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.ADMIN, isAdmin);
        editor.apply();
    }

    public void saveRemainingDataToSharedPreferences(int remainingAdds) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.REMAINING_ADDS, MODE_PRIVATE).edit();
        editor.putInt(Constants.REMAINING, remainingAdds);
        editor.apply();
    }

    @Override
    public boolean getAdminDataFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(Constants.ADMIN, MODE_PRIVATE);
        return prefs.getBoolean(Constants.ADMIN, false);

    }

    @Override
    public void hideSwipeRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshAdapter(Joke joke) {
        adapter.updateList(joke);
    }

    @Override
    public void updateCurrentRank(String rank) {
        this.currentRank = rank;
    }

    @Override
    public void updateRemainingAdds(int remaininigAdds) {
        saveRemainingDataToSharedPreferences(remaininigAdds);
    }


    @Override
    public void onItemShared(Joke joke) {
        shareJoke(joke.getJokeText());
    }

    @Override
    public void onItemVoted(Joke joke) {
        presenter.checkIfAlreadyVoted(joke);
    }

    private void shareJoke(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_text), getString(R.string.app_name)) + "\n\n" + text);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share"));
    }

    @Override
    public void checkIfAdmin() {
        presenter.checkIfAdmin();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
