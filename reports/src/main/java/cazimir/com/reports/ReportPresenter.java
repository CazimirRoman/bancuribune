package cazimir.com.reports;

import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.interfaces.reporting.IMainReportActivity;
import cazimir.com.interfaces.reporting.IReportPresenter;
import cazimir.com.interfaces.reporting.OnGetTotalNumberOfJokesCompleted;
import cazimir.com.repository.JokesRepository;

public class ReportPresenter implements IReportPresenter {

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

    private IMainReportActivity getMainReportActivityView() {
        return (IMainReportActivity) this.mView.getInstance();
    }
}