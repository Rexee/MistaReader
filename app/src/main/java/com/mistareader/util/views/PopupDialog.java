package com.mistareader.util.views;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog.Builder;
import android.widget.ArrayAdapter;

import com.mistareader.R;
import com.mistareader.util.TitleValueItem;

import java.util.ArrayList;

public class PopupDialog {
    public PopupDialog(Context context, ArrayList<Integer> itemsIds, OnClickListener callback) {
        ArrayList<TitleValueItem> items = new ArrayList<>();
        for (Integer itemId : itemsIds) {
            items.add(new TitleValueItem(context, itemId));
        }

        new Builder(context)
                .setAdapter(new ArrayAdapter<>(context, R.layout.list_item_popup_menu, items), (dialog, which) -> {
                    TitleValueItem selected = items.get(which);
                    callback.onClick(dialog, selected.value);
                })
                .create()
                .show();
    }
}
