package xyz.julianpeters.timedlists.adapters.Dividers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import xyz.julianpeters.timedlists.R;
import xyz.julianpeters.timedlists.helpers.StaticValues;

/**
 * Created by julian on 20.05.17.
 */

public class ItemDivider extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable divider;
    private int verticalSpaceHeight;
    float height;

    /**
     * Custom divider will be used
     */
    public ItemDivider(Context context, int resId, int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
        divider = ContextCompat.getDrawable(context, resId);
        height = context.getResources().getDimension(R.dimen.item_divider_size);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        int max = 7;
        float ch;
        for (int i = 0; i < childCount-2; i++) {
            if (i < max) {
                ch = (float)i/20;
            } else {
                ch = (float)max/20;
            }
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            float bottom = top +  height;
            float[] color = {StaticValues.hue(), StaticValues.sat()-ch, StaticValues.bright()-0.1f};
            Paint p = new Paint();
            p.setColor(Color.HSVToColor(color));
            c.drawRect(left, top, right, bottom, p);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) < parent.getAdapter().getItemCount() - 2) {
            outRect.bottom = verticalSpaceHeight;
        }
    }
}
