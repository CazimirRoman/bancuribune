package cazimir.com.bancuribune.presenter;


import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cazimir.com.bancuribune.ui.add.IAddJokeActivityView;
import cazimir.com.bancuribune.ui.admin.IAdminActivityView;
import cazimir.com.bancuribune.ui.forgotPassword.IForgotPasswordActivityView;
import cazimir.com.bancuribune.ui.list.IMainActivityView;
import cazimir.com.bancuribune.ui.login.ILoginActivityView;
import cazimir.com.bancuribune.ui.myjokes.IMyJokesActivityView;
import cazimir.com.bancuribune.ui.register.IRegisterActivityView;

public class AuthPresenter implements IAuthPresenter {

    private FirebaseAuth auth;
    private IRegisterActivityView register;
    private IForgotPasswordActivityView forgot;
    private ILoginActivityView login;
    private IMainActivityView jokes;
    private IAddJokeActivityView add;
    private IMyJokesActivityView myJokes;
    private IAdminActivityView adminView;

    public AuthPresenter(IRegisterActivityView view) {
        auth = FirebaseAuth.getInstance();
        this.register = view;
    }

    public AuthPresenter(ILoginActivityView view) {
        auth = FirebaseAuth.getInstance();
        this.login = view;
    }

    public AuthPresenter(IMainActivityView view) {
        auth = FirebaseAuth.getInstance();
        this.jokes = view;
    }

    public AuthPresenter(IAddJokeActivityView view) {
        auth = FirebaseAuth.getInstance();
        this.add = view;
    }

    public AuthPresenter(IMyJokesActivityView view) {
        auth = FirebaseAuth.getInstance();
        this.myJokes = view;
    }

    public AuthPresenter(IAdminActivityView view) {
        auth = FirebaseAuth.getInstance();
        this.adminView = view;
    }

    public AuthPresenter(IForgotPasswordActivityView view) {
        auth = FirebaseAuth.getInstance();
        this.forgot = view;
    }

    @Override
    public void login(final OnLoginWithEmailFinishedListener listener, String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            listener.onLoginWithEmailFailed(task.getException().getMessage());
                        } else {
                            if (auth.getCurrentUser().isEmailVerified()) {
                                listener.onLoginWithEmailSuccess();
                            } else {
                                listener.onLoginWithEmailFailed("Please verify your email to login");

                            }
                        }
                    }
                });
    }

    @Override
    public void registerUser(final OnRegistrationFinishedListener listener, String email, String password) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    listener.onRegistrationFailed(task.getException().getMessage().toString());
                } else {
                    final FirebaseUser user = auth.getCurrentUser();
                    if (user != null && !user.isEmailVerified()) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            listener.onRegistrationSuccess("Verification email sent to " + user.getEmail());
                                        } else {
                                            listener.onRegistrationFailed("Failed to send verification email.");
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    public FacebookCallback<LoginResult> loginWithFacebook() {

        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
            }
        };

    }

    @Override
    public void checkIfUserLoggedIn() {
        if (auth.getCurrentUser() != null) {
            login.launchMainActivity();
        }
    }

    @Override
    public String getCurrentUserID() {

        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return "";
    }

    @Override
    public String getCurrentUserName() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getEmail();
        }

        return "";
    }

    @Override
    public void logUserOut(IMainActivityView view) {
        auth.signOut();
        LoginManager.getInstance().logOut();
        view.redirectToLoginPage();
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(login.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            login.loginSucces();

                        } else {
                            // If sign in fails, display a message to the user.
                            login.loginFailed(task.getException().toString());
                        }

                    }
                });
    }

    public void performPasswordReset(final OnResetPasswordListener listener, String email){
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onResetPasswordSuccess("We have sent you instructions to reset your password!");
                        } else {
                            listener.onResetPasswordFailed(task.getException().getMessage());
                        }
                    }
                });
    }
}
