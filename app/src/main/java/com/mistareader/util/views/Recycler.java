package com.mistareader.util.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mistareader.R;
import com.mistareader.util.views.Recycler.RecyclerViewHolder;
import com.mistareader.util.ThemesManager;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.ButterKnife;

public class Recycler<T, VH extends RecyclerViewHolder> {
    private static final int DEFAULT_PREFETCH_ITEMS_COUNT = 5;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter<T, VH> mAdapter;
    public LinearLayoutManager    mLinearLayoutManager;

    public Recycler(RecyclerView recyclerView, RecyclerAdapter<T, VH> adapter) {
        this(recyclerView, -1, adapter, true, true);
    }

    public Recycler(RecyclerView recyclerView, int layoutItemResId, RecyclerAdapter<T, VH> adapter, boolean divider, boolean vertical) {
        mRecyclerView = recyclerView;
        Context context = mRecyclerView.getContext();
        if (vertical) {
            mLinearLayoutManager = new LinearLayoutManager(context);
        } else {
            mLinearLayoutManager = new LinearLayoutManager(context, OrientationHelper.HORIZONTAL, false);
        }
        mLinearLayoutManager.setSmoothScrollbarEnabled(false);
        mLinearLayoutManager.setInitialPrefetchItemCount(DEFAULT_PREFETCH_ITEMS_COUNT);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        if (divider) mRecyclerView.addItemDecoration(new HorizontalItemDecoration(context));

        mAdapter = adapter;
        mAdapter.setLayoutItemResId(layoutItemResId);
        mRecyclerView.setAdapter(mAdapter);
    }

    public Recycler(RecyclerView recyclerView, int layoutItemResId, RecyclerAdapter<T, VH> adapter) {
        this(recyclerView, layoutItemResId, adapter, true, true);
    }

    public Recycler(RecyclerView recyclerView, int layoutItemResId, RecyclerAdapter<T, VH> adapter, boolean divider) {
        this(recyclerView, layoutItemResId, adapter, divider, true);
    }

    public void updateList(LinkedHashMap<String, T> newItems) {
        mAdapter.updateList(newItems);
    }

    public void updateList(List<T> newItems) {
        mAdapter.updateList(newItems);
    }

    public void appendList(List<T> newItems) {
        mAdapter.appendList(newItems, false);
    }

    public void appendList(T newItem) {
        mAdapter.appendList(newItem);
    }

    public void appendList(List<T> newItems, boolean addToTop) {
        mAdapter.appendList(newItems, addToTop);
    }

    public void appendList(List<T> newItems, boolean addToTop, int offset) {
        mAdapter.appendList(newItems, addToTop, offset);
    }

    public void deleteItems(boolean notify) {
        mAdapter.deleteItems(notify);
    }

    public void updateItem(T newItem) {
        mAdapter.updateItem(newItem);
    }

    public void updateItem(T newItem, int id) {
        mAdapter.updateItem(newItem, id);
    }

    public void deleteItem(int id) {
        mAdapter.deleteItem(id);
    }

    public void insertList(List<T> newItems, int pos) {
        mAdapter.insertList(newItems, pos);
    }

    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    public void setHasStableIds(boolean hasStableIds) {
        mAdapter.setHasStableIds(hasStableIds);
    }

    public void clear() {
        mAdapter.clear();
    }

    public int findFirstVisibleItemPosition() {
        return mLinearLayoutManager.findFirstVisibleItemPosition();
    }

    public int findLastVisibleItemPosition() {
        return mLinearLayoutManager.findLastVisibleItemPosition();
    }

    public static abstract class RecyclerAdapter<T, VH extends RecyclerViewHolder> extends RecyclerView.Adapter<VH> {
        protected List<T> mItems;
        protected int     mLayoutItemResId;

        public void setLayoutItemResId(int layoutItemId) {
            this.mLayoutItemResId = layoutItemId;
        }

        //TODO: remove reflection!
        public VH onCreateViewHolder(View v, int viewType) {
            try {
                Class<VH> VH_class = (Class<VH>) ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[1];
                return VH_class.getConstructor(this.getClass(), View.class).newInstance(this, v);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public abstract void onBindViewHolder(VH holder, int position, T item);

        @Override
        public VH onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(mLayoutItemResId, viewGroup, false);
            return onCreateViewHolder(v, viewType);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            T item = mItems.get(position);
            onBindViewHolder(holder, position, item);
        }

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.size();
        }

        public int getItemPosition(T item) {
            return mItems.indexOf(item);
        }

