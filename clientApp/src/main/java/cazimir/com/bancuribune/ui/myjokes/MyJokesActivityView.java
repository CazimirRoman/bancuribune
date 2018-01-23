package cazimir.com.bancuribune.ui.myjokes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.base.EmptyRecyclerView;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.presenter.OnGetProfilePictureListener;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.ui.list.OnJokeItemClickListener;


public class MyJokesActivityView extends BaseBackActivity implements IMyJokesActivityView, OnJokeItemClickListener, OnGetProfilePictureListener, OnCalculatePointsListener, OnGetFacebookNameListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRecycleView();
        getProfilePictureAndName();
        getMyJokes();
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
        getPresenter().getProfileName(this);
    }

    private void getProfilePictureFromFacebook() throws IOException {
        getPresenter().getFacebookProfilePicture(this);
    }

    private void getMyJokes() {
        getPresenter().getMyJokes();
    }

    private void initRecycleView() {
        EmptyRecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        myJokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new MyJokesAdapter();
        myJokesListRecyclerView.setAdapter(adapter);
        myJokesListRecyclerView.setEmptyView(findViewById(R.id.empty_view));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_jokes_view;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.my_jokes_title;
    }


    @Override
    public void showMyJokesList(List<Joke> jokes) {
        adapter = new MyJokesAdapter();
        myJokesListRecyclerView.setAdapter(adapter);
        for (Joke joke : jokes) {
            adapter.add(joke);
        }

        getPresenter().calculateTotalPoints(this, jokes);

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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemShared(Joke data) {

    }

    @Override
    public void onItemVoted(Joke joke) {

    }

    @Override
    public void OnGetProfilePictureSuccess(URL url) {
        Picasso.with(this).load(String.valueOf(url)).into(profileImage);
    }

    @Override
    public void OnGetProfilePictureFailed() {

    }

    @Override
    public void OnCalculateSuccess(int points) {
        updateRank(points);
    }

    private void updateRank(int points) {
        String rankId = getRankDataFromSharedPreferences();
        String rankName = computeRankName(points);
        Integer likesForNextRank = computeLikeForNextRank(points);
        getPresenter().updateRankPointsAndName(points, rankName, rankId);

        profilePoints.setText(String.format(getString(R.string.total_points), String.valueOf(points)));
        profileRank.setText(String.format(getString(R.string.rankLabel), rankName));
        profileNextRank.setText(String.format(getString(R.string.nextRank), String.valueOf(likesForNextRank)));
    }

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
            return Constants.UPPER_LIMIT_RECHIN - points;
        }
        return 0;
    }

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

        return Constants.DEFAULT;
    }

    @Override
    public void OnCalculateFailed(String error) {

    }

    @Override
    public void OnGetFacebookNameSuccess(String name) {
        profileName.setText(name);
    }

    @Override
    public void OnGetFacebookNameFailed() {

    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }
}
