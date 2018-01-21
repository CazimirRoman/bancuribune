package cazimir.com.bancuribune.ui.forgotPassword;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.base.BaseActivity;
import cazimir.com.bancuribune.presenter.CommonPresenter;
import cazimir.com.bancuribune.utils.Utils;

public class ForgotPasswordActivityView extends BaseActivity implements IForgotPasswordActivityView {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.btnForgotPassword)
    Button btnForgotPassword;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.progress)
    ProgressBar progress;

    private CommonPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CommonPresenter(this);

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = etEmail.getText().toString().trim();

                if(isFormDataValid()){
                    mPresenter.sendResetInstructions(email);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forgot_password_view;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void redirectToLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

    public boolean isFormDataValid() {

        String email = etEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.email_missing));
            return false;
        } else {
            if (!Utils.isValidEmail(email)) {
                etEmail.setError(getString(R.string.email_invalid));
                return false;
            }
        }

        return true;
    }
}
