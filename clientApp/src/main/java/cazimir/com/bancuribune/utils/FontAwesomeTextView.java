package cazimir.com.bancuribune.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class FontAwesomeTextView extends android.support.v7.widget.AppCompatTextView {
    public FontAwesomeTextView(Context context) {
        super(context);
        setTypeface(context);
    }

    public FontAwesomeTextView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        setTypeface(context);
    }

    private void setTypeface(Context context) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(),"fontawesome-webfont.otf");
            setTypeface(tf);
    }
}
