package cazimir.com.bancuribune.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import cazimir.com.bancuribune.R;

public class MyAlertDialog {

    private Context mContext;

    public MyAlertDialog(Context context) {
        this.mContext = context;
    }

    public void show(String message) {

            new FancyAlertDialog.Builder((Activity) mContext)
                    .setBackgroundColor(Color.parseColor("#00bcd4"))  //Don't pass R.color.colorvalue
                    .setMessage(message)
                    .setNegativeBtnText("")
                    .setPositiveBtnBackground(Color.parseColor("#00bcd4"))  //Don't pass R.color.colorvalue
                    .setPositiveBtnText("Am inteles")
                    .setNegativeBtnBackground(Color.parseColor("#00FFFFFF"))  //Don't pass R.color.colorvalue
                    .setAnimation(Animation.POP)
                    .isCancellable(false)
                    .setIcon(R.mipmap.ic_launcher, Icon.Visible)
                    .OnPositiveClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            Toast.makeText(mContext,"Rate",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .OnNegativeClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            Toast.makeText(mContext,"Cancel",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .build();

    }

    public static class CustomBuilder extends FancyAlertDialog.Builder{

        public CustomBuilder(Activity activity) {
            super(activity);
        }

        @Override
        public FancyAlertDialog build() {
            return super.build();
        }
    }
}
