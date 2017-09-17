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

import cazimir.com.bancuribune.ui.login.ILoginActivityView;

public class LoginPresenter implements ILoginPresenter {

    private FirebaseAuth auth;
    private ILoginActivityView view;

    public LoginPresenter(ILoginActivityView view) {
        auth = FirebaseAuth.getInstance();
        this.view = view;
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
            view.launchMainActivity();
        }
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(view.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            view.loginSucces();

                        } else {
                            // If sign in fails, display a message to the user.
                            view.loginFailed(task.getException().toString());
                        }

                    }
                });
    }
}
