package cazimir.com.bancuribune.view.list;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.facebook.Profile;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.TrelloImpl;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.BuildConfig;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.callbacks.list.IMainActivityView;
import cazimir.com.bancuribune.callbacks.list.OnJokeClickListener;
import cazimir.com.bancuribune.callbacks.list.OnUpdateListFinished;
import cazimir.com.bancuribune.constant.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.presenter.auth.AuthPresenter;
import cazimir.com.bancuribune.presenter.main.MainPresenter;
import cazimir.com.bancuribune.report.ReportActivityView;
import cazimir.com.bancuribune.repository.DatabaseTypeSingleton;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.utils.RatingDialogCustom;
import cazimir.com.bancuribune.utils.ScrollListenerRecycleView;
import cazimir.com.bancuribune.utils.UtilHelper;
import cazimir.com.bancuribune.view.add.AddJokeActivityView;
import cazimir.com.bancuribune.view.admin.AdminActivityView;
import cazimir.com.bancuribune.view.likedJokes.LikedJokesActivityView;
import cazimir.com.bancuribune.view.login.LoginActivityView;
import cazimir.com.bancuribune.view.profile.MyJokesActivityView;
import cazimir.com.bancuribune.view.tutorial.TutorialActivityView;
import timber.log.Timber;

import static cazimir.com.bancuribune.constant.Constants.ADD_JOKE_LIMIT_HAMSIE;
import static cazimir.com.bancuribune.constant.Constants.ADD_JOKE_LIMIT_HERING;
import static cazimir.com.bancuribune.constant.Constants.ADD_JOKE_LIMIT_RECHIN;
import static cazimir.com.bancuribune.constant.Constants.ADD_JOKE_LIMIT_SOMON;
import static cazimir.com.bancuribune.constant.Constants.ADD_JOKE_LIMIT_STIUCA;
import static cazimir.com.bancuribune.constant.Constants.ADD_JOKE_REQUEST;
import static cazimir.com.bancuribune.constant.Constants.ADMIN;
import static cazimir.com.bancuribune.constant.Constants.EVENT_JOKE_EXPANDED;
import static cazimir.com.bancuribune.constant.Constants.EVENT_SHARED;
import static cazimir.com.bancuribune.constant.Constants.HAMSIE;
import static cazimir.com.bancuribune.constant.Constants.HERING;
import static cazimir.com.bancuribune.constant.Constants.LIKED_JOKES_REQ_CODE;
import static cazimir.com.bancuribune.constant.Constants.MY_STORAGE_REQ_CODE;
import static cazimir.com.bancuribune.constant.Constants.RANK;
import static cazimir.com.bancuribune.constant.Constants.RANK_NAME;
import static cazimir.com.bancuribune.constant.Constants.RECHIN;
import static cazimir.com.bancuribune.constant.Constants.REMAINING;
import static cazimir.com.bancuribune.constant.Constants.REMAINING_ADDS;
import static cazimir.com.bancuribune.constant.Constants.REMINDER_INTERVAL_CHECK;
import static cazimir.com.bancuribune.constant.Constants.REVIEW_REQUESTED;
import static cazimir.com.bancuribune.constant.Constants.SESSION_SHOW;
import static cazimir.com.bancuribune.constant.Constants.SOMON;
import static cazimir.com.bancuribune.constant.Constants.STAR_RATING_THRESHOLD;
import static cazimir.com.bancuribune.constant.Constants.STIUCA;
import static cazimir.com.bancuribune.constant.Constants.TRELLO_ACCESS_TOKEN;
import static cazimir.com.bancuribune.constant.Constants.TRELLO_APP_KEY;
import static cazimir.com.bancuribune.constant.Constants.TRELLO_FEEDBACK_LIST;
import static cazimir.com.bancuribune.constant.Constants.TRELLO_JOKE_LIST;
import static cazimir.com.bancuribune.constant.Constants.USER_LOGOUT_REQ;
import static java.lang.Math.abs;

