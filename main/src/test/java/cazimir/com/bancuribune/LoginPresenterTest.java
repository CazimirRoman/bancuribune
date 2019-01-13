package cazimir.com.bancuribune;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import cazimir.com.bancuribune.presenter.auth.AuthPresenter;
import cazimir.com.bancuribune.presenter.login.LoginPresenter;
import cazimir.com.bancuribune.presenter.login.OnCheckIfLoggedInCallback;
import cazimir.com.bancuribune.presenter.login.OnLoginWithFacebookCallback;
import cazimir.com.bancuribune.view.list.OnSaveInstanceIdToUserObjectCallback;
import cazimir.com.bancuribune.view.login.LoginActivityView;
import cazimir.com.bancuribune.view.login.OnLoginWithEmailCallback;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * TODO: Add a class header comment!
 */
public class LoginPresenterTest {

    private static final String SAVE_INSTANCE_ID_FAILED = "instance id not saved";
    private LoginPresenter mLoginPresenter;
    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "123456";
    private static final String LOGIN_WITH_EMAIL_FAILED = "Login failed!";
    private static final String LOGIN_WITH_FACEBOOK_FAILED = "login with facebook failed";

    @Mock
    private LoginActivityView mLoginActivityView;

    @Mock
    private AuthPresenter mAuthPresenter;

