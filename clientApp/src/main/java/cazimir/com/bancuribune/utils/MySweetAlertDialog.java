package cazimir.com.bancuribune.utils;

import android.content.Context;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class MySweetAlertDialog {

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
        }
    }
}
