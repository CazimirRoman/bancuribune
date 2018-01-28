package cazimir.com.bancuribune.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cazimir.com.bancuribune.constants.Constants;
import cazimir.com.bancuribune.ui.login.OnFormValidatedListener;

public class UtilHelperClass {

    public static boolean isSameDay(Date day1, Date day2) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(day1);
        cal2.setTime(day2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String removeAccents(String text) {
        return text == null ? null :
                Normalizer.normalize(text, Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String convertEpochToDate(long epoch) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date(epoch));
    }

    public static boolean isInternetAvailable(Context context) {
        return NetworkStatus.getInstance(context).isOnline();
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static void validateFormData(OnFormValidatedListener listener, String email, String password, String password2) {

        if (TextUtils.isEmpty(email)) {
            listener.onValidateFail(Constants.EMAIL_EMPTY);
            return;
        } else {
            if (!isValidEmail(email)) {
                listener.onValidateFail(Constants.EMAIL_INVALID);
                return;
            }
        }

        if (TextUtils.isEmpty(password)) {
            listener.onValidateFail(Constants.PASSWORD_EMPTY);
            return;
        } else {
            if (password.length() < 6) {
                listener.onValidateFail(Constants.PASSWORD_INVALID);
                return;
            }
        }

        if(!password2.equals("")){
            if(!password2.equals(password)){
                listener.onValidateFail(Constants.PASSWORD_MATCH_ERROR);
                return;
            }
        }

        listener.onValidateSuccess(email, password);
    }

    public static Bitmap drawMultilineTextToBitmap(Context gContext, int gResId, String gText) {
        // prepare canvas
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap background = BitmapFactory.decodeResource(resources, gResId);

        Bitmap.Config bitmapConfig = background.getConfig();
        // set default share_background config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        background = background.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(background);

        // new antialiased Paint
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(0, 0, 0));
        // text size in pixels

        paint.setTextSize((int) (50 * scale));

        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // set text width to canvas width minus 16dp padding
        int textWidth = canvas.getWidth() - (int) (16 * scale);

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(
                gText, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // get height of multiline text
        int textHeight = textLayout.getHeight();

        // get position of text's top left corner
        float x = (background.getWidth() - textWidth) / 2;
        float y = (background.getHeight() - textHeight) / 2;

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        return background;
    }
}
