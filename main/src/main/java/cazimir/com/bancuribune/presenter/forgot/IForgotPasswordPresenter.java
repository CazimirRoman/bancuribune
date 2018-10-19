package cazimir.com.bancuribune.presenter.forgot;

/**
 * TODO: Add a class header comment!
 */
public interface IForgotPasswordPresenter {
    void sendResetInstructions(String email);
    void resendVerificationEmail(String email, String password);
}
