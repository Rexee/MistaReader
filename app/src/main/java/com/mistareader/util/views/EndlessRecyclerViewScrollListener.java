package com.mistareader.util.views;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private static final int     PREFETCH_COUNT  = 20;
    public               boolean loading         = false;
    private              boolean controlsVisible = true;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLinearLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int visibleItemCount = view.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();

        if (controlsVisible && dy < -10) {
            hideBottomBar();
            controlsVisible = false;
        } else if (!controlsVisible && dy > 10) {
            showBottomBar();
            controlsVisible = true;
        }

        if (loading || totalItemCount <= 0) {
            return;
        }

        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        if (lastVisibleItem + PREFETCH_COUNT > totalItemCount) {
            onLoadMore(totalItemCount);
            loading = true;
        }
    }

    public void onLoadMore(int totalItemsCount) {}

    public void showBottomBar() {}

    public void hideBottomBar() {}

}
