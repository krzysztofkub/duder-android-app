package org.duder.view.fragment;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.duder.R;
import org.duder.view.adapter.listener.LazyLoadRecyclerViewListener;
import org.duder.viewModel.RecyclerViewModel;

public abstract class RecyclerFragment extends BaseFragment {
    protected ProgressBar progressBar;
    protected SwipeRefreshLayout swipeLayout;
    protected LazyLoadRecyclerViewListener lazyListener;
    protected RecyclerView list;
    protected RecyclerViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = onCreateRecyclerView(inflater, container, savedInstanceState);
        init();
        return view;
    }

    private void init() {
        setUpProgressBar();
        setUpSwipeLayout();
        initListeners();
    }

    private void setUpProgressBar() {
        Drawable indeterminateDrawable = progressBar.getIndeterminateDrawable();
        if (indeterminateDrawable != null) {
            indeterminateDrawable.setColorFilter(ContextCompat.getColor(mContext, R.color.secondary_text), PorterDuff.Mode.SRC_IN);
        }
        Drawable progressDrawable = progressBar.getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.setColorFilter(ContextCompat.getColor(mContext, R.color.secondary_text), PorterDuff.Mode.SRC_IN);
        }
    }

    private void setUpSwipeLayout() {
        swipeLayout.setColorSchemeResources(R.color.primary);
    }

    private void initListeners() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        list.setLayoutManager(layoutManager);
        list.setAdapter(viewModel.getListAdapter());
        //Setup infinite scrolling
        lazyListener = new LazyLoadRecyclerViewListener(layoutManager) {
            @Override
            public void onLoadMore() {
                viewModel.loadItemsBatch();
            }
        };
        list.addOnScrollListener(lazyListener);
        swipeLayout.setOnRefreshListener(viewModel::refreshItems);
    }

    protected void finishLoading() {
        lazyListener.setLoading(false);
        progressBar.setVisibility(View.GONE);
        swipeLayout.setRefreshing(false);
    }

    public abstract View onCreateRecyclerView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);
}
