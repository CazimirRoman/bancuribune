package cazimir.com.bancuribune.callbacks.reporting;

import cazimir.com.bancuribune.base.IGeneralView;

public interface IReportPresenter {
    void getTotalNumberOfJokes(IGeneralView view);
    void getUsersWithMostPoints(IGeneralView view);
    void getAllRanks();
}