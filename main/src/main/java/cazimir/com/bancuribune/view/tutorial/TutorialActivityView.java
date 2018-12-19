package cazimir.com.bancuribune.view.tutorial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.utils.CustomPaperOnboardingEngine;
import cazimir.com.bancuribune.view.list.MainActivityView;

/**
 * Class that handles the tutorial activity launch
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
                handleTutorialClosing();
            }
        });

    }

    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage(getString(R.string.tutorial1_title), getString(R.string.tutorial1_text),
                Color.parseColor("#678FB4"), R.drawable.onboarding_welcome, R.drawable.onboarding_welcome_small);
        PaperOnboardingPage scr2 = new PaperOnboardingPage(getString(R.string.tutorial2_title), getString(R.string.tutorial2_text),
                Color.parseColor("#678FB4"), R.drawable.onboarding_add, R.drawable.onboarding_welcome_small);
        PaperOnboardingPage scr3 = new PaperOnboardingPage(getString(R.string.tutorial3_title), getString(R.string.tutorial3_text),
                Color.parseColor("#678FB4"), R.drawable.onboarding_favorites, R.drawable.onboarding_welcome_small);
        PaperOnboardingPage scr4 = new PaperOnboardingPage(getString(R.string.tutorial4_title), getString(R.string.tutorial4_text),
                Color.parseColor("#678FB4"), R.drawable.onboarding_profile, R.drawable.onboarding_welcome_small);
        PaperOnboardingPage scr5 = new PaperOnboardingPage(getString(R.string.tutorial5_title), getString(R.string.tutorial5_text),
                Color.parseColor("#678FB4"), R.drawable.onboarding_ranks, R.drawable.onboarding_welcome_small);
        PaperOnboardingPage scr6 = new PaperOnboardingPage(getString(R.string.tutorial6_title), getString(R.string.tutorial6_text),
                Color.parseColor("#678FB4"), R.drawable.onboarding_like_share, R.drawable.onboarding_welcome_small);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        elements.add(scr6);
        elements.add(scr4);
        elements.add(scr5);
        return elements;
    }

    @Override
    public void onBackPressed() {
        handleTutorialClosing();
    }

    private void handleTutorialClosing() {
        Intent i = new Intent(TutorialActivityView.this, MainActivityView.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
