package cazimir.com.bancuribune.presenter;


import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;

import cazimir.com.bancuribune.ui.add.IAddJokeActivityView;
import cazimir.com.bancuribune.ui.list.IJokesActivityView;
import cazimir.com.bancuribune.ui.login.ILoginActivityView;
import cazimir.com.bancuribune.ui.login.OnAuthStateListenerRegister;

public class AuthPresenter implements IAuthPresenter {

    private FirebaseAuth auth;
    private ILoginActivityView login;
    private IJokesActivityView jokes;
    private IAddJokeActivityView add;

    public AuthPresenter(ILoginActivityView view) {
        auth = FirebaseAuth.getInstance();
        setListener();
        this.login = view;
    }

    public AuthPresenter(IJokesActivityView view) {
        auth = FirebaseAuth.getInstance();
        this.jokes = view;
    }

    public AuthPresenter(IAddJokeActivityView view) {
        auth = FirebaseAuth.getInstance();
        this.add = view;
    }

    private void setListener() {
        auth.addAuthStateListener(new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               if(firebaseAuth.getCurrentUser() == null){

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

        if(auth.getCurrentUser() != null){
            return auth.getCurrentUser().getUid();
        }
        return "";
    }

    @Override
    public String getCurrentUserName() {
        if(auth.getCurrentUser() != null){
            return auth.getCurrentUser().getDisplayName();
        }

        return "";
    }

    @Override
    public void logUserOut() {
        auth.signOut();
    }

    @Override
    public void registerAuthChangeListener(final OnAuthStateListenerRegister listener) {
        auth.addAuthStateListener(new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                listener.onAuthListenerSuccess();
            }
        });
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
}
