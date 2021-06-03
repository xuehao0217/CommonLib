package com.xueh.comm_core.weight;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blankj.utilcode.util.ConvertUtils;

/**
 * 创 建 人: xueh
 * 创建日期: 2021/5/28 15:56
 * 备注：
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private float dividerWidth;
    private int maxSpan;
    private float topMargin;
    private float edgeMargin;
    private boolean headerNeedMargin = true;
    private boolean hasHeaderView = false;//标识recyclerView是否添加了headerView，headerView处于第0位，不需要设置各种margin

    public void setHasHeaderView(boolean hasHeaderView) {
        this.hasHeaderView = hasHeaderView;
    }

    public void setHeaderNeedMargin(boolean headerNeedMargin) {
        this.headerNeedMargin = headerNeedMargin;
    }

    public void setTopMargin(float topMargin) {
        this.topMargin = topMargin;
    }

    /**
     * @param edgeMargin   左右边距，单位dp
     * @param dividerWidth 中间间距，单位dp
     */
    public GridItemDecoration(float edgeMargin, float dividerWidth) {
        this.dividerWidth = ConvertUtils.dp2px(dividerWidth);
        this.edgeMargin = ConvertUtils.dp2px(edgeMargin);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanIndex = 0, halfWidth = 0;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            maxSpan = gridLayoutManager.getSpanCount();

            GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            spanIndex = layoutParams.getSpanIndex();
            halfWidth = (int) (dividerWidth / 2);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            maxSpan = staggeredGridLayoutManager.getSpanCount();

            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            spanIndex = layoutParams.getSpanIndex();
            halfWidth = (int) (dividerWidth / 2);
        }

        int position = parent.getChildAdapterPosition(view);
        if (hasHeaderView) {
            if (position == 0) {
                return;
            }
        }
        if (headerNeedMargin || position > 0) {
            outRect.left = halfWidth;
            outRect.right = halfWidth;
            outRect.top = position < maxSpan ? (topMargin > 0 ? ConvertUtils.dp2px(topMargin) : 0) : (int) dividerWidth;
            if (edgeMargin > 0) {
                if (spanIndex == 0) {
                    outRect.left += edgeMargin - halfWidth;
                } else if (spanIndex == maxSpan - 1) {
                    outRect.right += edgeMargin - halfWidth;
                }
            }
        }
    }
}
