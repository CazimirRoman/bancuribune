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

import cazimir.com.bancuribune.ui.add.IAddJokeActivityView;
import cazimir.com.bancuribune.ui.admin.IAdminActivityView;
import cazimir.com.bancuribune.ui.list.IMainActivityView;
import cazimir.com.bancuribune.ui.login.ILoginActivityView;
import cazimir.com.bancuribune.ui.myjokes.IMyJokesActivityView;

public class AuthPresenter implements IAuthPresenter {

    private FirebaseAuth auth;
    private ILoginActivityView login;
    private IMainActivityView jokes;
    private IAddJokeActivityView add;
    private IMyJokesActivityView myJokes;
    private IAdminActivityView adminView;

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
}
