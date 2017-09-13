package cazimir.com.bancuribune.ui.add;

import android.os.Bundle;

import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.ui.base.BaseActivity;
import cazimir.com.bancuribune.ui.main.JokesPresenter;

public class AddJokeActivityView extends BaseActivity implements IAddJokeActivity {

    private JokesPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_joke;
    }

    @Override
    public void onSuccess() {
        presenter.getAllJokesData();

    }

    @Override
    public void onError() {

    }
}
