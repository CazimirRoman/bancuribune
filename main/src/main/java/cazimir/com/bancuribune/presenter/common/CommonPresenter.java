package cazimir.com.bancuribune.presenter.common;

import android.util.Log;

import com.facebook.Profile;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import cazimir.com.interfaces.authentication.IAuthPresenter;
import cazimir.com.interfaces.authentication.OnLoginWithEmailFinishedListener;
import cazimir.com.interfaces.authentication.OnRegistrationFinishedListener;
import cazimir.com.interfaces.authentication.OnResendVerificationEmailListener;
import cazimir.com.interfaces.authentication.OnResetPasswordListener;
import cazimir.com.interfaces.base.IGeneralView;
import cazimir.com.interfaces.common.ICommonPresenter;
import cazimir.com.interfaces.common.OnGetProfilePictureListener;
import cazimir.com.interfaces.repository.IJokesRepository;
import cazimir.com.interfaces.repository.OnUpdateRankPointsSuccess;
import cazimir.com.interfaces.ui.add.IAddJokeActivityView;
import cazimir.com.interfaces.ui.add.OnAddJokeFinishedListener;
import cazimir.com.interfaces.ui.admin.IAdminActivityView;
import cazimir.com.interfaces.ui.admin.OnGetAllPendingJokesListener;
import cazimir.com.interfaces.ui.admin.OnJokeApprovedListener;
import cazimir.com.interfaces.ui.forgotPassword.IForgotPasswordActivityView;
import cazimir.com.interfaces.ui.likedJokes.ILikedJokesActivityView;
import cazimir.com.interfaces.ui.likedJokes.OnGetLikedJokesListener;
import cazimir.com.interfaces.ui.list.IMainActivityView;
import cazimir.com.interfaces.ui.login.ILoginActivityView;
import cazimir.com.interfaces.ui.myJokes.IMyJokesActivityView;
import cazimir.com.interfaces.ui.myJokes.OnCalculatePointsListener;
import cazimir.com.interfaces.ui.myJokes.OnGetFacebookNameListener;
import cazimir.com.interfaces.ui.myJokes.OnGetMyJokesListener;
import cazimir.com.interfaces.ui.register.IRegisterActivityView;
import cazimir.com.models.Joke;

public class CommonPresenter implements ICommonPresenter {

    private static final String TAG = "CommonPresenter";
    private IGeneralView view;
    private IJokesRepository repository;
    private IAuthPresenter authPresenter;

    public String getCurrentUserID() {
        return currentUserID;
    }

    private String currentUserID;

    public CommonPresenter(IGeneralView view, IAuthPresenter authPresenter, IJokesRepository jokesRepository) {
        this.view = view;
        this.authPresenter = authPresenter;
        repository = jokesRepository;
        setCurrentLoggedInUserId();
    }

    private void setCurrentLoggedInUserId() {
        currentUserID = authPresenter.getCurrentUserID();
    }

    @Override
    public void registerUser(String email, String password) {
        getRegisterActivityView().showProgress();
        authPresenter.registerUser(new OnRegistrationFinishedListener() {
            @Override
            public void onRegistrationSuccess(String message) {
                getRegisterActivityView().showToast(message);
                getRegisterActivityView().hideProgress();
                getRegisterActivityView().redirectToLogin();
            }

            @Override
            public void onRegistrationFailed(String error) {
                getRegisterActivityView().showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
                getRegisterActivityView().hideProgress();
            }
        }, email, password);
    }

    @Override
    public void getAllPendingJokesData() {
        repository.getAllPendingJokes(new OnGetAllPendingJokesListener() {
            @Override
            public void onGetAllPendingJokesSuccess(List<Joke> jokes) {
                getAdminActivityView().refreshJokes(jokes);
            }

            @Override
            public void onGetAllPendingJokesFailed(String message) {
                getAdminActivityView().showToast(message);
            }
        });
    }

    @Override
    public void getMyJokes() {
        repository.getMyJokes(new OnGetMyJokesListener() {
            @Override
            public void onGetMyJokesSuccess(List<Joke> jokes) {
                getMyJokesActivityView().showMyJokesList(jokes);
            }

            @Override
            public void onGetMyJokesError(String error) {
                getMyJokesActivityView().showToast(error);
            }
        }, currentUserID);
    }

    @Override
    public void getLikedJokes() {
        repository.getVotesForUser(new OnGetLikedJokesListener() {
            @Override
            public void onGetLikedJokesSuccess(Joke myLikedJoke) {
                getLikedJokesActivityView().showMyLikedJokesList(myLikedJoke);
            }

            @Override
            public void onGetLikedJokesFailed(String error) {
                getLikedJokesActivityView().showToast(error);
            }

            @Override
            public void onNoLikedJokes() {
                getLikedJokesActivityView().showNoLikedJokesText();
            }
        }, currentUserID);
    }

    @Override
    public void addJoke(final Joke joke, final Boolean isAdmin) {
        joke.setCreatedBy(currentUserID);
        joke.setUserName(authPresenter.getCurrentUserName());

        if (isAdmin) {
            joke.setApproved(true);
        }

        repository.addJoke(new OnAddJokeFinishedListener() {
            @Override
            public void onAddSuccess() {
                if (!isAdmin) {
                    getAddJokeActivityView().populateIntent(joke.getJokeText());
                }

                getAddJokeActivityView().closeAdd();

            }

            @Override
            public void onAddFailed() {
                getMainActivityView().showAddFailedDialog();
            }
        }, joke);
    }

