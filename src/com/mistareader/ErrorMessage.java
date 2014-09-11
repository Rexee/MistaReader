package com.mistareader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

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

}
