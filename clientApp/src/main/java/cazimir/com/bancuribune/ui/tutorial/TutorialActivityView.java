package cazimir.com.bancuribune.ui.tutorial;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.utils.CustomPaperOnboardingEngine;

/**
 * TODO: Add a class header comment!
 */
public class TutorialActivityView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding_main_layout);

        CustomPaperOnboardingEngine engine = new CustomPaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());

        engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                // Probably here will be your exit action
                finish();
            }
        });

    }

    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage(getString(R.string.tutorial1_title), getString(R.string.tutorial1_text),
                Color.parseColor("#678FB4"), R.drawable.onboarding_like, R.drawable.key);
        PaperOnboardingPage scr2 = new PaperOnboardingPage(getString(R.string.tutorial2_title), getString(R.string.tutorial2_text),
                Color.parseColor("#65B0B4"), R.drawable.onboarding_add, R.drawable.wallet);
        PaperOnboardingPage scr3 = new PaperOnboardingPage(getString(R.string.tutorial3_title), getString(R.string.tutorial3_text),
                Color.parseColor("#9B90BC"), R.drawable.onboarding_like, R.drawable.shopping_cart);
        PaperOnboardingPage scr4 = new PaperOnboardingPage(getString(R.string.tutorial4_title), getString(R.string.tutorial4_text),
                Color.parseColor("#9B90BC"), R.drawable.onboarding_profile, R.drawable.shopping_cart);
        PaperOnboardingPage scr5 = new PaperOnboardingPage(getString(R.string.tutorial5_title), getString(R.string.tutorial5_text),
                Color.parseColor("#9B90BC"), R.drawable.onboarding_ranks, R.drawable.shopping_cart);
        PaperOnboardingPage scr6 = new PaperOnboardingPage(getString(R.string.tutorial6_title), getString(R.string.tutorial6_text),
                Color.parseColor("#9B90BC"), R.drawable.stores, R.drawable.shopping_cart);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        elements.add(scr4);
        elements.add(scr5);
        return elements;
    }
}
