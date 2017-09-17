package cazimir.com.bancuribune.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);

    }

    protected abstract int getLayoutId();
}
