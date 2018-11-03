package cazimir.com.bancuribune.presenter.auth;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

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

import cazimir.com.bancuribune.callbacks.login.OnLoginWithFacebookCallback;
import cazimir.com.bancuribune.callbacks.login.OnCheckIfLoggedInCallback;
import cazimir.com.bancuribune.view.login.OnLoginWithEmailFinishedListener;
import cazimir.com.bancuribune.callbacks.register.OnRegistrationFinishedListener;
import cazimir.com.bancuribune.view.forgotPassword.OnResendVerificationEmailListener;
import cazimir.com.bancuribune.view.forgotPassword.OnResetPasswordListener;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.view.login.ILoginActivityView;
import cazimir.com.bancuribune.view.profile.IMyJokesActivityView;

public class AuthPresenter implements IAuthPresenter {

    private static final String TAG = AuthPresenter.class.getSimpleName();
    private FirebaseAuth mAuth;
    private IGeneralView mView;
    private FirebaseUser currentUser;

    public AuthPresenter(IGeneralView view) {
        mAuth = FirebaseAuth.getInstance();
        this.mView = view;
    }

    @Override
    public void login(final OnLoginWithEmailFinishedListener listener, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                listener.onLoginWithEmailSuccess();
                            } else {
                                listener.onLoginWithEmailFailed("Te rog să îți verifici mailul. Ți-am trimis un link de confirmare.");
                            }
                        } else {
                            listener.onLoginWithEmailFailed(task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public void registerUser(final OnRegistrationFinishedListener listener, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    listener.onRegistrationFailed(task.getException().getMessage());
                } else {
                    final FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && !user.isEmailVerified()) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            listener.onRegistrationSuccess("Ți-am trimis un email de verificare la " + user.getEmail());
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
    public FacebookCallback<LoginResult> loginWithFacebook(final OnLoginWithFacebookCallback callback) {

        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken(), callback);
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
    public void checkIfUserLoggedIn(OnCheckIfLoggedInCallback callback) {
        if (isLoggedInViaEmail() || isLoggedInViaFacebook()) {
            callback.isLoggedIn();
        }
    }

    private boolean isLoggedInViaEmail() {
        Boolean result = mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified();
        Log.d(TAG, "isLoggedInViaEmail: " + result);
        return result;
    }

    @Override
    public boolean isLoggedInViaFacebook() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Boolean result = accessToken != null;
        Log.d(TAG, "isLoggedInViaFacebook: " + String.valueOf(result));
        return result;
    }


    @Override
    public String getCurrentUserID() {

        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }
        return "";
    }

    @Override
    public String getCurrentUserName() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getEmail();
        }

        return "";
    }

    @Override
    public String getCurrentUserEmail() {
        return mAuth.getCurrentUser().getEmail();
    }

    @Override
    public void logUserOut(IMyJokesActivityView view) {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        view.redirectToLoginPage();
        view.clearSharedPreferences();
    }

    private void handleFacebookAccessToken(AccessToken accessToken, final OnLoginWithFacebookCallback callback) {

        final ILoginActivityView login = (ILoginActivityView) this.mView.getInstance();
        Activity context = login.getContext();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                            // Sign in success, update UI with the signed-in user's information

                        } else {
                            callback.onFailed(task.getException().toString());
                            // If sign in fails, display a message to the user.
                        }
                    }
                });
    }

    public void performPasswordReset(final OnResetPasswordListener listener, String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onResetPasswordSuccess("Ți-am trimis un mail cu instrucțiunile de resetare");
                        } else {
                            listener.onResetPasswordFailed(task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public void performResendVerificationEmail(final OnResendVerificationEmailListener listener, final String email, final String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            listener.onResendEmailSuccess("Ți-am retrimis emailul de verificare la: " + email);
                                        } else {
                                            listener.onResendEmailFailed(task.getException().getMessage());
                                        }
                                    }
                                });
                            }
                        } else {
                            listener.onResendEmailFailed(task.getException().getMessage());
                        }
                    }
                });
    }
}
