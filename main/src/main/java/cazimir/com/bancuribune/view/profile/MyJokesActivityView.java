package cazimir.com.bancuribune.view.profile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import cazimir.com.bancuribune.BuildConfig;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.callbacks.list.OnJokeClickListener;
import cazimir.com.bancuribune.callbacks.myJokes.IMyJokesActivityView;
import cazimir.com.bancuribune.callbacks.myJokes.OnCalculatePointsListener;
import cazimir.com.bancuribune.callbacks.myJokes.OnGetFacebookNameListener;
import cazimir.com.bancuribune.constant.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.auth.AuthPresenter;
import cazimir.com.bancuribune.presenter.profile.IMyJokesPresenter;
import cazimir.com.bancuribune.presenter.profile.MyJokesPresenter;
import cazimir.com.bancuribune.repository.DatabaseTypeSingleton;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.utils.EmptyRecyclerView;
import cazimir.com.bancuribune.utils.UtilHelper;
import cazimir.com.bancuribune.view.login.LoginActivityView;

import static cazimir.com.bancuribune.constant.Constants.EVENT_SHARED;
import static cazimir.com.bancuribune.constant.Constants.MY_STORAGE_REQ_CODE;

public class MyJokesActivityView extends BaseBackActivity implements IMyJokesActivityView {

