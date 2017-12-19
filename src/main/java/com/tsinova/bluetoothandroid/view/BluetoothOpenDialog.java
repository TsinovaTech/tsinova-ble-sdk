package com.tsinova.bluetoothandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;

import com.tsinova.bluetoothandroid.R;
import com.tsinova.bluetoothandroid.util.UIUtils;

/**
 * Created by xucong on 2017/12/19.
 */

public class BluetoothOpenDialog {
    public void creatBluetoothOpenDialog(final Activity activity) {
        String title = activity.getResources().getString(R.string.sdk_bltmanager_search_bl_dialog_tip);
        String content = activity.getResources().getString(R.string.sdk_bltmanager_search_bl_dialog_open_ble);
        String ok = activity.getResources().getString(R.string.sdk_bltmanager_search_bl_dialog_ok);
        UIUtils.createSigleBtnDialog(activity, title, content, ok, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
