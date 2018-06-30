package cazimir.com.reports;

import java.util.ArrayList;

import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.interfaces.reporting.IReportActivityView;
import cazimir.com.interfaces.reporting.IReportPresenter;
import cazimir.com.interfaces.reporting.OnGetTotalNumberOfJokesCompleted;
import cazimir.com.interfaces.reporting.OnGetUsersWithMostPointsCompleted;
import cazimir.com.models.Rank;
import cazimir.com.repository.JokesRepository;

public class ReportPresenter implements IReportPresenter {

    public static final String TAG = ReportPresenter.class.getSimpleName();

    JokesRepository mRepository;
    private IGeneralView mView;

    public ReportPresenter(IGeneralView view) {
        mView = view;
        mRepository = new JokesRepository();
    }

    @Override
    public void getTotalNumberOfJokes(IGeneralView view) {
        mRepository.getTotalNumberOfJokes(new OnGetTotalNumberOfJokesCompleted() {
            @Override
            public void onSuccess(int size) {
                getMainReportActivityView().populateNumberOfJokesText(size);
            }

            @Override
            public void onFailed(String error) {

            }
        });
    }

    @Override
    public void getUsersWithMostPoints(IGeneralView view) {
        mRepository.getUsersWithMostPoints(new OnGetUsersWithMostPointsCompleted() {
            @Override
            public void onSuccess(ArrayList<Rank> ranks) {
                getMainReportActivityView().populateHighestPointsView(ranks);
            }

            @Override
            public void onFailed(String error) {

            }
        });
    }

    private IReportActivityView getMainReportActivityView() {
        return (IReportActivityView) this.mView.getInstance();
    }
}