    private final static String TAG = MyJokesActivityView.class.getSimpleName();
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.btnGetMostVotes)
    BootstrapButton btnGetMostVotes;
    private IMyJokesPresenter mPresenter;
    private MyJokesAdapter adapter;

    @BindView(R.id.adBannerLayout)
    LinearLayout adBannerLayout;
    @BindView(R.id.btnGetNewestJokes)
    BootstrapButton btnGetNewestJokes;
    @BindView(R.id.profileName)
    TextView profileName;
    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.myJokesList)
    EmptyRecyclerView myJokesListRecyclerView;
    @BindView(R.id.profilePoints)
    TextView profilePoints;
    @BindView(R.id.profileRank)
    TextView profileRank;
    @BindView(R.id.nextRank)
    TextView profileNextRank;
    private boolean maxRangReached = false;
    private String sharedText;
    private boolean mMostVoted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseTypeSingleton type = DatabaseTypeSingleton.getInstance();
        mPresenter = new MyJokesPresenter(this, new AuthPresenter(this), new JokesRepository(type.isDebug()));
        initRecycleView();
        getProfilePictureAndName();
        getMyJokes(false);
        showAds();

        btnGetNewestJokes.setOnCheckedChangedListener(new BootstrapButton.OnCheckedChangedListener() {
            @Override
            public void OnCheckedChanged(BootstrapButton bootstrapButton, boolean isChecked) {
                if (isChecked) {
                    refreshJokesList(false);
                }
            }
        });

        btnGetMostVotes.setOnCheckedChangedListener(new BootstrapButton.OnCheckedChangedListener() {
            @Override
            public void OnCheckedChanged(BootstrapButton bootstrapButton, boolean isChecked) {
                if (isChecked) {
                    refreshJokesList(true);
                }
            }
        });

    }

    private void refreshJokesList(Boolean mostVoted) {
        myJokesListRecyclerView.setAdapter(constructAndGetNewAdapter());
        showProgressBarForNewestMostVoted();
        getMyJokes(mostVoted);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_jokes_view;
    }

    @Override
    public void scrollToJokeIfFromPushNotification() {

        //don't scroll to joke if most voted jokes are shown
        if(mMostVoted){
            return;
        }

        if (getIntent().getStringExtra("jokeId") != null) {
            String jokeId = getIntent().getStringExtra("jokeId");
            myJokesListRecyclerView.scrollToPosition(getJokePositionFromId(jokeId));
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

    private int getJokePositionFromId(String jokeId) {
        return adapter.getItemPositionFromJokeId(jokeId);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                mPresenter.logUserOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showAlertDialog(String message, int type) {
        getAlertDialog().show(message, type);
    }

    @Override
    public void showProgressBarForNewestMostVoted() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBarForNewestMostVotedButton() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setMostPoints(boolean mostPoints) {
        mMostVoted = mostPoints;
    }

    private void getProfilePictureAndName() {
        try {
            getProfilePictureFromFacebook();
            getProfileNameFromFacebook();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getProfileNameFromFacebook() {
        mPresenter.getProfileName(new OnGetFacebookNameListener() {
            @Override
            public void onGetFacebookNameSuccess(String name) {
                profileName.setText(name);
            }

            @Override
            public void onGetFacebookNameFailed() {
                showAlertDialog("Nu am putut obtine numele tau de pe facebook", SweetAlertDialog.ERROR_TYPE);
            }
        });
    }

    private void getProfilePictureFromFacebook() throws IOException {
        mPresenter.getFacebookProfilePicture(new OnGetProfilePictureListener() {
            @Override
            public void onGetProfilePictureSuccess(URL url) {
                Picasso.get().load(String.valueOf(url)).into(profileImage);
            }

            @Override
            public void onGetProfilePictureFailed() {

            }
        });
    }

    private void getMyJokes(boolean mostPoints) {
        mPresenter.getMyJokes(mostPoints);
    }

    private void initRecycleView() {
        EmptyRecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        myJokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = constructAndGetNewAdapter();
        myJokesListRecyclerView.setAdapter(adapter);
        myJokesListRecyclerView.setEmptyView(findViewById(R.id.empty_view));
    }

    private MyJokesAdapter constructAndGetNewAdapter() {
        return new MyJokesAdapter(new OnJokeClickListener() {
            @Override
            public void onJokeShared(final Joke data) {
                logEvent(EVENT_SHARED, null);
                showToast(getString(R.string.share_open));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shareJoke(data.getJokeText());
                    }
                }, 500);
            }

            @Override
            public void onJokeVoted(Joke joke, int position) {

            }

            @Override
            public void onJokeExpanded() {

            }

            @Override
            public void onJokeUnlike(Joke joke, int position) {

            }

            @Override
            public void onJokeModified(String uid, String jokeText) {

            }
        });
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
    protected int setActionBarTitle() {
        return R.string.my_jokes_title;
    }


    @Override
    public void showMyJokesList(List<Joke> myJokes) {

        if (myJokes.isEmpty()) {
            findViewById(R.id.no_jokes_added).setVisibility(View.VISIBLE);
        }

        adapter = new MyJokesAdapter(new OnJokeClickListener() {
            @Override
            public void onJokeShared(final Joke data) {
                logEvent(EVENT_SHARED, null);
                showToast(getString(R.string.share_open));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shareJoke(data.getJokeText());
                    }
                }, 500);
            }

            @Override
            public void onJokeVoted(Joke joke, int position) {

            }

            @Override
            public void onJokeExpanded() {

            }

            @Override
            public void onJokeUnlike(Joke joke, int position) {

            }

            @Override
            public void onJokeModified(String uid, String jokeText) {

            }
        });
        myJokesListRecyclerView.setAdapter(adapter);
        for (Joke joke : myJokes) {
            adapter.add(joke);
        }

        //add all points for each joke the user has
        mPresenter.calculateTotalPoints(new OnCalculatePointsListener() {
            @Override
            public void onCalculateSuccess(int points) {
                updateRankAndPoints(points);
            }
        }, myJokes);

        adapter.notifyDataSetChanged();
    }

    @Override
    public String getRankDataFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(Constants.RANK, MODE_PRIVATE);
        String rankId = prefs.getString(Constants.RANK, null);
        if (rankId != null) {
            return rankId;
        }

        return "";
    }

    @Override
    public void showToast(String message) {
        buildToast(message).show();
    }

    @Override
    public void redirectToLoginPage() {
        setResult(RESULT_OK);
        finish();
        startActivity(new Intent(this, LoginActivityView.class));
    }

    @Override
    public void clearSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(Constants.RANK, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    private void updateRankAndPoints(int points) {
        String rankId = getRankDataFromSharedPreferences();
        String rankName = computeRankName(points);
        Integer likesForNextRank = 0;
        if (!maxRangReached) {
            likesForNextRank = computeLikeForNextRank(points);
        }

        mPresenter.updateRankPointsAndName(points, rankName, rankId);

        profilePoints.setText(String.format(getString(R.string.total_points), String.valueOf(points)));
        profileRank.setText(String.format(getString(R.string.rankLabel), rankName));
        profileNextRank.setText(String.format(getString(R.string.nextRank), String.valueOf(likesForNextRank)));
    }

    //TODO: move to presenter
    private Integer computeLikeForNextRank(int points) {

        if (Constants.LOWER_LIMIT_HAMSIE <= points && points < Constants.UPPER_LIMIT_HAMSIE) {
            return Constants.UPPER_LIMIT_HAMSIE - points;
        } else if (Constants.UPPER_LIMIT_HAMSIE <= points && points < Constants.UPPER_LIMIT_HERING) {
            return Constants.UPPER_LIMIT_HERING - points;
        } else if (Constants.UPPER_LIMIT_HERING <= points && points < Constants.UPPER_LIMIT_SOMON) {
            return Constants.UPPER_LIMIT_SOMON - points;
        } else if (Constants.UPPER_LIMIT_SOMON <= points && points < Constants.UPPER_LIMIT_STIUCA) {
            return Constants.UPPER_LIMIT_STIUCA - points;
        } else if (Constants.UPPER_LIMIT_STIUCA <= points && points < Constants.UPPER_LIMIT_RECHIN) {
            hideProfileNextRankText();
            maxRangReached = true;
            return Constants.UPPER_LIMIT_RECHIN - points;
        }

        return 0;
    }

    private void hideProfileNextRankText() {
        profileNextRank.setVisibility(View.GONE);
    }

    //TODO: move to presenter
    private String computeRankName(int points) {
        if (Constants.LOWER_LIMIT_HAMSIE <= points && points < Constants.UPPER_LIMIT_HAMSIE) {
            return Constants.HAMSIE;
        } else if (Constants.UPPER_LIMIT_HAMSIE <= points && points < Constants.UPPER_LIMIT_HERING) {
            return Constants.HERING;
        } else if (Constants.UPPER_LIMIT_HERING <= points && points < Constants.UPPER_LIMIT_SOMON) {
            return Constants.SOMON;
        } else if (Constants.UPPER_LIMIT_SOMON <= points && points < Constants.UPPER_LIMIT_STIUCA) {
            return Constants.STIUCA;
        } else if (Constants.UPPER_LIMIT_STIUCA <= points && points < Constants.UPPER_LIMIT_RECHIN) {
            return Constants.RECHIN;
        }
        // last rank is rechin. if user has more than 1000 points just stay on current rank
        return Constants.RECHIN;
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }
}
