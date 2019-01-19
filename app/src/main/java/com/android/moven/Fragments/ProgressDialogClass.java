package com.android.moven.Fragments;

import android.app.ProgressDialog;
import android.content.Context;


/**
 * Created by nafees on 12/16/2017.
 */

public class ProgressDialogClass {

    //public static SpotsDialog progressdialog_dotted;
    public static ProgressDialog progressDialog_round;


    public static void showRoundProgress(Context context, String message) {
        if (context != null || message.equals("")) {
            progressDialog_round = new ProgressDialog(context);
            progressDialog_round.setMessage(message);
            progressDialog_round.show();
        }
    }

    public static void dismissRoundProgress() {
        if (progressDialog_round != null && progressDialog_round.isShowing()) {
            progressDialog_round.dismiss();
        }
    }

}
