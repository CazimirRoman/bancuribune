package cazimir.com.bancuribune.view.login;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cazimir.com.bancuribune.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginWithEmailRedirectsToMainScreen {

    @Rule
    public ActivityTestRule<LoginActivityView> mActivityTestRule = new ActivityTestRule<>(LoginActivityView.class);

    @Test
    public void loginWithEmailRedirectsToMainScreen() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bootstrapButton = onView(
                allOf(withId(R.id.btnLoginWithEmail), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.scrollView),
                                        0),
                                2)));
        bootstrapButton.perform(scrollTo(), click());

        ViewInteraction bootstrapEditText = onView(
                allOf(withId(R.id.etEmail),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.expandableLayout),
                                        0),
                                0)));
        bootstrapEditText.perform(scrollTo(), replaceText("xewoce@nada.ltd"), closeSoftKeyboard());

        ViewInteraction bootstrapEditText2 = onView(
                allOf(withId(R.id.etPassword),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.expandableLayout),
                                        0),
                                1)));
        bootstrapEditText2.perform(scrollTo(), replaceText("123456"), closeSoftKeyboard());

        ViewInteraction bootstrapButton2 = onView(
                allOf(withId(R.id.btnLoginWithEmail), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.scrollView),
                                        0),
                                2)));
        bootstrapButton2.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //check if the toolbar contains the text "Bancuri bune"
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText("Bancuri bune")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
