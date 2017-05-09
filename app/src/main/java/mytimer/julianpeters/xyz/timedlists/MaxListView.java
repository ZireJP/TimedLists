package mytimer.julianpeters.xyz.timedlists;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by julian on 07.05.17.
 */

public class MaxListView extends ListView {

    public MaxListView(Context context) {
        super(context);
    }

    public MaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = heightMeasureSpec - 200;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
