package cazimir.com.bancuribune.utils;

import android.content.Context;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import cazimir.com.bancuribune.R;
import cazimir.com.bancuribune.constant.Constants;
import cazimir.com.bancuribune.view.list.MainActivityView;

public class MySweetAlertDialog {

    private String TAG = MySweetAlertDialog.class.getSimpleName();

    private Context mContext;

    public MySweetAlertDialog(Context context) {
        this.mContext = context;
    }

    public void show(String message, int type) {

        switch (type) {
            case SweetAlertDialog.ERROR_TYPE:
                new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(message)
                        .show();
                break;

            case SweetAlertDialog.SUCCESS_TYPE:
                new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Succes!")
                        .setContentText(message)
                        .show();
                break;


            case SweetAlertDialog.WARNING_TYPE:
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Atentie!")
                        .setContentText(message)
                        .show();
                break;

            case Constants.LEVEL_UP:
                new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Ai crescut in rang!")
                        .setContentText(message)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                MainActivityView main = (MainActivityView) mContext;
                                main.goToMyJokesActivity();
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
                break;

            case Constants.ADD_JOKE_REMINDER:
                new SweetAlertDialog(mContext, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Bloop!")
                        .setCustomImage(R.drawable.ic_add_joke_reminder)
                        .setContentText(message)
                        .show();
                break;
        }
    }
}
