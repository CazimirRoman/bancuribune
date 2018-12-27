package cazimir.com.bancuribune.presenter.auth;


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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.List;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.callbacks.login.ILoginActivityView;
import cazimir.com.bancuribune.callbacks.myJokes.IMyJokesActivityView;
import cazimir.com.bancuribune.constant.Constants;
import cazimir.com.bancuribune.model.User;
import cazimir.com.bancuribune.presenter.login.OnAnonymousLoginCallback;
import cazimir.com.bancuribune.presenter.login.OnCheckIfLoggedInCallback;
import cazimir.com.bancuribune.presenter.login.OnLoginWithFacebookCallback;
import cazimir.com.bancuribune.repository.DatabaseTypeSingleton;
import cazimir.com.bancuribune.view.forgotPassword.OnResendVerificationEmailListener;
import cazimir.com.bancuribune.view.forgotPassword.OnResetPasswordListener;
import cazimir.com.bancuribune.view.list.OnSaveInstanceIdToUserObjectCallback;
import cazimir.com.bancuribune.view.login.OnLoginWithEmailCallback;
import cazimir.com.bancuribune.view.register.OnRegistrationCallback;
import timber.log.Timber;

public class AuthPresenter implements IAuthPresenter {

    private FirebaseAuth mAuth;
    private IGeneralView mView;
    private FirebaseUser mCurrentUser;

    public AuthPresenter(IGeneralView view, FirebaseAuth auth) {
        mAuth = auth;
        mView = view;
    }

