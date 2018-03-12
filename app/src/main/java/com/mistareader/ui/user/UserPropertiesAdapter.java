package com.mistareader.ui.user;

import android.view.View;
import android.widget.TextView;

import com.mistareader.R;
import com.mistareader.util.TitleValueItem;
import com.mistareader.util.views.Recycler.RecyclerAdapter;
import com.mistareader.util.views.Recycler.RecyclerViewHolder;

import butterknife.BindView;

public class UserPropertiesAdapter extends RecyclerAdapter<TitleValueItem, UserPropertiesAdapter.ViewHolder> {

    public interface ValueClick {
        void onSkypeClick(String value);
    }

    private ValueClick mCallback;

    public UserPropertiesAdapter(ValueClick callback) {
        mCallback = callback;
    }

    @Override
    public void onBindViewHolder(final UserPropertiesAdapter.ViewHolder holder, int position, TitleValueItem item) {
        if (item.title.equals("URL")) {
            holder.value.setOnClickListener(null);
        } else if (item.title.equals("Skype")) {
            holder.value.setOnClickListener(v -> mCallback.onSkypeClick(((TextView) v).getText().toString()));
        } else {
            holder.value.setOnClickListener(null);
        }

        holder.title.setText(item.title);
        holder.value.setText("" + item.valueStr);
    }

    public class ViewHolder extends RecyclerViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.value) TextView value;

        public ViewHolder(View v) {
            super(v);
        }
    }
}
