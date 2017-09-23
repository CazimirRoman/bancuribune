package cazimir.com.bancuribune.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import cazimir.com.bancuribune.R;

public class MyAlertDialog extends AlertDialog {

    private Context context;
    private AlertDialog dialog;

    public MyAlertDialog(Context context) {
        super(context);
        this.context = context;
    }

    public AlertDialog getAlertDialog() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.close), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        return dialog;
    }

    public void showAlertDialog(String message){
        getAlertDialog().setMessage(message);
        if (!getAlertDialog().isShowing()) getAlertDialog().show();
    }
}
