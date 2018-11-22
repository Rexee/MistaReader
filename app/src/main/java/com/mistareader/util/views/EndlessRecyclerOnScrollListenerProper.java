package com.mistareader.util.views;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

public abstract class EndlessRecyclerOnScrollListenerProper extends RecyclerView.OnScrollListener {

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private static final int DEFAULT_THRESHOLD = 5;
    private boolean isStaggedLayoutManager;
    // The current offset index of data you have loaded
    private int currentPage = 1;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = false;
    private boolean controlsVisible = true;
    private int preloadThreshold = DEFAULT_THRESHOLD;

    private RecyclerView.LayoutManager mLinearLayoutManager;
    public int firstItem = 0;
    public int firstVisibleItem;

    public EndlessRecyclerOnScrollListenerProper(RecyclerView.LayoutManager layoutManager) {
        this.mLinearLayoutManager = layoutManager;
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            isStaggedLayoutManager = true;
        }
    }

    public EndlessRecyclerOnScrollListenerProper(RecyclerView.LayoutManager layoutManager, int threshold) {
        this(layoutManager);
        preloadThreshold = threshold;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        if (isStaggedLayoutManager) {
            int[] positions = new int[2];
            ((StaggeredGridLayoutManager) mLinearLayoutManager).findFirstVisibleItemPositions(positions);
            firstVisibleItem = positions[0];
        } else {
            firstVisibleItem = ((LinearLayoutManager) mLinearLayoutManager).findFirstVisibleItemPosition();
        }

        int visibleItemCount = view.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();

        if (controlsVisible && dy < -10) {
            hideBottomBar();
            controlsVisible = false;
        } else if (!controlsVisible && dy > 10) {
            showBottomBar();
            controlsVisible = true;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the DEFAULT_THRESHOLD and need to reload more data.
        // If we do need to load some more data, we execute onLoadMore to fetch the data.
        if (!loading && totalItemCount > firstItem && (totalItemCount - visibleItemCount) <= (firstVisibleItem + preloadThreshold)) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount);
            loading = true;
        }
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount);

    public void showBottomBar() {
    }

    public void hideBottomBar() {
    }

    public void refresh() {
        loading = false;
        currentPage = 1;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        this.mLinearLayoutManager = layoutManager;
    }
}
