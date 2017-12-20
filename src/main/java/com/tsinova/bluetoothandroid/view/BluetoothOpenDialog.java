package com.tsinova.bluetoothandroid.view;

import android.app.Activity;
import android.content.DialogInterface;

import com.tsinova.bluetoothandroid.R;
import com.tsinova.bluetoothandroid.util.UIUtils;

/**
 * Created by xucong on 2017/12/20.
 */

public class BluetoothOpenDialog {
    public static void creatBluetoothOpenDialog(Activity activity) {
        String title =  activity.getString(com.tsinova.bluetoothandroid.R.string.sdk_bltmanager_search_bl_dialog_tip);
        String content = activity.getString(com.tsinova.bluetoothandroid.R.string.sdk_bltmanager_search_bl_dialog_open_ble);
        String ok = activity.getString(com.tsinova.bluetoothandroid.R.string.sdk_bltmanager_search_bl_dialog_ok);
        UIUtils.createSigleBtnDialog(activity, title, content, ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