        public void updateList(List<T> items) {
            mItems = items;
            notifyDataSetChanged();
        }

        public void updateList(LinkedHashMap<String, T> items) {
            mItems = new ArrayList<>(items.size());
            mItems.addAll(items.values());
            notifyDataSetChanged();
        }

        public void appendList(T item) {
            if (mItems == null) {
                mItems = new ArrayList<>();
                mItems.add(item);
                notifyDataSetChanged();
            } else {
                int pos = mItems.size();
                mItems.add(item);
                notifyItemRangeInserted(pos, 1);
            }
        }

        public void appendList(List<T> items) {
            appendList(items, false);
        }

        public void appendList(List<T> items, boolean addToTop) {
            if (mItems == null) {
                mItems = items;
                notifyDataSetChanged();
            } else {
                if (addToTop) {
                    mItems.addAll(0, items);
                    notifyItemRangeInserted(0, items.size());
                } else {
                    int pos = mItems.size();
                    mItems.addAll(items);
                    notifyItemRangeInserted(pos, items.size());
                }
            }
        }

        public void appendList(List<T> items, boolean addToTop, int offset) {
            if (mItems == null) {
                mItems = items;
                notifyDataSetChanged();
            } else {
                if (addToTop) {
                    mItems.addAll(offset, items);
                    notifyItemRangeInserted(offset, items.size());
                } else {
                    int pos = mItems.size();
                    mItems.addAll(items);
                    notifyItemRangeInserted(pos, items.size());
                }
            }
        }

        public void updateItem(T newItem, int id) {
            if (mItems == null) return;

            if (!hasStableIds()) return;
            for (int i = 0; i < mItems.size(); i++) {
                if (getItemId(i) == id) {
                    mItems.set(i, newItem);
                    notifyItemChanged(i);
                    return;
                }
            }
        }

        public void updateItem(T newItem) {
            if (mItems == null) return;

            for (int i = 0; i < mItems.size(); i++) {
                if (mItems.get(i).equals(newItem)) {
                    mItems.set(i, newItem);
                    notifyItemChanged(i);
                    return;
                }
            }
        }

        public void deleteItem(int id) {
            if (!hasStableIds()) return;
            if (mItems == null) return;

            for (int i = 0; i < mItems.size(); i++) {
                if (getItemId(i) == id) {
                    mItems.remove(i);
                    notifyItemRemoved(i);
                    return;
                }
            }
        }

        public void deleteItems(boolean notify) {
            if (mItems == null) return;
            int count = mItems.size();
            mItems.clear();
            if (notify) notifyItemRangeRemoved(0, count);
        }

        public void insertList(List<T> items, int pos) {
            if (mItems == null) {
                mItems = items;
                notifyDataSetChanged();
            } else {
                mItems.addAll(pos, items);
                notifyItemRangeInserted(pos, items.size());
                notifyItemChanged(0);//TODO: only for lists with headers
            }
        }

        public boolean isEmpty() {
            return mItems == null || mItems.isEmpty();
        }

        public List<T> getItems() {
            return mItems;
        }

        public void clear() {
            if (mItems != null) {
                int count = mItems.size();
                mItems.clear();
                notifyItemRangeRemoved(0, count);
            }
        }
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public RecyclerViewHolder(ViewGroup viewGroup, int layoutItemResId) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(layoutItemResId, viewGroup, false));
            ButterKnife.bind(this, itemView);
        }
    }

    public static class HorizontalItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public HorizontalItemDecoration(Context context) {
            final TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
            mDivider = a.getDrawable(0);
            a.recycle();
            ThemesManager.tint(context, mDivider, R.attr.dividerListTint);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (mDivider == null) {
                return;
            }

            int position = parent.getChildAdapterPosition(view);
            if (position == RecyclerView.NO_POSITION || (position == 0)) {
                return;
            }

            //hide divider for hidden post
            Object tag = view.getTag();
            if (tag != null && tag.equals(false)) {
                return;
            }

            outRect.top = mDivider.getIntrinsicHeight();
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mDivider == null) {
                super.onDrawOver(c, parent, state);
                return;
            }

            int left, right, top, bottom, size;
            int childCount = parent.getChildCount();

            size = mDivider.getIntrinsicHeight();
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 1; i < childCount; i++) {
                View child = parent.getChildAt(i);

                //hide divider for hidden post
                Object tag = child.getTag();
                if (tag != null && tag.equals(false)) {
                    continue;
                }
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                top = child.getTop() - params.topMargin - size;
                bottom = top + size;

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}