    @Captor
    private ArgumentCaptor<OnLoginWithEmailCallback> mOnLoginWithEmailCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<OnSaveInstanceIdToUserObjectCallback> mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<OnLoginWithFacebookCallback> mOnLoginWithFacebookCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<OnCheckIfLoggedInCallback> mOnCheckIfLoggedInCallbackArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mLoginPresenter = new LoginPresenter(mLoginActivityView, mAuthPresenter);
    }

    @Test
    public void shouldLaunchMainActivityIfLoginWithEmailSuccessful() {
        //try to login
        mLoginPresenter.performLogin(EMAIL, PASSWORD);
        verify(mAuthPresenter).login(mOnLoginWithEmailCallbackArgumentCaptor.capture(), eq(EMAIL), eq(PASSWORD));
        //return login success
        mOnLoginWithEmailCallbackArgumentCaptor.getValue().onSuccess();
        verify(mAuthPresenter).saveInstanceIdToUserObject(mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.capture());
        //saved instanceId to DB
        mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.getValue().onSuccess();
        //check if method to launch main activity is called
        InOrder inOrder = Mockito.inOrder(mLoginActivityView);
        inOrder.verify(mLoginActivityView).launchMainActivity();
        inOrder.verify(mLoginActivityView).hideProgress();
    }

    @Test
    public void shouldShowAlertDialogIfLoginWithEmailFailed(){
        //try to login
        mLoginPresenter.performLogin(EMAIL, PASSWORD);
        verify(mAuthPresenter).login(mOnLoginWithEmailCallbackArgumentCaptor.capture(), eq(EMAIL), eq(PASSWORD));
        //return login success
        mOnLoginWithEmailCallbackArgumentCaptor.getValue().onFailed(LOGIN_WITH_EMAIL_FAILED);
        InOrder inOrder = Mockito.inOrder(mLoginActivityView);
        inOrder.verify(mLoginActivityView).showAlertDialog(LOGIN_WITH_EMAIL_FAILED, SweetAlertDialog.ERROR_TYPE);
        inOrder.verify(mLoginActivityView).hideProgress();
    }

    /**
     * Each time the user logs in the instance id of the firebase session needs to be saved in order
     * for the push notification logic on the server to work (on joke approved, on rank updated etc).
     */
    @Test
    public void shouldSaveInstanceIdToDatabaseIfLoginWithEmailSuccessful() {
        mLoginPresenter.performLogin(EMAIL, PASSWORD);
        verify(mAuthPresenter).login(mOnLoginWithEmailCallbackArgumentCaptor.capture(), eq(EMAIL), eq(PASSWORD));
        mOnLoginWithEmailCallbackArgumentCaptor.getValue().onSuccess();
        verify(mAuthPresenter).saveInstanceIdToUserObject(mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.capture());
        mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.getValue().onSuccess();
        verify(mLoginActivityView).launchMainActivity();
    }

    /**
     * Saving the instance id failed after logging with email!
     */
    @Test
    public void shouldShowErrorToastIfSavingInstanceIdFailedWAfterLoggingInWithEmail() {
        mLoginPresenter.performLogin(EMAIL, PASSWORD);
        verify(mAuthPresenter).login(mOnLoginWithEmailCallbackArgumentCaptor.capture(), eq(EMAIL), eq(PASSWORD));
        mOnLoginWithEmailCallbackArgumentCaptor.getValue().onSuccess();
        verify(mAuthPresenter).saveInstanceIdToUserObject(mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.capture());
        mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.getValue().onFailed(SAVE_INSTANCE_ID_FAILED);
        InOrder inOrder = Mockito.inOrder(mLoginActivityView);
        inOrder.verify(mLoginActivityView).showToast(SAVE_INSTANCE_ID_FAILED);
        inOrder.verify(mLoginActivityView).hideProgress();
    }

    /**
     * Should test that after logging in to facebook the instance id is saved to the database
     */
    @Test
    public void shouldSaveInstanceIdToDatabaseIfLoginWithFacebookSuccessful() {
        mLoginPresenter.loginWithFacebook();
        verify(mAuthPresenter).loginWithFacebook(mOnLoginWithFacebookCallbackArgumentCaptor.capture());
        mOnLoginWithFacebookCallbackArgumentCaptor.getValue().onLoginWithFacebookSuccess();
        verify(mAuthPresenter).saveInstanceIdToUserObject(mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.capture());
    }

    /**
     * Saving the instance id failed after logging with email!
     */
    @Test
    public void shouldShowErrorToastIfSavingInstanceIdFailedAfterLoggingInWithFacebook() {
        mLoginPresenter.loginWithFacebook();
        verify(mAuthPresenter).loginWithFacebook(mOnLoginWithFacebookCallbackArgumentCaptor.capture());
        mOnLoginWithFacebookCallbackArgumentCaptor.getValue().onLoginWithFacebookSuccess();
        verify(mAuthPresenter).saveInstanceIdToUserObject(mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.capture());
        mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.getValue().onFailed(LOGIN_WITH_FACEBOOK_FAILED);
        InOrder inOrder = Mockito.inOrder(mLoginActivityView);
        inOrder.verify(mLoginActivityView).loginFailed(LOGIN_WITH_FACEBOOK_FAILED);
        inOrder.verify(mLoginActivityView).hideProgress();
    }

    /**
     * Saving the instance id failed after logging with email!
     */
    @Test
    public void shouldShowErrorToastAndHideProgressIfSavingInstanceIdFailed() {
        mLoginPresenter.performLogin(EMAIL, PASSWORD);
        verify(mAuthPresenter).login(mOnLoginWithEmailCallbackArgumentCaptor.capture(), eq(EMAIL), eq(PASSWORD));
        mOnLoginWithEmailCallbackArgumentCaptor.getValue().onSuccess();
        verify(mAuthPresenter).saveInstanceIdToUserObject(mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.capture());
        mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.getValue().onFailed(SAVE_INSTANCE_ID_FAILED);
        InOrder inOrder = Mockito.inOrder(mLoginActivityView);
        inOrder.verify(mLoginActivityView).showToast(SAVE_INSTANCE_ID_FAILED);
        inOrder.verify(mLoginActivityView).hideProgress();
    }

    /**
     * Each time the user is automatically logged in via the method {@link LoginPresenter#checkIfUserLoggedIn()}
     */
    @Test
    public void shouldSaveInstanceIdToDatabaseOnEachAutomaticLogin() {
        mLoginPresenter.checkIfUserLoggedIn();
        verify(mAuthPresenter).checkIfUserLoggedIn(mOnCheckIfLoggedInCallbackArgumentCaptor.capture());
        mOnCheckIfLoggedInCallbackArgumentCaptor.getValue().isLoggedIn();
        verify(mAuthPresenter).saveInstanceIdToUserObject(mOnSaveInstanceIdToUserObjectCallbackArgumentCaptor.capture());
    }

}