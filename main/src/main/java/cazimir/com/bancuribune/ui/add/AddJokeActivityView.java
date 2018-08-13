package cazimir.com.bancuribune.ui.add;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.presenter.common.CommonPresenter;
import cazimir.com.constants.Constants;
import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.interfaces.ui.add.IAddJokeActivityView;
import cazimir.com.models.Joke;
import cazimir.com.utils.UtilHelper;

import static cazimir.com.constants.Constants.EVENT_ADDED;

public class AddJokeActivityView extends BaseBackActivity implements IAddJokeActivityView {

    private CommonPresenter presenter;
    private Intent intent;

    @BindView(R.id.editNewJoke)
    EditText addJokeEdit;

    @BindView(R.id.addNewJokeButtonFAB)
    android.support.design.widget.FloatingActionButton addJokeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CommonPresenter(this);
        intent = this.getIntent();
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
    protected int getLayoutId() {
        return R.layout.activity_add_joke;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.add_joke;
    }

    @Override
    public boolean dataValid() {
        if(addJokeEdit.getText().toString().isEmpty()){
            onError();
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void sendDataToDatabase(Joke joke) {
        presenter.addJoke(joke, getIntent().getExtras().getBoolean(Constants.ADMIN));
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
        if(jokeText != null){
            intent.putExtra(Constants.JOKE_TEXT, jokeText);
        }
    }

    @OnClick(R.id.addNewJokeButtonFAB)
    public void addJoke(View view){
        if(dataValid()){
            if(UtilHelper.isInternetAvailable(this)){
                sendDataToDatabase(constructJokeObject());
                hideSoftInput(addJokeEdit);
            }else{
                getAlertDialog().show(getString(R.string.no_internet), SweetAlertDialog.ERROR_TYPE);
            }
        }
    }

    private void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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
