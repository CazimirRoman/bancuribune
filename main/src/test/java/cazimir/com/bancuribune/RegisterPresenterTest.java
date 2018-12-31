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
import cazimir.com.bancuribune.presenter.register.RegisterPresenter;
import cazimir.com.bancuribune.view.register.OnRegistrationCallback;
import cazimir.com.bancuribune.view.register.RegisterActivityView;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * TODO: Add a class header comment!
 */
public class RegisterPresenterTest {
    
    private RegisterPresenter mRegisterPresenter;
    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "123456";
    private static final String REGISTRATION_FAILED = "Registration failed";
    private static final String REGISTRATION_SUCCEEDED = "Registration succeeded";

    @Mock
    private AuthPresenter mAuthPresenter;

    @Mock
    private RegisterActivityView mRegisterActivityView;

    @Captor
    private ArgumentCaptor<OnRegistrationCallback> mOnRegistrationCallbackArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mRegisterPresenter = new RegisterPresenter(mRegisterActivityView, mAuthPresenter);
    }

    @Test
    public void shouldRedirectToLoginPageIfRegisterSuccessful() {
        mRegisterPresenter.registerUser(EMAIL, PASSWORD);
        verify(mAuthPresenter).registerUser(mOnRegistrationCallbackArgumentCaptor.capture(), eq(EMAIL), eq(PASSWORD));
        mOnRegistrationCallbackArgumentCaptor.getValue().onRegistrationSuccess(REGISTRATION_SUCCEEDED);
        InOrder inOrder = Mockito.inOrder(mRegisterActivityView);
        inOrder.verify(mRegisterActivityView).showToast(REGISTRATION_SUCCEEDED);
        inOrder.verify(mRegisterActivityView).hideProgress();
        inOrder.verify(mRegisterActivityView).redirectToLogin();
    }

    @Test
    public void shouldShowErrorDialogIfRegisterFailed(){
        mRegisterPresenter.registerUser(EMAIL, PASSWORD);
        verify(mAuthPresenter).registerUser(mOnRegistrationCallbackArgumentCaptor.capture(), eq(EMAIL), eq(PASSWORD));
        mOnRegistrationCallbackArgumentCaptor.getValue().onRegistrationFailed(REGISTRATION_FAILED);
        InOrder inOrder = Mockito.inOrder(mRegisterActivityView);
        inOrder.verify(mRegisterActivityView).showAlertDialog(REGISTRATION_FAILED, SweetAlertDialog.ERROR_TYPE);
        inOrder.verify(mRegisterActivityView).hideProgress();
    }
}