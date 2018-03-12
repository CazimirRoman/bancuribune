package cazimir.com.bancuribune.ui.list;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.Profile;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.TrelloImpl;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.utils.ScrollListenerRecycleView;
import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.ui.add.AddJokeActivityView;
import cazimir.com.bancuribune.ui.admin.AdminActivityView;
import cazimir.com.bancuribune.ui.likedJokes.LikedJokesActivityView;
import cazimir.com.bancuribune.ui.myJokes.MyJokesActivityView;
import cazimir.com.bancuribune.ui.tutorial.TutorialActivityView;
import cazimir.com.bancuribune.utils.RatingDialogCustom;
import cazimir.com.bancuribune.utils.UtilHelper;

import static cazimir.com.bancuribune.constants.Constants.*;

import static java.lang.Math.abs;

public class MainActivityView extends BaseActivity implements IMainActivityView {

    private static final String TAG = MainActivityView.class.getSimpleName();
    private JokesAdapter adapter;
    private String currentRank;
    private Boolean isAdmin = false;
    private String sharedText;
    private SharedPreferences preferences;
    private MediaPlayer mediaPlayer;

    @BindView(R.id.jokesList)
    RecyclerView jokesListRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.myLikedJokesButtonFAB)
    FloatingActionButton myLikedJokesButtonFAB;
    @BindView(R.id.addJokeButtonFAB)
    FloatingActionButton addJokeFAB;
    @BindView(R.id.myJokesButtonFAB)
    FloatingActionButton myJokesFAB;
    @BindView(R.id.admin)
    FrameLayout admin;
    @BindView(R.id.progress_bar)
    ProgressBar progressMain;
    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.fabActionButtons)
    LinearLayout fabActionButtons;
    @BindView(R.id.fabScrollToTop)
    FrameLayout fabScrollToTop;
    @BindView(R.id.scrollToTop)
    FloatingActionButton scrollToTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpActionbar();
        setSwipeRefreshListener();
        onboardingNeeded();
        initializeRatingReminder();
        checkIfReminderToAddShouldBeShown();
        checkIfAdmin();
        getMyRank();
        getAllJokesData(true, false);
        initializeLikeSound();
    }

    private void initializeLikeSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.drop);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void checkIfReminderToAddShouldBeShown() {
        Date lastCheckDate = getLastCheckDateFromSharedPreferences();
        int daysApart = (int) ((lastCheckDate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24l));
        if (abs(daysApart) >= REMINDER_INTERVAL_CHECK) {
            getPresenter().checkNumberOfAddsLastWeek(lastCheckDate);
        }
    }

    private Date getLastCheckDateFromSharedPreferences() {
        preferences = this.getSharedPreferences("reminder", Context.MODE_PRIVATE);
        long lastCheck = preferences.getLong("last_check", 0);
        //first run
        if (lastCheck == 0) {
            return addLastCheckDateToSharedPreferences();
        }

        return new Date(lastCheck);
    }

    public Date addLastCheckDateToSharedPreferences() {
        Date now = new Date();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("last_check", now.getTime());
        editor.apply();
        return now;
    }

    @Override
    public void checkIfNewRank(String rank) {
        String currentRank = getCurrentRankNameFromSharedPreferences();
        if (currentRank != null && !currentRank.equals(rank)) {
            showAlertDialog("Leveled up!", SweetAlertDialog.SUCCESS_TYPE);
        }

        updateCurrentRank(rank);
    }

    @Override
    public void playOnVotedAudio() {
        mediaPlayer.start();
    }

    private String getCurrentRankNameFromSharedPreferences() {
        preferences = getSharedPreferences(Constants.RANK, MODE_PRIVATE);
        return preferences.getString(Constants.RANK_NAME, null);
    }

    private void initializeRatingReminder() {
        final RatingDialogCustom ratingDialog = (RatingDialogCustom) new RatingDialogCustom.BuilderCustom(this)
                .threshold(STAR_RATING_THRESHOLD)
                .title(getString(R.string.rating_text))
                .session(SESSION_SHOW)
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

        Trello trelloApi = new TrelloImpl(TRELLO_APP_KEY, TRELLO_ACCESS_TOKEN);

        @Override
        protected Card doInBackground(String... params) {
            Card feedBack = new Card();
            feedBack.setName(params[0]);
            feedBack.setDesc(getPresenter().getAuthPresenter().getCurrrentUserEmail());
            return trelloApi.createCard(TRELLO_FEEDBACK_LIST, feedBack);
        }

        @Override
        protected void onPostExecute(Card result) {
            super.onPostExecute(result);
            showToast(getString(R.string.feedback_sent));
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
                if (isInternetAvailable()) {
                    getAllJokesData(true, true);
                } else {
                    showAlertDialog(getString(R.string.no_internet), SweetAlertDialog.ERROR_TYPE);
                    swipeRefreshLayout.setRefreshing(false);
                }

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
        adapter = new JokesAdapter(new OnJokeClickListener() {
            @Override
            public void onJokeShared(final Joke joke) {
                showToast(getString(R.string.share_open));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shareJoke(joke.getJokeText());
                    }
                }, 500);
            }

            @Override
            public void onJokeVoted(Joke joke) {
                getPresenter().checkIfAlreadyVoted(joke);
            }
        });
        jokesListRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showAlertDialog(String message, int type) {
        getAlertDialog().show(message, type);
    }

    private void getMyRank() {
        getPresenter().checkAndGetMyRank();
    }

    private void getAllJokesData(boolean reset, boolean swipe) {
        getPresenter().getAllJokesData(reset, swipe);
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
                fabActionButtons.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                fabScrollToTop.animate().translationX(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                fabActionButtons.animate().translationY(fabActionButtons.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
                fabScrollToTop.animate().translationX(fabActionButtons.getWidth()).setInterpolator(new AccelerateInterpolator(2)).start();
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
            getAlertDialog().show(error, SweetAlertDialog.ERROR_TYPE);
        }
    }

    @OnClick(R.id.addJokeButtonFAB)
    public void checkIfAllowedToAdd() {

        String currentRank = getCurrentRankNameFromSharedPreferences();

        if (isInternetAvailable()) {

            checkIfAdmin();

            if (!isAdmin) {

                if (currentRank.equals(HAMSIE)) {
                    getPresenter().checkNumberOfAdds(ADD_JOKE_LIMIT_HAMSIE);
                } else if (currentRank.equals(HERING)) {
                    getPresenter().checkNumberOfAdds(ADD_JOKE_LIMIT_HERING);
                } else if (currentRank.equals(SOMON)) {
                    getPresenter().checkNumberOfAdds(ADD_JOKE_LIMIT_SOMON);
                } else if (currentRank.equals(STIUCA)) {
                    getPresenter().checkNumberOfAdds(ADD_JOKE_LIMIT_STIUCA);
                } else if (currentRank.equals(RECHIN)) {
                    getPresenter().checkNumberOfAdds(ADD_JOKE_LIMIT_RECHIN);
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

    @OnClick(R.id.adminFAB)
    public void startAdminJokesActivity() {
        if (isInternetAvailable()) {
            startActivity(new Intent(this, AdminActivityView.class));
        }
    }

    @OnClick(R.id.myLikedJokesButtonFAB)
    public void startMyLikedJokesActivity() {
        if (isInternetAvailable()) {
            startActivity(new Intent(this, LikedJokesActivityView.class));
        }
    }

    @OnClick(R.id.scrollToTop)
    public void scrollToTopOfList() {
        jokesListRecyclerView.scrollToPosition(0);
    }


    @Override
    public void navigateToAddJokeActivity() {
        Intent addJokeIntent = new Intent(this, AddJokeActivityView.class);
        addJokeIntent.putExtra(ADMIN, isAdmin);
        startActivityForResult(addJokeIntent, ADD_JOKE_REQUEST);
    }

    @Override
    public void isNotAllowedToAdd(int addLimit) {
        showAlertDialog(String.format(getString(R.string.add_limit_reached), String.valueOf(addLimit)), SweetAlertDialog.WARNING_TYPE);
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

        showAlertDialog(getString(R.string.add_success), SweetAlertDialog.SUCCESS_TYPE);
    }

    @Override
    public void showAddFailedDialog() {
        showAlertDialog(getString(R.string.add_failed), SweetAlertDialog.ERROR_TYPE);
    }

    @Override
    public void showToast(String message) {
        buildToast(message).show();
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
        SharedPreferences.Editor editor = getSharedPreferences(RANK, MODE_PRIVATE).edit();
        editor.putString(RANK, rank.getUid());
        editor.putString(RANK_NAME, rank.getRank());
        editor.apply();
    }

    public void saveRemainingDataToSharedPreferences(int remainingAdds) {
        SharedPreferences.Editor editor = getSharedPreferences(REMAINING_ADDS, MODE_PRIVATE).edit();
        editor.putInt(REMAINING, remainingAdds);
        editor.apply();
    }

    @Override
    public void hideSwipeRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshAdapter(Joke joke) {
        adapter.updatePoints(new OnUpdateListFinished() {
            @Override
            public void onUpdateSuccess(int index) {
                animateHeartIcon(index);
            }
        }, joke);
    }

    private void animateHeartIcon(final int index) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JokesAdapter.MyViewHolder holder = (JokesAdapter.MyViewHolder) jokesListRecyclerView.findViewHolderForAdapterPosition(index);

                final TextView heart = holder.heart;

                final Animation animationEnlarge, animationShrink;
                animationEnlarge = AnimationUtils.loadAnimation(MainActivityView.this,
                        R.anim.enlarge);
                animationShrink = AnimationUtils.loadAnimation(MainActivityView.this,
                        R.anim.shrink);

                animationEnlarge.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        heart.startAnimation(animationShrink);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                heart.startAnimation(animationEnlarge);
            }
        }, 5);

    }

    @Override
    public void updateCurrentRank(String rank) {
        this.currentRank = rank;
    }

    @Override
    public void updateRemainingAdds(int remainingAdds) {
        saveRemainingDataToSharedPreferences(remainingAdds);
    }

    private void shareJoke(String text) {

        this.sharedText = text;

        if (!requestWriteStoragePermissions()) {
            Bitmap bitmap = UtilHelper.drawMultilineTextToBitmap(this, R.drawable.share_background, text);
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
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_STORAGE_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareJoke(sharedText);
            } else {
                showToast(getString(R.string.permission_denied));
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
