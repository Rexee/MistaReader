package com.mistareader.util.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mistareader.R;
import com.mistareader.util.Empty;
import com.mistareader.util.ThemesManager;
import com.mistareader.util.views.Recycler.RecyclerViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.ButterKnife;

import static com.mistareader.api.API.DEFAULT_PAGE;
import static com.mistareader.api.API.DEFAULT_TOPICS_COUNT;

public class Recycler<T, VH extends RecyclerViewHolder> {
    private static final int DEFAULT_PREFETCH_ITEMS_COUNT = 5;
    private RecyclerView                          mRecyclerView;
    public  RecyclerAdapter<T, VH>                mAdapter;
    public  LinearLayoutManager                   mLinearLayoutManager;
    public  EndlessRecyclerOnScrollListenerProper mOnScrollListener;
    private boolean mDisableScrolling = false;
    public  boolean mHasMoreItems     = true;
    private boolean mIsLoading        = true;
    public  int     mPage             = 1;

    public Recycler() {
    }

    public Recycler(RecyclerView recyclerView, RecyclerAdapter<T, VH> adapter) {
        this(recyclerView, adapter.mLayoutItemResId, adapter, true, true, null);
    }

    public Recycler(RecyclerView recyclerView, RecyclerAdapter<T, VH> adapter, boolean divider, boolean vertical) {
        this(recyclerView, adapter.mLayoutItemResId, adapter, divider, vertical, null);
    }

    public Recycler(RecyclerView recyclerView, RecyclerAdapter<T, VH> adapter, boolean divider, boolean vertical, LinearLayoutManager layoutManager) {
        this(recyclerView, adapter.mLayoutItemResId, adapter, divider, vertical, layoutManager);
    }

    public Recycler(RecyclerView recyclerView, RecyclerAdapter<T, VH> adapter, boolean divider) {
        this(recyclerView, adapter.mLayoutItemResId, adapter, divider, true, null);
    }

    public Recycler(RecyclerView recyclerView, int layoutItemResId, RecyclerAdapter<T, VH> adapter, boolean divider, boolean vertical, LinearLayoutManager layoutManager) {
        init(recyclerView, layoutItemResId, adapter, divider, vertical, layoutManager);
    }

    public Recycler(RecyclerView recyclerView, int layoutItemResId, RecyclerAdapter<T, VH> adapter) {
        this(recyclerView, layoutItemResId, adapter, true, true, null);
    }

    public Recycler(RecyclerView recyclerView, int layoutItemResId, RecyclerAdapter<T, VH> adapter, boolean divider) {
        this(recyclerView, layoutItemResId, adapter, divider, true, null);
    }

    public void init(RecyclerView recyclerView, int layoutItemResId, RecyclerAdapter<T, VH> adapter, boolean divider, boolean vertical, LinearLayoutManager layoutManager) {
        mRecyclerView = recyclerView;
        if (mDisableScrolling) {
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setNestedScrollingEnabled(false);
        }
        Context context = mRecyclerView.getContext();
        if (layoutManager == null) {
            if (vertical) {
                mLinearLayoutManager = new LinearLayoutManager(context);
            } else {
                mLinearLayoutManager = new LinearLayoutManager(context, OrientationHelper.HORIZONTAL, false);
            }
        } else {
            mLinearLayoutManager = layoutManager;
        }
        if (mDisableScrolling) {
            mLinearLayoutManager.setAutoMeasureEnabled(true);
        }
        mLinearLayoutManager.setSmoothScrollbarEnabled(false);
        //        mLinearLayoutManager.setInitialPrefetchItemCount(DEFAULT_PREFETCH_ITEMS_COUNT);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        if (divider) mRecyclerView.addItemDecoration(new HorizontalItemDecoration(context));

        mAdapter = adapter;
        mAdapter.setLayoutItemResId(layoutItemResId);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void disableChangesAnimations() {
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    public void setHasStableIds(boolean hasStableIds) {
        mAdapter.setHasStableIds(hasStableIds);
    }

    public void addOnScrollListener(int threshold, final OnScroll callback) {
        mOnScrollListener = new EndlessRecyclerOnScrollListenerProper(mLinearLayoutManager, threshold) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                callback.onLoadMore(page, totalItemsCount);
            }
        };
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    public void addOnScrollListener(final OnScroll callback) {
        mOnScrollListener = new EndlessRecyclerOnScrollListenerProper(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                callback.onLoadMore(page, totalItemsCount);
            }
        };
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    public void setSnapAsPager(boolean pager) {
        SnapHelper snapHelper;
        if (pager) {
            snapHelper = new PagerSnapHelper();
        } else {
            snapHelper = new LinearSnapHelper();
        }

        snapHelper.attachToRecyclerView(mRecyclerView);
    }

    public void removeOnScrollListener() {
        if (mOnScrollListener != null) {
            mRecyclerView.removeOnScrollListener(mOnScrollListener);
        }
    }

    public void addOnItemClickListener(OnRecyclerItemClickListener<T> callback) {
        mAdapter.mCallback = callback;
    }

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        mLinearLayoutManager = layoutManager;
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        if (mOnScrollListener != null) {
            mOnScrollListener.setLayoutManager(mLinearLayoutManager);
        }
    }

