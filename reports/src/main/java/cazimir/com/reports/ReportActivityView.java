package cazimir.com.reports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.interfaces.reporting.IReportActivityView;
import cazimir.com.models.Rank;

public class ReportActivityView extends AppCompatActivity implements IReportActivityView {

    private static final String TAG = ReportActivityView.class.getSimpleName();
    ReportPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_report);
        mPresenter = new ReportPresenter(this);
        getTotalNumberOfJokes();
        getUsersWithMostPoints();
    }

    @Override
    public void getTotalNumberOfJokes() {
        mPresenter.getTotalNumberOfJokes(this);
    }

    @Override
    public void getUsersWithMostPoints() {
        mPresenter.getUsersWithMostPoints(this);
    }

    @Override
    public void populateNumberOfJokesText(int number) {
        Log.d(TAG, "populateNumberOfJokesText size: " + number);
        TextView txtNumber = findViewById(R.id.txt_number_jokes);
        txtNumber.setText(String.format(getString(R.string.number_of_jokes), String.valueOf(number)));
    }

    @Override
    public void populateHighestPointsView(ArrayList<Rank> ranks) {

        TextView appVersion = findViewById(R.id.txt_application_version);
        appVersion.setText("Versiunea aplicației este: " + getIntent().getStringExtra("app_version"));

        TextView firstPlace = findViewById(R.id.txt_points_1);
        firstPlace.setText(String.format(getString(R.string.number_of_points), "Pe primul loc",
                ranks.get(4).getUserName(), String.valueOf(ranks.get(4).getTotalPoints())));
        TextView secondPlace = findViewById(R.id.txt_points_2);
        secondPlace.setText(String.format(getString(R.string.number_of_points), "Pe al doilea loc",
                ranks.get(3).getUserName(), String.valueOf(ranks.get(3).getTotalPoints())));
        TextView thirdPlace = findViewById(R.id.txt_points_3);
        thirdPlace.setText(String.format(getString(R.string.number_of_points), "Pe al treilea loc",
                ranks.get(2).getUserName(), String.valueOf(ranks.get(2).getTotalPoints())));
        TextView forthPlace = findViewById(R.id.txt_points_4);
        forthPlace.setText(String.format(getString(R.string.number_of_points), "Pe al patrulea loc",
                ranks.get(1).getUserName(), String.valueOf(ranks.get(1).getTotalPoints())));
        TextView fifthPlace = findViewById(R.id.txt_points_5);
        fifthPlace.setText(String.format(getString(R.string.number_of_points), "Pe al cincilea loc",
                ranks.get(0).getUserName(), String.valueOf(ranks.get(0).getTotalPoints())));

    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }
}