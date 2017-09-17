package cazimir.com.bancuribune.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.model.Joke;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.ui.add.AddJokeActivityView;
import cazimir.com.bancuribune.utils.MyAlertDialog;

public class JokesActivityView extends BaseActivity implements IJokesActivityView {

    private MyAlertDialog alertDialog;
    private CommonPresenter presenter;
    @BindView(R.id.jokesList) RecyclerView jokesListRecyclerView;
    @BindView(R.id.addJokeButtonFAB) FloatingActionButton addJokeFAB;
    private JokesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alertDialog = new MyAlertDialog(this);
        initRecycleView();
        presenter = new CommonPresenter(this, new JokesRepository());
        presenter.getAllJokesData();
    }

    private void initRecycleView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        jokesListRecyclerView.setLayoutManager(layoutManager);
        adapter = new JokesAdapter();
        jokesListRecyclerView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_jokes_view;
    }

    @Override
    public void refreshJokes(List<Joke> jokes) {
        adapter = new JokesAdapter();
        jokesListRecyclerView.setAdapter(adapter);
        for(Joke joke : jokes) {
            adapter.add(joke);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void requestFailed(String error) {
        //TODO : refactor
        alertDialog.getAlertDialog().setMessage(error);
        if (!alertDialog.getAlertDialog().isShowing()) alertDialog.getAlertDialog().show();
    }

    @OnClick (R.id.addJokeButtonFAB)
    public void checkIfAllowedToAdd(){
        presenter.checkNumberOfAdds();
    }

    @Override
    public void navigateToAddJokeActivity(){
        Intent addJokeIntent = new Intent(this, AddJokeActivityView.class);
        startActivityForResult(addJokeIntent, Constants.ADD_JOKE_REQUEST);
    }

    @Override
    public void isNotAllowedToAdd() {
        showAlertDialog(R.string.add_limit_reached);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADD_JOKE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                showAddConfirmationDialog();
            }
        }
    }


    public void showAddConfirmationDialog() {
        showAlertDialog(R.string.add_confirmation);
    }

    private void showAlertDialog(int message){
        alertDialog.getAlertDialog().setMessage(getString(message));
        if (!alertDialog.getAlertDialog().isShowing()) alertDialog.getAlertDialog().show();
    }
}