    public void setLoading(boolean loading) {
        mIsLoading = loading;
        if (mOnScrollListener != null) {
            mOnScrollListener.setLoading(loading);
        }
    }

    public boolean isLoading() {
        if (mOnScrollListener != null) {
            return mOnScrollListener.isLoading();
        } else {
            return mIsLoading;
        }
    }

    public void disableScrolling() {
        mDisableScrolling = true;

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mLinearLayoutManager.setAutoMeasureEnabled(true);
    }

    public void refresh() {
        if (mOnScrollListener != null) {
            mOnScrollListener.refresh();
        }
        mLinearLayoutManager.scrollToPosition(0);
    }

    public void setList(LinkedHashMap<String, T> newItems) {
        mAdapter.setList(newItems);
    }

    public void setList(List<T> newItems) {
        mAdapter.setList(newItems);
    }

    public void appendList(List<T> newItems) {
        mAdapter.appendList(newItems, false);
    }

    public void appendList(T newItem) {
        mAdapter.appendList(newItem);
    }

    public void appendList(T newItem, boolean addToTop) {
        mAdapter.appendList(newItem, addToTop);
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

    public void clear() {
        mAdapter.clear();
    }

    public int findFirstVisibleItemPosition() {
        return mLinearLayoutManager.findFirstVisibleItemPosition();
    }

    public int findLastVisibleItemPosition() {
        return mLinearLayoutManager.findLastVisibleItemPosition();
    }

    public List<T> getItems() {
        return mAdapter.getItems();
    }

    public boolean loadData(int page) {
        mPage = page;
        if (page == DEFAULT_PAGE) {
            mHasMoreItems = true;
            if (mOnScrollListener != null) {
                mOnScrollListener.setCurrentPage(DEFAULT_PAGE);
            }
        }
        mIsLoading = mHasMoreItems;
        if (mOnScrollListener != null) {
            mOnScrollListener.setLoading(mHasMoreItems);
        }
        return mHasMoreItems;
    }

    public int size() {
        return mAdapter.size();
    }

    public void setPage(int page) {
        mPage = page;
        if (mOnScrollListener != null) {
            mOnScrollListener.setCurrentPage(page);
        }
    }

    public void onLoadedList(List<T> items, long page) {
        setLoading(false);
        mHasMoreItems = !(Empty.is(items) || items.size() < DEFAULT_TOPICS_COUNT);

        if (page == DEFAULT_PAGE) {
            setList(items);
        } else {
            appendList(items);
        }
    }

    public static abstract class RecyclerAdapter<T, VH extends RecyclerViewHolder> extends RecyclerView.Adapter<VH> {
        protected List<T>                        mItems;
        public    int                            mLayoutItemResId;
        protected OnRecyclerItemClickListener<T> mCallback;
        protected Constructor<VH>                mHolderConstructor;

        public RecyclerAdapter(int layoutItemResId) {
            mLayoutItemResId = layoutItemResId;
        }

        public void setLayoutItemResId(int layoutItemId) {
            this.mLayoutItemResId = layoutItemId;
        }

        @NonNull
        public VH onCreateViewHolder(View v, int viewType) {
            try {
                if (mHolderConstructor == null) {
                    Type classType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
                    mHolderConstructor = ((Class<VH>) classType).getConstructor(this.getClass(), View.class);
                }
                return mHolderConstructor.newInstance(this, v);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        //        public VH onCreateViewHolder(View v, int viewType) {
        //            return null;
        //        }

        public void onBindViewHolder(VH holder, int position, T item) {}

        @NonNull @Override
        public VH onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(mLayoutItemResId, viewGroup, false);
            return onCreateViewHolder(v, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
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

        public void setList(List<T> items) {
            setList(items, true);
        }

        public void setList(List<T> items, boolean notify) {
            mItems = items;
            if (notify) {
                notifyDataSetChanged();
            }
        }

        public void setList(LinkedHashMap<String, T> items) {
            mItems = new ArrayList<>(items.size());
            mItems.addAll(items.values());
            notifyDataSetChanged();
        }

        public void addItemNoNotif(T item) {
            if (mItems == null) {
                mItems = new ArrayList<>();
            }
            mItems.add(item);
        }

        public void appendList(T item) {
            appendList(item, false);
        }

        public void appendList(T item, boolean addToTop) {
            if (mItems == null) {
                mItems = new ArrayList<>();
                mItems.add(item);
                notifyDataSetChanged();
            } else {
                if (addToTop) {
                    mItems.add(0, item);
                    notifyItemRangeInserted(0, 1);
                } else {
                    mItems.add(item);
                    notifyItemRangeInserted(mItems.size() - 1, 1);
                }
            }
        }

        public void appendList(List<T> items) {
            appendList(items, false);
        }

        public void appendList(List<T> items, boolean addToTop) {
            if (items == null) {
                return;
            }
            if (mItems == null) {
                setList(items);
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

        //Let's assume that items are already sorted
        //Update only if new list have changes
        public void updateItems(List<T> items) {
            if (mItems == null || items.size() != mItems.size()) {
                mItems = items;
                notifyDataSetChanged();
                return;
            }

            for (int i = 0; i < mItems.size(); i++) {
                if (!mItems.get(i).equals(items.get(i))) {
                    mItems = items;
                    notifyDataSetChanged();
                    return;
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

        public void deleteItemByPos(int pos) {
            if (mItems == null || pos < 0) return;
            mItems.remove(pos);
            notifyItemRemoved(pos);
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

        public T getItemById(long listItemId) {
            if (mItems == null) return null;
            for (int i = 0; i < mItems.size(); i++) {
                if (getItemId(i) == listItemId) {
                    return mItems.get(i);
                }
            }
            return null;
        }

        public T getItemByPos(int pos) {
            if (mItems == null || pos < 0) {
                return null;
            }
            return mItems.get(pos);
        }

        public boolean isEmpty() {
            return mItems == null || mItems.isEmpty();
        }

        public List<T> getItems() {
            return mItems;
        }

        public void clear() {
            clear(true);
        }

        protected OnClickListener getCallback(final RecyclerView.ViewHolder holder) {
            return new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        int pos = holder.getAdapterPosition();
                        if (pos < 0 || Empty.is(mItems)) {
                            return;
                        }
                        mCallback.onRecyclerItemClicked(mItems.get(pos), pos, v);
                    }
                }
            };
        }

        public int size() {
            return getItemCount();
        }

        public void clear(boolean notify) {
            if (mItems != null) {
                int count = mItems.size();
                mItems.clear();
                if (notify) {
                    notifyItemRangeRemoved(0, count);
                }
            }
        }

        @Nullable
        public T getCurrentItem(RecyclerView.ViewHolder holder) {
            if (Empty.is(mItems)) {
                return null;
            }
            int pos = holder.getAdapterPosition();
            if (pos < 0 || pos > mItems.size() - 1) {
                return null;
            }
            return mItems.get(pos);
        }

        protected void setText(TextView textView, String value) {
            if (Empty.is(value)) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.VISIBLE);
                textView.setText(value);
            }
        }

        protected void displayImage(ImageLoader imageLoader, String url, ImageView imageView, ImageLoadingListener callback) {
            if (Empty.is(url)) {
                imageView.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.VISIBLE);
                imageLoader.displayImage(url, imageView, callback);
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

        public RecyclerViewHolder(ViewGroup viewGroup, int layoutItemResId, boolean allowBind) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(layoutItemResId, viewGroup, false));
            if (allowBind) {
                ButterKnife.bind(this, itemView);
            }
        }
    }

    public interface OnRecyclerItemClickListener<T> {
        void onRecyclerItemClicked(T item, int pos, View v);
    }

    public interface OnScroll {
        void onLoadMore(int page, int totalItemsCount);
    }

    public static class HorizontalItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public HorizontalItemDecoration(Context context) {
            final TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
            mDivider = a.getDrawable(0);
            a.recycle();
            ThemesManager.tintAttr(context, mDivider, R.attr.dividerListTint);
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

    public abstract static class BasicDiff<T> extends DiffUtil.Callback {
        protected final List<T> mOldList;
        protected final List<T> mNewList;

        public BasicDiff(List<T> oldList, List<T> newList) {
            this.mOldList = oldList;
            this.mNewList = newList;
        }

        @Override
        public int getOldListSize() {
            return mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return getOldId(oldItemPosition).equals(getNewId(newItemPosition));
        }

        protected abstract String getOldId(int pos);

        protected abstract String getNewId(int pos);

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return defaultCompare(oldItemPosition, newItemPosition);
        }

        private boolean defaultCompare(int oldItemPosition, int newItemPosition) {
            final T oldCategories = mOldList.get(oldItemPosition);
            final T newCategories = mNewList.get(newItemPosition);
            return oldCategories.equals(newCategories);
        }
    }
}