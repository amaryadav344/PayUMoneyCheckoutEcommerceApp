package com.loopwiki.payumoneycheckout.Util;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;

import com.loopwiki.payumoneycheckout.R;

public class Helper {

    public static Dialog getSuccessDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.success_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }
}
