package cazimir.com.bancuribune.ui.add;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.base.BaseBackActivity;
import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.utils.MyAlertDialog;
import cazimir.com.bancuribune.utils.Utils;

public class AddJokeActivityView extends BaseBackActivity implements IAddJokeActivityView {

    private MyAlertDialog alertDialog;
    private CommonPresenter presenter;

    @BindView(R.id.editNewJoke)
    EditText addJokeEdit;

    @BindView(R.id.addNewJokeButtonFAB)
    android.support.design.widget.FloatingActionButton addJokeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertDialog = new MyAlertDialog(this);
        presenter = new CommonPresenter(this);
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
    public void onDataValidated() {
        presenter.getAllJokesData(true, false);
    }

    @Override
    public void sendDataToDatabase(Joke joke) {
        presenter.addJoke(joke, getIntent().getExtras().getBoolean(Constants.ADMIN));
    }

    @Override
    public void onError() {
        alertDialog.show(getString(R.string.add_joke_empty));
    }

    @Override
    public void closeAdd() {
        Intent intent = this.getIntent();
        this.setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.addNewJokeButtonFAB)
    public void addJoke(View view){
        if(dataValid()){
            if(Utils.isInternetAvailable(this)){
                sendDataToDatabase(constructJokeObject());
                hideSoftInput(addJokeEdit);
                closeAdd();
            }else{
                alertDialog.show(getString(R.string.no_internet));
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
}
