package cazimir.com.bancuribune.report;

import java.util.ArrayList;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.callbacks.reporting.IReportActivityView;
import cazimir.com.bancuribune.callbacks.reporting.IReportPresenter;
import cazimir.com.bancuribune.callbacks.reporting.OnGetTotalNumberOfJokesCompleted;
import cazimir.com.bancuribune.callbacks.reporting.OnGetUsersWithMostPointsCompleted;
import cazimir.com.bancuribune.model.Rank;
import cazimir.com.bancuribune.repository.IJokesRepository;

public class ReportPresenter implements IReportPresenter {

    public static final String TAG = ReportPresenter.class.getSimpleName();

    private IGeneralView mView;
    private IJokesRepository mRepository;

    public ReportPresenter(IGeneralView view, IJokesRepository repository) {
        mView = view;
        mRepository = repository;
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

    @Override
    public void getAllRanks() {
        mRepository.getAllRanks(new OnGetAllRanksListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    private IReportActivityView getMainReportActivityView() {
        return (IReportActivityView) this.mView.getInstance();
    }
}