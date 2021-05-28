package com.xueh.comm_core.weight;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blankj.utilcode.util.ConvertUtils;

/**
 * 创 建 人: xueh
 * 创建日期: 2021/5/28 15:56
 * 备注：
 */
public class CommonStaggeredItemDecoration extends RecyclerView.ItemDecoration {
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
     * @param maxSpan      几列
     */
    public CommonStaggeredItemDecoration(float edgeMargin, float dividerWidth, int maxSpan) {
        this.dividerWidth = ConvertUtils.dp2px(dividerWidth);
        this.edgeMargin = ConvertUtils.dp2px(edgeMargin);
        this.maxSpan = maxSpan;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int position = parent.getChildAdapterPosition(view);
            if (hasHeaderView) {
                if (position == 0) {
                    return;
                }
            }
            int spanIndex = layoutParams.getSpanIndex();
            int halfWidth = (int) (dividerWidth / 2);

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
}
