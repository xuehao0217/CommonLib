package com.xueh.comm_core.base;

import android.view.View;

/**
 * tag: class//
 * description:
 */
public class BaseViewImpl {

    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     */
    public interface OnClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        void onClick(View v);
    }

}
