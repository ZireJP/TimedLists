package xyz.julianpeters.timedlists.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by julian on 07.05.17.
 */

public class MaxListView extends ListView {

    private int max;

    public MaxListView(Context context) {
        super(context);
        max = 0;
    }

    public MaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = heightMeasureSpec - max;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMax(int max) {
        this.max =  max;
    }
}
