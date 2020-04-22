package org.duder.view.adapter.listener;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class LazyLoadRecyclerViewListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
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

        if (!isLoading && shouldFetchMoreItems) {
            //End of list has been reached
            onLoadMore();
            isLoading = true;
        }
    }

    private boolean hasMore(int totalItemCount) {
        return true;
    }

    private void reset() {
        isLoading = true;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }
}
