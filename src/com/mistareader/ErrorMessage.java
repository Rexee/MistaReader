package com.mistareader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

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

    public static void Toast(String inStr, Activity activity)
    {
        Toast.makeText(activity, inStr, Toast.LENGTH_SHORT).show();
    }
    
}
