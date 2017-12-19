package com.tsinova.bluetoothandroid.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tsinova.bluetoothandroid.R;
import com.tsinova.bluetoothandroid.view.BluetoothCustomDialog;

/**
 * Created by xucong on 17/11/9.
 */

public class UIUtils {
    /**
     * 创建带有按钮的Dialog
     *
     * @param title
     * @param message
     * @param negativeListener
     * @param positiveListener
     * @return Dialog
     */
    public static Dialog createDialog(Context context, String title,
                                      String message, String negativeTitle,
                                      DialogInterface.OnClickListener negativeListener,
                                      String positiveTitle,
                                      DialogInterface.OnClickListener positiveListener) {
        BluetoothCustomDialog.Builder customBuilder = new BluetoothCustomDialog.Builder(context);
//		 AlertDialog.Builder customBuilder = new AlertDialog.Builder(context);
        customBuilder.setTitle(title).setMessage(message)
                .setNegativeButton(negativeTitle, negativeListener)
                .setPositiveButton(positiveTitle, positiveListener);

        return customBuilder.create();
    }

    public static void toastFalse(Context context, int messageID) {
        try {
            String str = context.getResources().getString(messageID);
            toastFalse(context, str);
        } catch (Exception e) {
            CommonUtils.log("find bug ---------", e.getLocalizedMessage());
        }
    }

    private static Toast toast = null;

    /**
     * 自定义Toast
     */
    public static void toastFalse(Context context, String message) {
        if (toast == null) {
            toast = new Toast(context);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.toast_false, null);
        TextView contant = (TextView) view.findViewById(R.id.toast_contant);
        if (message.equals("")) {
            contant.setText("error");
        } else {
            contant.setText(message);
        }
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 创建只带确定按钮的Dialog
     *
     * @param title
     * @param message
     * @return Dialog
     */
    public static Dialog createSigleBtnDialog(Context context, String title,
                                              String message,
                                              String positiveTitle,
                                              DialogInterface.OnClickListener positiveListener) {
        BluetoothCustomDialog.Builder customBuilder = new BluetoothCustomDialog.Builder(context);
//		 AlertDialog.Builder customBuilder = new AlertDialog.Builder(context);
        customBuilder.setTitle(title).setMessage(message)
                .setPositiveButton(positiveTitle, positiveListener);

        return customBuilder.create();

    }

}

