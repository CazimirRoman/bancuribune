package cazimir.com.bancuribune.ui.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

    @BindView(R.id.editNewJoke)
    EditText addJokeEdit;

    @BindView(R.id.buttonAddNewJoke)
    Button addJokeButton;

    private CommonPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Add joke");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alertDialog = new MyAlertDialog(this);
        presenter = new CommonPresenter(this, new JokesRepository());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_joke;
    }

    @Override
    public boolean dataValid() {
        if(addJokeEdit.getText().toString().isEmpty()){
            onError("Please fill out Joke field");
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
    public void onError(String error) {
        alertDialog.getAlertDialog().setMessage(error);
        if (!alertDialog.getAlertDialog().isShowing()) alertDialog.getAlertDialog().show();
    }

    @Override
    public void closeAdd() {
        Intent intent = this.getIntent();
        this.setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.buttonAddNewJoke)
    public void addJoke(View view){
        if(dataValid()){
            sendDataToDatabase(constructJokeObject());
            hideSoftInput();
        }
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(addJokeEdit.getWindowToken(), 0);
    }

    private Joke constructJokeObject() {
        return new Joke(addJokeEdit.getText().toString().trim());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
