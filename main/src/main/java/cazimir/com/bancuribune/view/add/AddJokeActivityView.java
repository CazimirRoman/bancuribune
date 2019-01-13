package cazimir.com.bancuribune.view.add;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.BuildConfig;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.constant.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.add.AddJokePresenter;
import cazimir.com.bancuribune.presenter.auth.AuthPresenter;
import cazimir.com.bancuribune.repository.DatabaseTypeSingleton;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.utils.UtilHelper;

import static cazimir.com.bancuribune.constant.Constants.EVENT_ADDED;

public class AddJokeActivityView extends BaseBackActivity implements IAddJokeActivityView {

    @BindView(R.id.adBannerLayout)
    LinearLayout adBannerLayout;
    private AddJokePresenter mPresenter;
    private Intent intent;

    @BindView(R.id.editNewJoke)
    EditText addJokeEdit;
    @BindView(R.id.addNewJokeButtonFAB)
    FloatingActionButton addJokeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseTypeSingleton type = DatabaseTypeSingleton.getInstance();
        mPresenter = new AddJokePresenter(this, new AuthPresenter(this, FirebaseAuth.getInstance()), new JokesRepository(type.isDebug()));
        intent = this.getIntent();
        showAds();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_joke;
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
    protected int setActionBarTitle() {
        return R.string.add_joke;
    }

    @Override
    public boolean dataValid() {
        if (addJokeEdit.getText().toString().isEmpty()) {
            onError();
            return false;
        } else {
            return true;
        }
    }

    //get Intent extras (admin) to set joke to approved state if admin added it.
    @Override
    public void sendDataToDatabase(Joke joke) {
        mPresenter.addJoke(joke, intent.getExtras().getBoolean(Constants.ADMIN));
    }

    @Override
    public void onError() {
        getAlertDialog().show(getString(R.string.add_joke_empty), SweetAlertDialog.ERROR_TYPE);
    }

    @Override
    public void closeAdd() {
        this.setResult(RESULT_OK, intent);
        logEvent(EVENT_ADDED, null);
        finish();
    }

    @Override
    public void populateIntent(String jokeText) {
        if (jokeText != null) {
            intent.putExtra(Constants.JOKE_TEXT, jokeText);
        }
    }

    @OnClick(R.id.addNewJokeButtonFAB)
    public void addJoke(View view) {
        if (dataValid()) {
            if (UtilHelper.isInternetAvailable(this)) {
                addJokeButton.setEnabled(false);
                sendDataToDatabase(constructJokeObject());
                hideSoftInput(addJokeEdit);
            } else {
                getAlertDialog().show(getString(R.string.no_internet), SweetAlertDialog.ERROR_TYPE);
            }
        }
    }

    private void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private Joke constructJokeObject() {
        return new Joke(addJokeEdit.getText().toString().trim());
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }
}
