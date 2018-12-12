package cazimir.com.bancuribune.view.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.base.IGeneralView;
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
import cazimir.com.bancuribune.view.login.LoginActivityView;

public class MyJokesActivityView extends BaseBackActivity implements IMyJokesActivityView {

    @BindView(R.id.adView)
    AdView adView;
    private IMyJokesPresenter mPresenter;
    private MyJokesAdapter adapter;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseTypeSingleton type = DatabaseTypeSingleton.getInstance();
        mPresenter = new MyJokesPresenter(this, new AuthPresenter(this), new JokesRepository(type.isDebug()));
        initRecycleView();
        getProfilePictureAndName();
        getMyJokes();
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public void scrollToJokeIfFromPushNotification() {
        if(getIntent().getStringExtra("jokeId") != null){
            String jokeId = getIntent().getStringExtra("jokeId");
            myJokesListRecyclerView.scrollToPosition(getJokePositionFromId(jokeId));
        }
    }

    private int getJokePositionFromId(String jokeId) {
        return adapter.getItemPositionFromJokeId(jokeId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_jokes_view;
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

    private void getMyJokes() {
        mPresenter.getMyJokes();
    }

    private void initRecycleView() {
        EmptyRecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        myJokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new MyJokesAdapter();
        myJokesListRecyclerView.setAdapter(adapter);
        myJokesListRecyclerView.setEmptyView(findViewById(R.id.empty_view));
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

        adapter = new MyJokesAdapter();
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