public class MainActivityView extends BaseBackActivity implements IMainActivityView {

    private static final String TAG = MainActivityView.class.getSimpleName();
    private MainPresenter mPresenter;
    private JokesAdapter adapter;
    private String sharedText;
    private SharedPreferences preferences;
    private SoundPool soundPool;
    private int soundID;
    boolean loaded = false;
    private boolean mDebug = false;
    private int mCurrentPositionNew = 0;
    private int mCurrentPositionMostVoted = 0;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    private boolean mFirstRunShowBanner = true;
    private Menu mMenu;
    private boolean mMostVotedSelected;

    @BindView(R.id.btnGetNewestJokes)
    BootstrapButton btnGetNewestJokes;
    @BindView(R.id.btnGetMostVotes)
    BootstrapButton btnGetMostVotes;
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
    @BindView(R.id.reports)
    FrameLayout reports;
    @BindView(R.id.progress_bar)
    ProgressBar progressMain;
    @BindView(R.id.fabActionButtons)
    LinearLayout fabActionButtons;
    @BindView(R.id.scrollToTop)
    FloatingActionButton fabScrollToTop;
    @BindView(R.id.adBannerLayout)
    LinearLayout adBannerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseTypeSingleton type = DatabaseTypeSingleton.getInstance();
        mPresenter = new MainPresenter(this, new AuthPresenter(this, FirebaseAuth.getInstance()), new JokesRepository(type.isDebug()));
        handlePushNotifications();
        onboardingNeeded();

        Log.d("Firebase", "token "+ FirebaseInstanceId.getInstance().getToken());
        //because when you logout the shared preferences containing the current rank is also deleted
        //we do not need to add user and rank to db if anonymous login because user cannot add or vote.
        if (!isAnonymousLogin()) {
            //if rank is in DB save instanceId to user table to send push notifications
            addRankAndUserToDB();
        }
        setSwipeRefreshListener();
        initializeRatingReminder();
        checkIfReminderToAddShouldBeShown();
        updateUIForAdmin();
        getAllJokesData(true, false, false);
        initializeLikeSound();
        showAds();

        btnGetNewestJokes.setOnCheckedChangedListener(new BootstrapButton.OnCheckedChangedListener() {
            @Override
            public void OnCheckedChanged(BootstrapButton bootstrapButton, boolean isChecked) {
                if (isChecked) {
                    mMostVotedSelected = false;
                    getAllJokesData(true, false, false);
                }
            }
        });

