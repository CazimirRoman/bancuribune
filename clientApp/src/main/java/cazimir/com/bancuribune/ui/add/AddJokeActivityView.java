package cazimir.com.bancuribune.ui.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.utils.MyAlertDialog;

public class AddJokeActivityView extends BaseActivity implements IAddJokeActivityView {

    private MyAlertDialog alertDialog;
    private CommonPresenter presenter;

    @BindView(R.id.editNewJoke)
    EditText addJokeEdit;

    @BindView(R.id.addNewJokeButtonFAB)
    android.support.design.widget.FloatingActionButton addJokeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.add_joke));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        alertDialog = new MyAlertDialog(this);
        presenter = new CommonPresenter(this, new JokesRepository());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_joke;
    }

    @Override
    public void showAlertDialog(String message) {
        alertDialog.getAlertDialog().setMessage(message);
        if (!alertDialog.getAlertDialog().isShowing()) alertDialog.getAlertDialog().show();
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
        presenter.getAllJokesData();
    }

    @Override
    public void sendDataToDatabase(Joke joke) {
        presenter.addJoke(joke);
    }

    @Override
    public void onError() {
        alertDialog.getAlertDialog().setMessage(getString(R.string.add_joke_empty));
        if (!alertDialog.getAlertDialog().isShowing()) alertDialog.getAlertDialog().show();
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
            sendDataToDatabase(constructJokeObject());
            hideSoftInput(addJokeEdit);
            closeAdd();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        hideSoftInput(addJokeEdit);
        return true;
    }
}
