package cazimir.com.bancuribune;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import cazimir.com.bancuribune.presenter.authentication.AuthPresenter;
import cazimir.com.bancuribune.presenter.common.CommonPresenter;
import cazimir.com.bancuribune.ui.list.MainActivityView;
import cazimir.com.interfaces.repository.OnCheckIfRankDataInDBListener;
import cazimir.com.models.Rank;
import cazimir.com.repository.JokesRepository;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TODO: Add a class header comment!
 */
public class CommonPresenterTest {

    String userId = "userId";

    private CommonPresenter mCommonPresenter;

    @Mock
    private MainActivityView mMainActivityView;

    @Mock
    private JokesRepository mJokesRepository;

    @Mock
    private AuthPresenter mAuthPresenter;

    @Captor
    private ArgumentCaptor<OnCheckIfRankDataInDBListener> mOnCheckIfRankDataInDBListener;


    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        when(mAuthPresenter.getCurrentUserID()).thenReturn(userId);
        mCommonPresenter = new CommonPresenter(mMainActivityView, mAuthPresenter, mJokesRepository);
    }

    @Test
    public void checkAndGetMyRank() {

        String userId = "userId";
        Rank rank = new Rank();

        mCommonPresenter.checkAndGetMyRank();

        verify(mJokesRepository).checkIfRankDataInDB(mOnCheckIfRankDataInDBListener.capture(), eq(userId));
        mOnCheckIfRankDataInDBListener.getValue().rankDataIsInDB(rank);

        InOrder inOrder = Mockito.inOrder(mMainActivityView);
        inOrder.verify(mMainActivityView).checkIfNewRank(rank.getRank());
        inOrder.verify(mMainActivityView).saveRankDataToSharedPreferences(rank);
        inOrder.verify(mMainActivityView).checkIfAdmin();
    }

    @Test
    public void addRankToDatabase() {
    }

    @Test
    public void updateRankPointsAndName() {
    }
}