    @Override
    public void login(final OnLoginWithEmailCallback callback, final String email, String password) {
        Timber.i("Trying to log in with email: %s", email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mCurrentUser = mAuth.getCurrentUser();
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                Timber.i("Login success!");
                                callback.onSuccess();
                            } else {
                                Timber.i("User did not verify his/her email address: %s", email);
                                callback.onFailed("Te rog să îți verifici mailul. Ți-am trimis un link de confirmare.");
                            }
                        } else {
                            Timber.e("Could not log user with email %s in. Reason: %s", email, task.getException().getMessage());
                            callback.onFailed(task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public void loginAnonymously(final OnAnonymousLoginCallback listener) {
        Timber.i("Trying to login anonymously...");
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Timber.i("Signed in anonymously!");
                            listener.onSuccess();
                        } else {
                            Timber.i("Failed to sign in anonyously! Reason: %s", task.getException().getMessage());
                            listener.onFailed(task.getException().getMessage());

                        }
                    }
                });
    }

    @Override
    public FacebookCallback<LoginResult> loginWithFacebook(final OnLoginWithFacebookCallback onLoginWithFacebookCallback) {
        Timber.i("Trying to login with Facebook...");

        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken(), onLoginWithFacebookCallback);
            }

            @Override
            public void onCancel() {
                Timber.e("Facebook login canceled!");
            }

            @Override
            public void onError(FacebookException error) {
                Timber.e(error);
            }
        };

    }

    @Override
    public void registerUser(final OnRegistrationCallback callback, String email, String password) {
        Timber.i("Trying to register user with email: %s", email);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && !user.isEmailVerified()) {
                        Timber.i("User registered. Sending email verification...");
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Timber.i("Verification email sent to: %s", user.getEmail());
                                            callback.onRegistrationSuccess("Ți-am trimis un email de verificare la " + user.getEmail());
                                        } else {
                                            Timber.e("Verification email could not be sent. Reason: %s", task.getException().getMessage());
                                            callback.onRegistrationFailed("Nu am putut trimite mailul de verificare");
                                        }
                                    }
                                });
                    }

                } else {
                    Timber.e("User registration failed. Reason: %s", task.getException().getMessage());
                    callback.onRegistrationFailed(task.getException().getMessage());
                }
            }
        });
    }

    @Override
    public void checkIfUserLoggedIn(OnCheckIfLoggedInCallback callback) {
        Timber.i("Checking if user is logged in...");
        //this implementation is for cases where the instanceId is not saved yet
        final ILoginActivityView view = (ILoginActivityView) mView.getInstance();
        view.hideViewsAndButtons();
        view.showProgress();

        if (isLoggedInViaEmail() || isLoggedInViaFacebook()) {

            callback.isLoggedIn();

            //not logged in. show login views and buttons. that means showing the whole scrollview which contains them.
        } else {
            Timber.i("User is NOT logged in");
            callback.isNotLoggedIn();
        }
    }

    /**
     * This method saves the instance id for the user to the database so we can use it when sending push notifications
     *
     * @param callback
     */
    @Override
    public void saveInstanceIdToUserObject(final OnSaveInstanceIdToUserObjectCallback callback) {

        final String instanceId = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        DatabaseTypeSingleton type = DatabaseTypeSingleton.getInstance();
        final DatabaseReference usersRef;

        if (!type.isDebug()) {
            usersRef = mDatabase.getReference("/users");
        } else {
            usersRef = mDatabase.getReference("/_dev/users_dev");
        }

        //if first login there is no user in the database until you get to the tutorial screen.
        if (instanceId != null) {
            Query query = usersRef.orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                        final User user = usersSnapshot.getValue(User.class);
                        usersRef.child(usersSnapshot.getKey()).child("instanceId").setValue(instanceId).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Timber.i("Saved instance id for user %s: %s", user.getName(), instanceId);
                                callback.onSuccess();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Timber.i("Could not save instance id to database. Reason: %s", databaseError.getMessage());
                    callback.onFailed(databaseError.getMessage());
                }
            });
        }
    }

    private boolean isLoggedInViaEmail() {
        Boolean result = mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified();
        Timber.i("The user is logged in with email: %s", result);
        return result;
    }

    @Override
    public boolean isLoggedInViaFacebook() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Boolean result = accessToken != null;
        Timber.i("The user is logged in with Facebook: %s", result);
        return result;
    }


    @Override
    public String getCurrentUserID() throws NullPointerException {

        String currentUserId = null;

        try {
            currentUserId = mAuth.getCurrentUser().getUid();
            Timber.i("Got current user id: %s", currentUserId);
            return currentUserId;
        } catch (NullPointerException e) {
            Timber.e("Get Current user; get user id returned null!");
            Timber.e(e);
        }

        return currentUserId;
    }

    @Override
    public String getCurrentUserName() {
        // TODO: 27.12.2018 Refactor the same as above method
        if (mAuth.getCurrentUser() != null) {
            String currentUserEmail = mAuth.getCurrentUser().getEmail();
            Timber.i("Got current user email: %s", currentUserEmail);
            return currentUserEmail;
        }

        return "";
    }

    @Override
    public void logUserOut(IMyJokesActivityView view) {
        Timber.i("Logging user %s out", mAuth.getCurrentUser().getEmail());
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Timber.i("Logout success!");
        view.redirectToLoginPage();
        view.clearSharedPreferences();
    }

    private void handleFacebookAccessToken(AccessToken accessToken, final OnLoginWithFacebookCallback listenerFromLoginPresenter) {

        final ILoginActivityView login = (ILoginActivityView) this.mView.getInstance();
        Activity context = login.getContext();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.i("Facebook login success!");
                            listenerFromLoginPresenter.onLoginWithFacebookSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.e("Login with facebook failed! Reason: %s", task.getException().toString());
                            listenerFromLoginPresenter.onLoginWithFacebookFailed(task.getException().toString());
                        }

                    }
                });
    }

    public void performPasswordReset(final OnResetPasswordListener listener, final String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Timber.i("Password reset email sent to %s", email);
                            listener.onResetPasswordSuccess("Ți-am trimis un mail cu instrucțiunile de resetare");
                        } else {
                            Timber.e("Password reset email NOT sent to %s. Reason: %s", email, task.getException().getMessage());
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
                            mCurrentUser = mAuth.getCurrentUser();
                            if (mCurrentUser != null) {
                                mCurrentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Timber.i("Verification email resent to %s", email);
                                            listener.onResendEmailSuccess("Ți-am retrimis emailul de verificare la: " + email);
                                        } else {
                                            Timber.e("Verification email NOT sent to %s. Reason: %s", email, task.getException().getMessage());
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

    @Override
    public boolean isAdmin() {
        List<String> list = Arrays.asList(Constants.ADMINS);
        Boolean isAdmin = list.contains(getCurrentUserID());
        Timber.i("Is current user admin: %s", isAdmin);
        return isAdmin;
    }

    @Override
    public boolean isLoggedInAnonymously() {
        Boolean isLoggedInAnonymously = mAuth.getCurrentUser().isAnonymous();
        Timber.i("Logged in anonymously: %s", isLoggedInAnonymously);
        return isLoggedInAnonymously;
    }
}