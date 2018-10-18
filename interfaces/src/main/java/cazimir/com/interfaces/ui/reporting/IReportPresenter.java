package cazimir.com.interfaces.ui.reporting;

import cazimir.com.interfaces.base.IGeneralView;

public interface IReportPresenter {
    void getTotalNumberOfJokes(IGeneralView view);
    void getUsersWithMostPoints(IGeneralView view);
}