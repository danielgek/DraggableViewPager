package com.danielgek.tabsscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by danielgek on 25/02/15.
 */
public class CustomScrollView extends ScrollView {
    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context) {
        super(context);
    }

    public boolean isAtTop(){
        // Grab the first child placed in the this CustomScrollView
        View view = (View) getChildAt(0);

        // Calculate the scrolldiff
        int diff = (view.getTop()-getScrollY());

        // if diff is zero, then the bottom has been reached
        if( diff == 0 )
        {
            return true;

        }
        return false;
    }


}
