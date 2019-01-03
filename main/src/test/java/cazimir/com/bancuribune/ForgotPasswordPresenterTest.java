package cazimir.com.bancuribune;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import cazimir.com.bancuribune.presenter.auth.AuthPresenter;
import cazimir.com.bancuribune.presenter.forgot.ForgotPasswordPresenter;
import cazimir.com.bancuribune.view.forgotPassword.ForgotPasswordActivityView;
import cazimir.com.bancuribune.view.forgotPassword.OnResendVerificationEmailListener;
import cazimir.com.bancuribune.view.forgotPassword.OnResetPasswordListener;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * TODO: Add a class header comment!
 */
public class ForgotPasswordPresenterTest {

    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "123456";
    private static final String RESENT_EMAIL_SUCCEEDED = "Email resend succeeded";
    private static final String RESEND_EMAIL_FAILED = "Email resend failed";
    private static final String RESET_EMAIL_SUCCEEDED = "Email reset succeeded";
    private static final String RESET_EMAIL_FAILED = "Email reset failed";

    private ForgotPasswordPresenter mForgotPasswordPresenter;

    @Mock
    private ForgotPasswordActivityView mForgotPasswordActivityView;

    @Mock
    private AuthPresenter mAuthPresenter;

    @Captor
    private ArgumentCaptor<OnResendVerificationEmailListener> mOnResendVerificationEmailListenerArgumentCaptor;

    @Captor
    private ArgumentCaptor<OnResetPasswordListener> mOnResetPasswordListenerArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mForgotPasswordPresenter = new ForgotPasswordPresenter(mForgotPasswordActivityView, mAuthPresenter);
    }

    @Test
    public void shouldRedirectToLoginIfActivationEmailResendSuccess() {
        mForgotPasswordPresenter.resendVerificationEmail(EMAIL, PASSWORD);
        verify(mAuthPresenter).performResendVerificationEmail(mOnResendVerificationEmailListenerArgumentCaptor.capture(), eq(EMAIL), eq(PASSWORD));
        mOnResendVerificationEmailListenerArgumentCaptor.getValue().onResendEmailSuccess(RESENT_EMAIL_SUCCEEDED);
        InOrder inOrder = Mockito.inOrder(mForgotPasswordActivityView);
        inOrder.verify(mForgotPasswordActivityView).showToast(RESENT_EMAIL_SUCCEEDED);
        inOrder.verify(mForgotPasswordActivityView).redirectToLogin();
        inOrder.verify(mForgotPasswordActivityView).hideProgress();
    }

    @Test
    public void shouldShowErrorToastIfActivationEmailResendFailed() {
        mForgotPasswordPresenter.resendVerificationEmail(EMAIL, PASSWORD);
        verify(mAuthPresenter).performResendVerificationEmail(mOnResendVerificationEmailListenerArgumentCaptor.capture(), eq(EMAIL), eq(PASSWORD));
        mOnResendVerificationEmailListenerArgumentCaptor.getValue().onResendEmailFailed(RESEND_EMAIL_FAILED);
        InOrder inOrder = Mockito.inOrder(mForgotPasswordActivityView);
        inOrder.verify(mForgotPasswordActivityView).hideProgress();
        inOrder.verify(mForgotPasswordActivityView).showToast(RESEND_EMAIL_FAILED);
    }

    @Test
    public void shouldShowToastAndRedirectToLoginIfPasswordResetSuccessful() {
        mForgotPasswordPresenter.sendResetInstructions(EMAIL);
        verify(mAuthPresenter).performPasswordReset(mOnResetPasswordListenerArgumentCaptor.capture(), eq(EMAIL));
        mOnResetPasswordListenerArgumentCaptor.getValue().onResetPasswordSuccess(RESET_EMAIL_SUCCEEDED);
        InOrder inOrder = Mockito.inOrder(mForgotPasswordActivityView);
        inOrder.verify(mForgotPasswordActivityView).showToast(RESET_EMAIL_SUCCEEDED);
        inOrder.verify(mForgotPasswordActivityView).redirectToLogin();
        inOrder.verify(mForgotPasswordActivityView).hideProgress();
    }

    @Test
    public void shouldShowToastAndHideProgressIfPasswordResetFailed() {
        mForgotPasswordPresenter.sendResetInstructions(EMAIL);
        verify(mAuthPresenter).performPasswordReset(mOnResetPasswordListenerArgumentCaptor.capture(), eq(EMAIL));
        mOnResetPasswordListenerArgumentCaptor.getValue().onResetPasswordFailed(RESET_EMAIL_FAILED);
        InOrder inOrder = Mockito.inOrder(mForgotPasswordActivityView);
        inOrder.verify(mForgotPasswordActivityView).showToast(RESET_EMAIL_FAILED);
        inOrder.verify(mForgotPasswordActivityView).hideProgress();
    }
}