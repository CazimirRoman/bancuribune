package cazimir.com.bancuribune.ui.add;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.repository.JokesRepository;

public class AddJokeActivityView extends BaseActivity implements IAddJokeActivityView {

    @BindView(R.id.editNewJoke)
    EditText addJokeEdit;

    @BindView(R.id.buttonAddNewJoke)
    Button addJokeButton;

    private CommonPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void closeAdd() {
        finish();
    }

    @OnClick(R.id.buttonAddNewJoke)
    public void addJoke(View view){
        if(dataValid()){
            sendDataToDatabase(constructJokeObject());
        }
    }

    private Joke constructJokeObject() {
        return new Joke(addJokeEdit.getText().toString().trim());
    }
}
