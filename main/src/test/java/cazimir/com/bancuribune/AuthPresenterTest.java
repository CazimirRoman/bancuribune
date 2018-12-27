package cazimir.com.bancuribune;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cazimir.com.bancuribune.base.IGeneralView;
import cazimir.com.bancuribune.presenter.auth.AuthPresenter;

import static org.junit.Assert.*;

/**
 * TODO: Add a class header comment!
 */
public class AuthPresenterTest {

    private AuthPresenter mAuthPresenter;

    @Mock
    private IGeneralView mView;

    @Mock
    private FirebaseAuth mAuth;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        mAuthPresenter = new AuthPresenter(mView, mAuth);
    }

    @Test
    public void login() {



    }

}