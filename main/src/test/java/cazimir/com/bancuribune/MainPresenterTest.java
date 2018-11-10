package cazimir.com.bancuribune;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cazimir.com.bancuribune.presenter.auth.AuthPresenter;
import cazimir.com.bancuribune.presenter.main.MainPresenter;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.repository.OnAdminCheckCallback;
import cazimir.com.bancuribune.view.list.MainActivityView;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TODO: Add a class header comment!
 */
public class MainPresenterTest {

    private static final String NO_ADMIN_ID = "123456";
    private static final String ADMIN_ID = "IINgcJQYhrar9QBi2qcojfRqely2";
    String userId = "userId";

    private MainPresenter mMainPresenter;

    @Mock
    private MainActivityView mMainActivityView;

    @Mock
    private JokesRepository mJokesRepository;

    @Mock
    private AuthPresenter mAuthPresenter;

    @Captor
    private ArgumentCaptor<OnAdminCheckCallback> mOnAdminCheckCallbackArgumentCaptor;


    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        mMainPresenter = new MainPresenter(mMainActivityView, mAuthPresenter, mJokesRepository);
    }

    @Test
    public void shouldReturnTrueIfUserAdmin() {

        when(mAuthPresenter.getCurrentUserID()).thenReturn(ADMIN_ID);
        Boolean isAdmin = mMainPresenter.isAdmin();
        Assert.assertTrue(isAdmin);
    }

    @Test
    public void shouldReturnFalseIfUserNotAdmin() {

        when(mAuthPresenter.getCurrentUserID()).thenReturn(NO_ADMIN_ID);
        Boolean isAdmin = mMainPresenter.isAdmin();
        Assert.assertFalse(isAdmin);
    }

    @Test
    public void shouldShowAdminButtonsIfAdmin(){

        when(mAuthPresenter.getCurrentUserID()).thenReturn(ADMIN_ID);
        mMainPresenter.showAdminButtonsIfAdmin();
        verify(mJokesRepository).checkIfAdmin(mOnAdminCheckCallbackArgumentCaptor.capture(), eq(ADMIN_ID));
        mOnAdminCheckCallbackArgumentCaptor.getValue().onIsAdmin();
        verify(mMainActivityView).showAdminButtons();
    }

    @Test
    public void checkNumberOfAddsLastWeek(){
        
    }
}