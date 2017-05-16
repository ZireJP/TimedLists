package mytimer.julianpeters.xyz.timedlists.adapters.itemtouchhelpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import mytimer.julianpeters.xyz.timedlists.R;
import mytimer.julianpeters.xyz.timedlists.adapters.MainCursorAdapter;
import mytimer.julianpeters.xyz.timedlists.adapters.SubListCursorAdapter;
import mytimer.julianpeters.xyz.timedlists.adapters.itemtouchhelpers.ItemTouchHelperAdapter;

/**
 * Created by julian on 15.05.17.
 */

public class MyTouchHelper extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    Context context;

    public MyTouchHelper(ItemTouchHelperAdapter mAdapter, Context context) {
        this.mAdapter = mAdapter;
        this.context = context;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof SubListCursorAdapter.ViewHolder || viewHolder instanceof MainCursorAdapter.ViewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
        return makeMovementFlags(0, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof SubListCursorAdapter.ViewHolder || viewHolder instanceof MainCursorAdapter.ViewHolder) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        mAdapter.onReleased();
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;

            Paint p = new Paint();
            p.setColor(context.getColor(R.color.deleteColor));
            float r = itemView.getRight();
            float t = itemView.getTop();
            float b = itemView.getBottom();
            int height = (int)(b-t);
            c.drawRect(r + dX, t, r, b, p);
            Drawable draw = context.getDrawable(R.drawable.ic_trash);
            draw.setBounds((int)r-height-height, (int)t, (int)r-height, (int)b);
            draw.draw(c);

        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}

