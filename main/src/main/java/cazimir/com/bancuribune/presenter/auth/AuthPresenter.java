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
import java.util.function.ToDoubleBiFunction;

import cazimir.com.bancuribune.constant.Constants;
import cazimir.com.bancuribune.model.User;
import cazimir.com.bancuribune.presenter.login.OnAnonymousLoginCallback;
import cazimir.com.bancuribune.repository.DatabaseTypeSingleton;
import cazimir.com.bancuribune.view.list.OnSaveInstanceIdToUserObjectCallback;
import cazimir.com.bancuribune.view.login.OnLoginWithEmailCallback;
import cazimir.com.bancuribune.view.register.OnRegistrationCallback;
import cazimir.com.bancuribune.view.forgotPassword.OnResendVerificationEmailListener;
import cazimir.com.bancuribune.view.forgotPassword.OnResetPasswordListener;
import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.callbacks.login.ILoginActivityView;
import cazimir.com.bancuribune.callbacks.myJokes.IMyJokesActivityView;
import timber.log.Timber;

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
    public void login(final OnLoginWithEmailCallback callback, String email, String password) {
        Timber.i("Trying to log in with email...");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                Timber.i("Email is verified. Success!");
                                callback.onSuccess();
                            } else {
                                Timber.i("User did not verify his/her email address");
                                callback.onFailed("Te rog să îți verifici mailul. Ți-am trimis un link de confirmare.");
                            }
                        } else {
                            Timber.e(task.getException().getMessage());
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
                            Timber.i("Signed in anonymously failed! Reason: %s", task.getException().getMessage());
                            listener.onFailed(task.getException().getMessage());

                        }
                    }
                });
    }

    @Override
    public FacebookCallback<LoginResult> loginWithFacebook() {
        Timber.i("Trying to login with Facebook...");

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
    public void registerUser(final OnRegistrationCallback callback, String email, String password) {
        Timber.i("Trying to register user...");
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
    public void checkIfUserLoggedIn() {
        Timber.i("Checking if user is logged in...");
        //this implementation is for cases where the instanceId is not saved yet
        final ILoginActivityView view = (ILoginActivityView) mView.getInstance();
        view.hideViewsAndButtons();
        view.showProgress();

        if (isLoggedInViaEmail() || isLoggedInViaFacebook()) {
            saveInstanceIdToUserObject(new OnSaveInstanceIdToUserObjectCallback() {
                @Override
                public void onSuccess() {
                    view.launchMainActivity();
                    view.hideProgress();
                }

                @Override
                public void onFailed(String error) {
                    view.showToast(error);
                }
            });
            //not logged in
        } else {
            view.showViewsAndButtons();
            view.hideProgress();
        }


    }

    /**
     * This method saves the instance id for the user to the database so we can use it when sending push notifications
     * @param callback
     */
    @Override
    public void saveInstanceIdToUserObject(final OnSaveInstanceIdToUserObjectCallback callback) {

        final String instanceId = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        DatabaseTypeSingleton type = DatabaseTypeSingleton.getInstance();
        final DatabaseReference usersRef;

        if(!type.isDebug()){
            usersRef = mDatabase.getReference("/users");
        }else{
            usersRef = mDatabase.getReference("/_dev/users_dev");
        }

        //if first login there is no user in the database until you get to the tutorial screen.
        if (instanceId != null) {
            Query query = usersRef.orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot usersSnapshot : dataSnapshot.getChildren()) {
                        User user = usersSnapshot.getValue(User.class);
                        usersRef.child(usersSnapshot.getKey()).child("instanceId").setValue(instanceId).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Timber.i("Saved instance id: %s to database", instanceId);
                                callback.onSuccess();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onFailed(databaseError.getMessage());
                }
            });
        }
    }

    private boolean isLoggedInViaEmail() {
        Boolean result = mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified();
        Timber.i("The user is logged in via email: " + result);
        return result;
    }

    @Override
    public boolean isLoggedInViaFacebook() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Boolean result = accessToken != null;
        Timber.i("The user is logged in via Facebook: " + result);
        return result;
    }


    @Override
    public String getCurrentUserID() {

        // TODO: 23-Dec-18 Throw exception if getCurrentUser return null

        if (mAuth.getCurrentUser() != null) {
            String currentUserId = mAuth.getCurrentUser().getUid();
            Timber.i("Got current user id: %s", currentUserId);
            return currentUserId;
        }
        return "";
    }

    @Override
    public String getCurrentUserName() {
        if (mAuth.getCurrentUser() != null) {
            String currentUserEmail = mAuth.getCurrentUser().getEmail();
            Timber.i("Got current user email: %s", currentUserEmail);
            return currentUserEmail;
        }

        return "";
    }

    @Override
    public void logUserOut(IMyJokesActivityView view) {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        view.redirectToLoginPage();
        view.clearSharedPreferences();
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {

        final ILoginActivityView login = (ILoginActivityView) this.mView.getInstance();
        Activity context = login.getContext();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            login.loginSuccess();
                            login.hideProgress();
                            Timber.i("Facebook login success!");
                            saveInstanceIdToUserObject(new OnSaveInstanceIdToUserObjectCallback() {
                                @Override
                                public void onSuccess() {
                                    Timber.i("Saved instance id to database after facebook " +
                                            "login");
                                }

                                @Override
                                public void onFailed(String error) {
                                    Timber.e("Something went wrong when saving instance id " +
                                            "to db after facebook login: %s", error);
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.e("Login with facebook failed! Reason: %s", task.getException().toString());
                            login.loginFailed(task.getException().toString());
                            login.hideProgress();
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

    @Override
    public boolean isAdmin() {
        List<String> list = Arrays.asList(Constants.ADMINS);
        return list.contains(getCurrentUserID());
    }

    @Override
    public boolean isLoggedInAnonymously() {
        return mAuth.getCurrentUser().isAnonymous();
    }

}
