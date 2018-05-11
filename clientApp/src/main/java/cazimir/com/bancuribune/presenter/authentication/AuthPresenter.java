package cazimir.com.bancuribune.presenter.authentication;


import android.app.Activity;
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

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.ui.login.ILoginActivityView;
import cazimir.com.bancuribune.ui.myJokes.IMyJokesActivityView;

public class AuthPresenter implements IAuthPresenter {

    private FirebaseAuth auth;
    private IGeneralView view;

    public AuthPresenter(IGeneralView view) {
        auth = FirebaseAuth.getInstance();
        this.view = view;
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
                    listener.onRegistrationFailed(task.getException().getMessage());
                } else {
                    final FirebaseUser user = auth.getCurrentUser();
                    if (user != null && !user.isEmailVerified()) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            listener.onRegistrationSuccess("Ti-am trimis un email de verificare la " + user.getEmail());
                                        } else {
                                            listener.onRegistrationFailed("Nu am putut trimite mailul de verificare");
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
            ILoginActivityView view = (ILoginActivityView) this.view.getInstance();
            view.launchMainActivity();
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
    public String getCurrrentUserEmail() {
        return auth.getCurrentUser().getEmail();
    }

    @Override
    public void logUserOut(IMyJokesActivityView view) {
        auth.signOut();
        LoginManager.getInstance().logOut();
        view.redirectToLoginPage();
        view.clearSharedPreferences();
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {

        final ILoginActivityView login = (ILoginActivityView) this.view.getInstance();
        Activity context = login.getContext();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            login.loginSuccess();

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
                            listener.onResetPasswordSuccess("Ti-am trimis un mail cu instructiunile de resetare");
                        } else {
                            listener.onResetPasswordFailed(task.getException().getMessage());
                        }
                    }
                });
    }
}
