package org.duder.view.adapter.listener;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class LazyLoadRecyclerViewListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private boolean wasOnBottom = false;
    private static final int VISIBLE_THRESHOLD = 3; //Number of items left in list before we start loading more

    public LazyLoadRecyclerViewListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    protected abstract void onLoadMore();

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
        boolean shouldFetchMoreItems = totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD;

        if (!wasOnBottom && shouldFetchMoreItems) {
            //End of list has been reached
            onLoadMore();
            wasOnBottom = true;
        }
    }

    private boolean hasMore(int totalItemCount) {
        return true;
    }

    private void reset() {
        wasOnBottom = true;
    }

    public void setWasOnBottom(boolean wasOnBottom) {
        this.wasOnBottom = wasOnBottom;
    }
}
