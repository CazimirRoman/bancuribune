package cazimir.com.interfaces.ui.reporting;

import java.util.ArrayList;

import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.models.Rank;

public interface IReportActivityView extends IGeneralView {
    void getTotalNumberOfJokes();
    void getUsersWithMostPoints();
    void populateNumberOfJokesText(int number);
    void populateHighestPointsView(ArrayList<Rank> ranks);
}