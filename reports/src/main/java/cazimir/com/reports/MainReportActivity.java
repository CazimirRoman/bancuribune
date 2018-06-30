package cazimir.com.reports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.interfaces.reporting.IMainReportActivity;

public class MainReportActivity extends AppCompatActivity implements IMainReportActivity {

    private static final String TAG = MainReportActivity.class.getSimpleName();
    ReportPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_report);
        mPresenter = new ReportPresenter(this);
        getTotalNumberOfJokes();
    }

    @Override
    public void getTotalNumberOfJokes() {
        mPresenter.getTotalNumberOfJokes(this);
    }

    @Override
    public void populateNumberOfJokesText(int number) {
        Log.d(TAG, "populateNumberOfJokesText size: " + number);
        TextView txtNumber = findViewById(R.id.txt_number_jokes);
        txtNumber.setText(String.format(getString(R.string.number_of_jokes), String.valueOf(number)));
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }
}