package cazimir.com.bancuribune.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.codemybrainsout.ratingdialog.RatingDialog;

/**
 * Own implementation of RatingDialog because the default implementation sets the Newer flag in shared preferences
 * even if user did not send feedback and clicked on cancel.
 */
public class RatingDialogCustom extends RatingDialog {

    private static final String SHOW_NEVER = "show_never";
    private Context context;

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == com.codemybrainsout.ratingdialog.R.id.dialog_rating_button_feedback_cancel) {
            dismiss();
            showNeverOff();
        }
    }

    private RatingDialogCustom(Context context, Builder builder) {
        super(context, builder);
        this.context = context;
    }

    private void showNeverOff() {
        String myPrefs = "RatingDialog";
        SharedPreferences sharedpreferences = context.getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(SHOW_NEVER, false);
        editor.apply();
    }

    public static class BuilderCustom extends RatingDialog.Builder {


        private Context context;

        public BuilderCustom(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public RatingDialog build() {
            return new RatingDialogCustom(context, this);
        }
    }
}
