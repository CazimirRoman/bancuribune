package cazimir.com.bancuribune.ui.list;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.facebook.Profile;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.TrelloImpl;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.base.ScrollListenerRecycleView;
import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.ui.add.AddJokeActivityView;
import cazimir.com.bancuribune.ui.admin.AdminActivityView;
import cazimir.com.bancuribune.ui.likedJokes.MyLikedJokesActivityView;
import cazimir.com.bancuribune.ui.myjokes.MyJokesActivityView;
import cazimir.com.bancuribune.ui.tutorial.TutorialActivityView;
import cazimir.com.bancuribune.utils.RatingDialogCustom;
import cazimir.com.bancuribune.utils.UtilHelperClass;

import static cazimir.com.bancuribune.R.id.addJokeButtonFAB;
import static cazimir.com.bancuribune.R.id.adminFAB;
import static cazimir.com.bancuribune.constants.Constants.ADD_JOKE_REQUEST;

public class MainActivityView extends BaseActivity implements IMainActivityView, OnJokeItemClickListener {

    private JokesAdapter adapter;
    private String currentRank;
    private Boolean isAdmin = false;
    private String sharedText;

    @BindView(R.id.jokesList)
    RecyclerView jokesListRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.myLikedJokesButtonFAB)
    FloatingActionButton myLikedJokesButtonFAB;
    @BindView(addJokeButtonFAB)
    FloatingActionButton addJokeFAB;
    @BindView(R.id.myJokesButtonFAB)
    FloatingActionButton myJokesFAB;
    @BindView(R.id.admin)
    FrameLayout admin;
    @BindView(R.id.progress_bar)
    ProgressBar progressMain;
    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.fab)
    LinearLayout fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpActionbar();
        setSwipeRefreshListener();
        onboardingNeeded();
        initializeRating();
        initSearch();
        checkIfAdmin();
        getMyRank();
        getAllJokesData(true, false);
    }

    private void initializeRating() {
        final RatingDialogCustom ratingDialog = (RatingDialogCustom) new RatingDialogCustom.BuilderCustom(this)
                .threshold(Constants.STAR_RATING_THRESHOLD)
                .title(getString(R.string.rating_text))
                .session(Constants.SESSION_SHOW)
                .positiveButtonText(getString(R.string.not_now))
                .formTitle(getString(R.string.rating_send_message))
                .formHint(getString(R.string.rating_improve_question))
                .formSubmitText(getString(R.string.rating_send))
                .formCancelText(getString(R.string.rating_cancel))
                .negativeButtonText("")
                .onRatingBarFormSumbit(new RatingDialogCustom.BuilderCustom.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        sendFeedbackToTrello(feedback);
                    }
                }).build();

        ratingDialog.show();
    }

    private void sendFeedbackToTrello(final String feedback) {
        new sendFeedbackToTrello().execute(feedback);
    }

    private class sendFeedbackToTrello extends AsyncTask<String, Integer, Card> {

        Trello trelloApi = new TrelloImpl(Constants.TRELLO_APP_KEY, Constants.TRELLO_ACCESS_TOKEN);

        @Override
        protected Card doInBackground(String... params) {
            Card feedBack = new Card();
            feedBack.setName(params[0]);
            feedBack.setDesc(getPresenter().getAuthPresenter().getCurrrentUserEmail());
            return trelloApi.createCard(Constants.TRELLO_FEEDBACK_LIST, feedBack);
        }

        @Override
        protected void onPostExecute(Card result) {
            super.onPostExecute(result);
            Toast.makeText(MainActivityView.this, getString(R.string.feedback_sent), Toast.LENGTH_SHORT).show();
        }
    }

    private void onboardingNeeded() {
        if (isFirstRun()) {
            startTutorialActivity();
        }
    }

    private void startTutorialActivity() {
        startActivity(new Intent(this, TutorialActivityView.class));
    }

    private void setSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllJokesData(true, true);
            }
        });
    }

    private boolean isFirstRun() {

        Boolean mFirstRun;

        SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
        mFirstRun = mPreferences.getBoolean(getPresenter().getCurrentUserID(), true);
        if (mFirstRun) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(getPresenter().getCurrentUserID(), false);
            editor.apply();
            return true;
        }

        return false;
    }

    private void setUpActionbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("  " + getString(R.string.app_name));
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        }
    }

    public void refreshJokesListAdapter() {
        setOnScrollListener((LinearLayoutManager) initRecycleView());
        adapter = new JokesAdapter(this);
        jokesListRecyclerView.setAdapter(adapter);

    }

    @Override
    public void showAlertDialog(String message) {
        getAlertDialog().show(message);
    }

    private void getMyRank() {
        getPresenter().checkAndGetMyRank();
    }

    private void getAllJokesData(boolean reset, boolean swipe) {
        getPresenter().getAllJokesData(reset, swipe);
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
                    getPresenter().getAllJokesData(true, true);
                } else {
                    if (editable.toString().length() >= Constants.FILTER_MINIMUM_CHARACTERS) {
                        showProgressBar();
                        getPresenter().getFilteredJokesData(editable.toString().trim());
                    }
                }
            }
        });
    }

    private RecyclerView.LayoutManager initRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        jokesListRecyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    private void setOnScrollListener(final LinearLayoutManager layoutManager) {
        jokesListRecyclerView.setOnScrollListener(new ScrollListenerRecycleView(layoutManager) {

            @Override
            public void show() {
                fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                fab.animate().translationY(fab.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
            }

            @Override
            public void onLoadMore(int current_page) {
                getAllJokesData(false, false);

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_view;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.app_name;
    }

    @Override
    public void setAdmin(Boolean value) {
        isAdmin = value;
    }

    @Override
    public void displayJokes(List<Joke> jokes) {

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
            getAlertDialog().show(error);
        }
    }

    @OnClick(addJokeButtonFAB)
    public void checkIfAllowedToAdd() {

        if (isInternetAvailable()) {

            checkIfAdmin();

            if (!isAdmin) {

                if (currentRank.equals(Constants.HAMSIE)) {
                    getPresenter().checkNumberOfAdds(Constants.ADD_JOKE_LIMIT_HAMSIE);
                } else if (currentRank.equals(Constants.HERING)) {
                    getPresenter().checkNumberOfAdds(Constants.ADD_JOKE_LIMIT_HERING);
                } else if (currentRank.equals(Constants.SOMON)) {
                    getPresenter().checkNumberOfAdds(Constants.ADD_JOKE_LIMIT_SOMON);
                } else if (currentRank.equals(Constants.STIUCA)) {
                    getPresenter().checkNumberOfAdds(Constants.ADD_JOKE_LIMIT_STIUCA);
                } else if (currentRank.equals(Constants.RECHIN)) {
                    getPresenter().checkNumberOfAdds(Constants.ADD_JOKE_LIMIT_RECHIN);
                }

            } else {
                navigateToAddJokeActivity();
            }
        }
    }

    @OnClick(R.id.myJokesButtonFAB)
    public void startMyJokesActivity() {

        if (isInternetAvailable()) {
            startActivity(new Intent(new Intent(this, MyJokesActivityView.class)));
        }
    }

    @OnClick(adminFAB)
    public void startAdminJokesActivity() {
        if (isInternetAvailable()) {
            startActivity(new Intent(this, AdminActivityView.class));
        }
    }

    @OnClick(R.id.myLikedJokesButtonFAB)
    public void startMyLikedJokesActivity() {
        if (isInternetAvailable()) {
            startActivity(new Intent(this, MyLikedJokesActivityView.class));
        }
    }

    @Override
    public void navigateToAddJokeActivity() {
        Intent addJokeIntent = new Intent(this, AddJokeActivityView.class);
        addJokeIntent.putExtra(Constants.ADMIN, isAdmin);
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
                getAllJokesData(true, false);
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
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
    public void saveRankDataToSharedPreferences(Rank rank) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.RANK, MODE_PRIVATE).edit();
        editor.putString(Constants.RANK, rank.getUid());
        editor.apply();
    }

    public void saveRemainingDataToSharedPreferences(int remainingAdds) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.REMAINING_ADDS, MODE_PRIVATE).edit();
        editor.putInt(Constants.REMAINING, remainingAdds);
        editor.apply();
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
    public void updateRemainingAdds(int remainingAdds) {
        saveRemainingDataToSharedPreferences(remainingAdds);
    }


    @Override
    public void onItemShared(Joke joke) {
        shareJoke(joke.getJokeText());
    }

    @Override
    public void onItemVoted(Joke joke) {
        getPresenter().checkIfAlreadyVoted(joke);
    }

    private void shareJoke(String text) {

        this.sharedText = text;

        if (!requestWriteStoragePermissions()) {
            Bitmap bitmap = UtilHelperClass.drawMultilineTextToBitmap(this, R.drawable.share_background, text);
            String bitmapPath = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "title", "description");
            Uri bitmapUri = Uri.parse(bitmapPath);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            sendIntent.setType("image/*");
            startActivity(Intent.createChooser(sendIntent, "Share"));
        }
    }

    private boolean requestWriteStoragePermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.MY_STORAGE_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.MY_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareJoke(sharedText);
            } else {
                Toast.makeText(this, "Permission denied. Please accept permission request", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void checkIfAdmin() {
        getPresenter().checkIfAdmin();
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }
}
