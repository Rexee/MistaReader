package com.mistareader.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.mistareader.R;

public class ErrorMessage {

    public static void Show(int EerrorRes, Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(EerrorRes);
        builder.setTitle(R.string.sError);
        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public static void Toast(String inStr, Context activity) {
        Toast.makeText(activity, inStr, Toast.LENGTH_SHORT).show();
    }

}
