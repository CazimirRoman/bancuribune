package cazimir.com.interfaces.reporting;

import cazimir.com.interfaces.base.IGeneralView;

public interface IMainReportActivity extends IGeneralView {
    void getTotalNumberOfJokes();
    void populateNumberOfJokesText(int number);
}