        btnGetMostVotes.setOnCheckedChangedListener(new BootstrapButton.OnCheckedChangedListener() {
            @Override
            public void OnCheckedChanged(BootstrapButton bootstrapButton, boolean isChecked) {
                if (isChecked) {
                    mMostVotedSelected = true;
                    getAllJokesData(true, false, true);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_view;
    }

    private void handlePushNotifications() {
        goToProfileActivityFromPush();
    }

    private void goToProfileActivityFromPush() {
        //we need to start Profile activity in both cases (joke approved and rank updated)
        if (checkIfExtraRegardsAvailable()) {

            if(getIntent().getStringExtra("regards").equals(REVIEW_REQUESTED)){
                redirectUserToPlayStore();
                //for all other cases joke approved, joke rejected and rank update
            }else{
                if(checkIfExtraJokeIdAvailable()){
                    startMyJokesActivity();
                }

            }

        } else {
            Log.d(TAG + " getExtras", "No extras received");
        }
    }

    private void redirectUserToPlayStore() {
        final Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=cazimir.com.bancuribune&hl=en_US");
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(MainActivityView.this, "Couldn't find PlayStore on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAds() {
        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);
        if (BuildConfig.DEBUG) {
            mAdView.setAdUnitId(Constants.AD_UNIT_ID_TEST);
        } else {
            mAdView.setAdUnitId(Constants.AD_UNIT_ID_PROD);
        }
        adBannerLayout.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private boolean checkIfExtraRegardsAvailable() {
        return (getIntent().getStringExtra("regards") != null);
    }

    private boolean checkIfExtraJokeIdAvailable() {
        return (getIntent().getStringExtra("jokeId") != null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return true;
    }

    @Override
    protected void setBackArrowColour() {
        final Drawable logo = getResources().getDrawable(R.mipmap.ic_launcher);
        getSupportActionBar().setHomeAsUpIndicator(logo);
    }

    private void initializeLikeSound() {
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });
        soundID = soundPool.load(this, R.raw.drop, 1);

    }

    private void checkIfReminderToAddShouldBeShown() {
        Date lastCheckDate = getLastCheckDateFromSharedPreferences();
        int daysApart = (int) ((lastCheckDate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24l));
        if (abs(daysApart) >= REMINDER_INTERVAL_CHECK) {
            mPresenter.checkNumberOfAddsLastWeek(lastCheckDate);
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
    public void playOnVotedAudio() {

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_SYSTEM);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        float volume = actualVolume / maxVolume;

        if (loaded) {
            soundPool.play(soundID, volume, volume, 1, 0, 1f);
            Log.e("Test", "Played sound");
        }
    }

    private String getCurrentRankNameFromSharedPreferences() {
        preferences = getSharedPreferences(RANK, MODE_PRIVATE);
        Timber.tag(Constants.TIMBER_SHARED_PREFERENCES).i("Current rank from shared preferences is: %s",
                preferences.getString(RANK_NAME, null));
        return preferences.getString(RANK_NAME, null);
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

    private class TrelloObject {
        private String text;
        private boolean isJoke;

        TrelloObject(String text, boolean isJoke) {
            this.text = text;
            this.isJoke = isJoke;
        }

        public String getText() {
            return text;
        }

        public boolean isJoke() {
            return isJoke;
        }
    }

    private void sendFeedbackToTrello(final String feedback) {

        new sendToTrello(this).execute(new TrelloObject(feedback, false));
    }

    @Override
    public void sendJokeToTrello(String joke) {
        new sendToTrello(this).execute(new TrelloObject(joke, true));
    }

    private static class sendToTrello extends AsyncTask<TrelloObject, Integer, Card> {

        private WeakReference<MainActivityView> activityReference;

        Trello trelloApi = new TrelloImpl(TRELLO_APP_KEY, TRELLO_ACCESS_TOKEN);

        sendToTrello(MainActivityView context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Card doInBackground(TrelloObject... params) {
            Card card = new Card();
            card.setName(params[0].getText());
            card.setDesc(activityReference.get().mPresenter.getAuthPresenter().getCurrentUserName());
            if (params[0].isJoke()) {
                return trelloApi.createCard(TRELLO_JOKE_LIST, card);
            }
            return trelloApi.createCard(TRELLO_FEEDBACK_LIST, card);
        }

        @Override
        protected void onPostExecute(Card result) {
            super.onPostExecute(result);
            if (result.getIdList().equals(TRELLO_FEEDBACK_LIST)) {
                activityReference.get().showToast(activityReference.get().getString(R.string.feedback_sent));
            }
        }
    }

    //if first run then no rank data or user data is in DB so add it
    private void onboardingNeeded() {
        if (isFirstRun()) {
            startTutorialActivity();
        }
    }

    private boolean isAnonymousLogin() {
        return getIntent().getBooleanExtra(Constants.ANONYMOUS_LOGIN, false);
    }

    @Override
    public void addUserToDatabase(final OnAddUserToDatabaseCallback callback) {
        mPresenter.addUserToDatabase(mPresenter.getAuthPresenter().getCurrentUserID(),
                mPresenter.getAuthPresenter().getCurrentUserName());

        mPresenter.getAuthPresenter().saveInstanceIdToUserObject(new OnSaveInstanceIdToUserObjectCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailed(String error) {
                callback.onError(error);
            }
        });
    }

    private void addRankAndUserToDB() {
        mPresenter.checkAndAddRankToDB();
    }

    private void startTutorialActivity() {
        startActivity(new Intent(this, TutorialActivityView.class));
    }

    private void setSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isInternetAvailable()) {
                    getAllJokesData(true, true, mMostVotedSelected);
                } else {
                    showAlertDialog(getString(R.string.no_internet), SweetAlertDialog.ERROR_TYPE);
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });
    }

    /**
     * @return true if it is a fresh install of the application in order to show the user the tutorial screen.
     */
    private boolean isFirstRun() {

        Boolean mFirstRun;
        String currentUserID = mPresenter.getAuthPresenter().getCurrentUserID();

        SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
        mFirstRun = mPreferences.getBoolean(currentUserID, true);
        if (mFirstRun) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(currentUserID, false);
            editor.apply();
            return true;
        }

        return false;
    }

    public void refreshJokesListAdapter() {
        setOnScrollListener((LinearLayoutManager) initRecycleView());
        adapter = new JokesAdapter(new OnJokeClickListener() {
            @Override
            public void onJokeShared(final Joke joke) {
                logEvent(EVENT_SHARED, null);
                showToast(getString(R.string.share_open));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shareJoke(joke.getJokeText());
                    }
                }, 500);
            }

            @Override
            public void onJokeVoted(Joke joke, int position) {

                disableHeartIcon(position);
                animateHeartIcon(position);
                playOnVotedAudio();

                //if not logged in anonymously
                if (!mPresenter.isLoggedInAnonymously()) {
                    mPresenter.checkIfAlreadyVoted(joke, position);
                } else {
                    startLoginActivity();
                }
            }

            @Override
            public void onJokeExpanded() {
                logEvent(EVENT_JOKE_EXPANDED);
            }

            @Override
            public void onJokeUnlike(Joke joke, int position) {

            }

            @Override
            public void onJokeModified(String uid, String jokeText) {
                mPresenter.approveJoke(uid, jokeText);
            }
        }, mPresenter.isAdmin());
        jokesListRecyclerView.setAdapter(adapter);
    }

    private void disableHeartIcon(int position) {
        getViewHolder(position).heart.setEnabled(false);
    }

    @Override
    public void enableHeartIcon(int position) {
        getViewHolder(position).heart.setEnabled(true);
    }

    @Override
    public void showAlertDialog(String message, int type) {
        getAlertDialog().show(message, type);
    }

    @Override
    public void getAllJokesData(boolean reset, boolean swipe, boolean mostVoted) {
        mPresenter.getAllJokesData(reset, swipe, mostVoted);
    }

    private RecyclerView.LayoutManager initRecycleView() {
        jokesListRecyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    private void setOnScrollListener(final LinearLayoutManager layoutManager) {
        jokesListRecyclerView.addOnScrollListener(new ScrollListenerRecycleView(layoutManager) {

            @Override
            public void onScrollFinished() {
                if(mMostVotedSelected){
                    mCurrentPositionMostVoted = layoutManager.findFirstVisibleItemPosition();
                    Log.i(TAG, String.format("onScrollFinished: mCurrentPositionMostVoted: %s", String.valueOf(mCurrentPositionMostVoted)));
                }else{
                    mCurrentPositionNew = layoutManager.findFirstVisibleItemPosition();
                    Log.i(TAG, String.format("onScrollFinished: mCurrentPositionNew: %s", String.valueOf(mCurrentPositionNew)));
                }

            }

            @Override
            public void show() {
                fabActionButtons.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                fabScrollToTop.animate().translationX(0).setInterpolator(new DecelerateInterpolator(2)).start();
                hideAdBanner();

            }

            @Override
            public void hide() {
                fabActionButtons.animate().translationY(fabActionButtons.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
                fabScrollToTop.animate().translationX(fabActionButtons.getWidth()).setInterpolator(new AccelerateInterpolator(2)).start();
                showAdBanner();
            }

            @Override
            public void onLoadMore(int current_page) {
                getAllJokesData(false, false, false);

            }
        });
    }

    private void showAdBanner() {
        if (mFirstRunShowBanner) {
            adBannerLayout.setVisibility(View.VISIBLE);
            mFirstRunShowBanner = false;
        }

        adBannerLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    private void hideAdBanner() {
        adBannerLayout.animate().translationY(adBannerLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.app_name;
    }

    @Override
    public void displayJokes(List<Joke> jokes) {

        for (Joke joke : jokes) {
            adapter.add(joke);
        }

        adapter.notifyDataSetChanged();
        hideProgressBar();

        if(mMostVotedSelected){
            jokesListRecyclerView.scrollToPosition(mCurrentPositionMostVoted);
        }else{
            jokesListRecyclerView.scrollToPosition(mCurrentPositionNew);
        }

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

        if (!mPresenter.isLoggedInAnonymously()) {
            String currentRank = getCurrentRankNameFromSharedPreferences();
            if (isInternetAvailable()) {
                if (!mPresenter.isAdmin()) {

                    switch (currentRank) {
                        case HAMSIE:
                            mPresenter.checkNumberOfAdds(ADD_JOKE_LIMIT_HAMSIE);
                            break;
                        case HERING:
                            mPresenter.checkNumberOfAdds(ADD_JOKE_LIMIT_HERING);
                            break;
                        case SOMON:
                            mPresenter.checkNumberOfAdds(ADD_JOKE_LIMIT_SOMON);
                            break;
                        case STIUCA:
                            mPresenter.checkNumberOfAdds(ADD_JOKE_LIMIT_STIUCA);
                            break;
                        case RECHIN:
                            mPresenter.checkNumberOfAdds(ADD_JOKE_LIMIT_RECHIN);
                            break;
                    }

                } else {
                    navigateToAddJokeActivity();
                }
            }
        } else {
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivityView.class);
        i.putExtra("loginRequired", true);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.myJokesButtonFAB)
    public void startMyJokesActivity() {
        if (isInternetAvailable()) {
            if (!mPresenter.isLoggedInAnonymously()) {
                goToMyJokesActivity();
            } else {
                startLoginActivity();
            }

        }
    }

    public void goToMyJokesActivity() {
        Intent i = new Intent(this, MyJokesActivityView.class);
        i.putExtra("jokeId", getIntent().getStringExtra("jokeId"));
        startActivityForResult(i, USER_LOGOUT_REQ);
    }

    @Override
    public void writeToLog(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void logEvent(String event) {
        //from base class
        logEvent(event, null);
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnClick(R.id.adminFAB)
    public void startAdminJokesActivity() {
        if (isInternetAvailable()) {
            startActivityForResult(new Intent(this, AdminActivityView.class), LIKED_JOKES_REQ_CODE);
        }
    }

    @OnClick(R.id.reportsFAB)
    public void startReportsActivity() {
        if (isInternetAvailable()) {
            Intent i = new Intent(this, ReportActivityView.class);
            i.putExtra("app_version", BuildConfig.VERSION_NAME);
            startActivity(i);
        }
    }

    @OnClick(R.id.myLikedJokesButtonFAB)
    public void startMyLikedJokesActivity() {
        if (isInternetAvailable()) {
            if (!mPresenter.isLoggedInAnonymously()) {
                startActivityForResult(new Intent(this, LikedJokesActivityView.class), LIKED_JOKES_REQ_CODE);
            } else {
                startLoginActivity();
            }
        }
    }

    @OnClick(R.id.scrollToTop)
    public void scrollToTopOfList() {
        jokesListRecyclerView.scrollToPosition(0);
    }


    @Override
    public void navigateToAddJokeActivity() {
        Intent addJokeIntent = new Intent(this, AddJokeActivityView.class);
        addJokeIntent.putExtra(ADMIN, mPresenter.isAdmin());
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
                if (data.getStringExtra(Constants.JOKE_TEXT) != null) {
                    if(!BuildConfig.DEBUG){
                        sendJokeToTrello(data.getStringExtra(Constants.JOKE_TEXT));
                    }
                }

                showAddSuccessDialog();

                getAllJokesData(true, false, false);
            }
        } else if (requestCode == USER_LOGOUT_REQ) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }

        } else if (requestCode == LIKED_JOKES_REQ_CODE) {
            getAllJokesData(true, false, false);

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
    public void showAdminButtons() {
        admin.setVisibility(View.VISIBLE);
        reports.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        mMenu = menu;
        if (mPresenter.isAdmin()) {
            inflater.inflate(R.menu.db_switcher, menu);
            if (DatabaseTypeSingleton.getInstance().isDebug()) {
                mMenu.findItem(R.id.switchDB).setTitle("Switch to prod DB");
            } else {
                mMenu.findItem(R.id.switchDB).setTitle("Switch to test DB");

            }
            return true;
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.switchDB:
                DatabaseTypeSingleton type = DatabaseTypeSingleton.getInstance();
                type.setType(!type.isDebug());
                mPresenter = new MainPresenter(this, new AuthPresenter(this, FirebaseAuth.getInstance()), new JokesRepository(type.isDebug()));
                mPresenter.getAllJokesData(true, true, false);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateMenuTitles();
                    }
                }, 500);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateMenuTitles() {

        Boolean isDebug = DatabaseTypeSingleton.getInstance().isDebug();

        MenuItem switchDBItem = mMenu.findItem(R.id.switchDB);
        if (isDebug) {
            switchDBItem.setTitle("Switch to prod DB");
        } else {
            switchDBItem.setTitle("Switch to test DB");
        }
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
                Log.d(TAG, "Points on joke updated");
            }
        }, joke);
    }

    private void animateHeartIcon(final int index) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JokesAdapter.MyViewHolder holder = getViewHolder(index);
                final TextView heart = holder.heart;
                heart.setEnabled(false);

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
        }, 1);

    }

    private JokesAdapter.MyViewHolder getViewHolder(int index) {
        return (JokesAdapter.MyViewHolder) jokesListRecyclerView.findViewHolderForAdapterPosition(index);
    }

    @Override
    public void updateRemainingAdds(int remaining) {
        saveRemainingDataToSharedPreferences(remaining);
    }

    private void shareJoke(String text) {

        this.sharedText = text;

        if (writeStoragePermissionGranted()) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            int words = UtilHelper.countWords(text);
            if (mPresenter.isAdmin()) {
                Log.d(TAG, "shareJoke: " + "Admin user logged in");
                if (words <= Constants.MAX_JOKE_SIZE_PER_PAGE) {
                    Log.d(TAG, "shareJoke: " + "Bancul este mai mic de 45 de cuvinte. Are lungimea de: " + words + " de cuvinte");
                    Bitmap bitmap = UtilHelper.drawMultilineTextToBitmap(this, R.drawable.share_background, text);
                    String bitmapPath = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "title", "description");
                    Uri bitmapUri = Uri.parse(bitmapPath);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                    sendIntent.setType("image/*");
                    startActivity(Intent.createChooser(sendIntent, "Share"));
                } else {
                    Log.d(TAG, "shareJoke: " + "Bancul este prea lung. Alege altul mai scurt");
                    Toast.makeText(this, "Bancul este prea lung", Toast.LENGTH_SHORT).show();
                }
            } else {
                sendIntent.putExtra(Intent.EXTRA_TEXT, text + "\n\n" + getString(R.string.share_text));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        }
    }

    private boolean writeStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_STORAGE_REQ_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_STORAGE_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareJoke(sharedText);
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }
    }

    @Override
    public void updateUIForAdmin() {
        mPresenter.showAdminButtonsIfAdmin();
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }
}