    @Override
    public void logOutUser() {
        authPresenter.logUserOut(getMyJokesActivityView());
    }

    @Override
    public void approveJoke(String jokeUid) {
        repository.setApprovedStatusToTrue(new OnJokeApprovedListener() {
            @Override
            public void onJokeApprovedSuccess() {
                getAdminActivityView().showToast("Approved!");
                getAdminActivityView().getAllPendingJokes();
            }

            @Override
            public void onJokeApprovedFailed(String error) {
                getAdminActivityView().showToast(error);
            }
        }, jokeUid);
    }

    @Override
    public void getFacebookProfilePicture(final OnGetProfilePictureListener listener) throws IOException {
        if (isLoggedInFacebook()) {
            String id = Profile.getCurrentProfile().getId();
            final URL imageURL = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
            listener.onGetProfilePictureSuccess(imageURL);
        } else {
            listener.onGetProfilePictureFailed();
        }
    }

    @Override
    public void getProfileName(OnGetFacebookNameListener listener) {
        if (isLoggedInFacebook()) {
            String name = Profile.getCurrentProfile().getName();
            if (name != null) {
                listener.onGetFacebookNameSuccess(name);
            } else {
                listener.onGetFacebookNameFailed();
            }
        } else {
            listener.onGetFacebookNameSuccess(authPresenter.getCurrentUserName());
        }
    }

    private boolean isLoggedInFacebook() {
        return Profile.getCurrentProfile() != null;

    }

    @Override
    public void calculateTotalPoints(OnCalculatePointsListener listener, List<Joke> jokes) {
        int points = 0;
        for (Joke joke : jokes) {
            points = points + joke.getPoints();
        }

        listener.onCalculateSuccess(points);
    }

    @Override
    public void updateRankPointsAndName(int points, String rankName, String rankId) {
        repository.updateRankPointsAndName(new OnUpdateRankPointsSuccess() {
            @Override
            public void onUpdateRankPointsSuccess() {
                Log.d(TAG, "Rank points updated");
            }
        }, rankName, points, rankId);
    }

    @Override
    public void resendVerificationEmail(String email, String password) {
        getForgotPasswordActivityView().showProgress();
        authPresenter.performResendVerificationEmail(new OnResendVerificationEmailListener() {

            @Override
            public void onResendEmailSuccess(String message) {
                getForgotPasswordActivityView().showToast(message);
                getForgotPasswordActivityView().redirectToLogin();
                getForgotPasswordActivityView().hideProgress();
            }

            @Override
            public void onResendEmailFailed(String error) {
                getForgotPasswordActivityView().hideProgress();
                getForgotPasswordActivityView().showToast(error);
            }
        }, email, password);
    }

    @Override
    public void sendResetInstructions(String email) {
        getForgotPasswordActivityView().showProgress();
        authPresenter.performPasswordReset(new OnResetPasswordListener() {
            @Override
            public void onResetPasswordSuccess(String message) {
                getForgotPasswordActivityView().showToast(message);
                getForgotPasswordActivityView().redirectToLogin();
                getForgotPasswordActivityView().hideProgress();
            }

            @Override
            public void onResetPasswordFailed(String error) {
                getForgotPasswordActivityView().showToast(error);
                getForgotPasswordActivityView().hideProgress();
            }
        }, email);
    }

    @Override
    public void performLogin(String email, String password) {
        authPresenter.login(new OnLoginWithEmailFinishedListener() {
            @Override
            public void onLoginWithEmailSuccess() {
                ILoginActivityView login = (ILoginActivityView) view.getInstance();
                login.launchMainActivity();
                login.hideProgress();
            }

            @Override
            public void onLoginWithEmailFailed(String error) {
                getLoginActivityView().showAlertDialog(error, SweetAlertDialog.ERROR_TYPE);
                getLoginActivityView().hideProgress();
            }
        }, email, password);
    }

    private ILoginActivityView getLoginActivityView() {
        return (ILoginActivityView) this.view.getInstance();
    }

    private IRegisterActivityView getRegisterActivityView() {
        return (IRegisterActivityView) this.view.getInstance();
    }

    private IForgotPasswordActivityView getForgotPasswordActivityView() {
        return (IForgotPasswordActivityView) this.view.getInstance();
    }

    private IMainActivityView getMainActivityView() {
        return (IMainActivityView) this.view.getInstance();
    }

    private IAddJokeActivityView getAddJokeActivityView() {
        return (IAddJokeActivityView) this.view.getInstance();
    }

    private IMyJokesActivityView getMyJokesActivityView() {
        return (IMyJokesActivityView) this.view.getInstance();
    }

    private ILikedJokesActivityView getLikedJokesActivityView() {
        return (ILikedJokesActivityView) this.view.getInstance();
    }

    private IAdminActivityView getAdminActivityView() {
        return (IAdminActivityView) this.view.getInstance();
    }

    @Override
    public IAuthPresenter getAuthPresenter() {
        return authPresenter;
    }
}