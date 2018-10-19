package cazimir.com.bancuribune.callbacks.reporting;

import java.util.ArrayList;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.model.Rank;

public interface IReportActivityView extends IGeneralView {
    void getTotalNumberOfJokes();
    void getUsersWithMostPoints();
    void populateNumberOfJokesText(int number);
    void populateHighestPointsView(ArrayList<Rank> ranks);
}