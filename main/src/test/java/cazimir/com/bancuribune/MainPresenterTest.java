package cazimir.com.bancuribune;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import cazimir.com.bancuribune.callbacks.admin.OnJokeApprovedListener;
import cazimir.com.bancuribune.presenter.auth.AuthPresenter;
import cazimir.com.bancuribune.presenter.main.MainPresenter;
import cazimir.com.bancuribune.repository.JokesRepository;
import cazimir.com.bancuribune.repository.OnShowReminderToAddListener;
import cazimir.com.bancuribune.view.list.MainActivityView;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    private static final String NO_ADMIN_ID = "123456";
    private static final String ADMIN_ID = "IINgcJQYhrar9QBi2qcojfRqely2";
    private static final String USER_ID = "xxxxxxxxxxxxxxxxx";
    private static final String JOKE_TEXT = "This is a joke";
    private static final String APROBAT_TEXT = "aprobat";
    private static final String ERROR_TEXT = "error text";

    private MainPresenter mMainPresenter;

    @Mock
    private MainActivityView mMainActivityView;

    @Mock
    private JokesRepository mJokesRepository;

    @Mock
    private AuthPresenter mAuthPresenter;

    @Captor
    private ArgumentCaptor<OnShowReminderToAddListener> mOnShowReminderToAddListenerArgumentCaptor;

    @Captor
    private ArgumentCaptor<OnJokeApprovedListener> mOnJokeApprovedListenerArgumentCaptor;


    @Before
    public void setUp() {

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
        verify(mMainActivityView).showAdminButtons();
    }

    @Test
    public void shouldRefreshJokesListIfJokeTextSuccesfullySavedToDatabase(){
        mMainPresenter.approveJoke(USER_ID, JOKE_TEXT);
        verify(mJokesRepository).approveJoke(mOnJokeApprovedListenerArgumentCaptor.capture(), eq(USER_ID), eq(JOKE_TEXT));
        mOnJokeApprovedListenerArgumentCaptor.getValue().onJokeApprovedSuccess(APROBAT_TEXT);
        InOrder inOrder = Mockito.inOrder(mMainActivityView);
        inOrder.verify(mMainActivityView).showToast(APROBAT_TEXT);
        inOrder.verify(mMainActivityView).getAllJokesData(true, false);
        inOrder.verify(mMainActivityView).hideKeyboard();
    }

    @Test
    public void shouldShowErrorToastIfJokeTextFailedToSaveToDatabase(){
        mMainPresenter.approveJoke(USER_ID, JOKE_TEXT);
        verify(mJokesRepository).approveJoke(mOnJokeApprovedListenerArgumentCaptor.capture(), eq(USER_ID), eq(JOKE_TEXT));
        mOnJokeApprovedListenerArgumentCaptor.getValue().onJokeApprovedFailed(ERROR_TEXT);
        verify(mMainActivityView).showToast(eq(ERROR_TEXT));
    }
}