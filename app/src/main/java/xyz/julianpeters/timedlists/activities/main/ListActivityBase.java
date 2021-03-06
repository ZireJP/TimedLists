package xyz.julianpeters.timedlists.activities.main;

import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import xyz.julianpeters.timedlists.adapters.Dividers.ItemDivider;
import xyz.julianpeters.timedlists.adapters.itemtouchhelpers.MyTouchHelper;
import xyz.julianpeters.timedlists.adapters.*;
import xyz.julianpeters.timedlists.R;
import xyz.julianpeters.timedlists.helpers.StaticValues;

/**
 * Created by julian on 15.05.17.
 */

abstract class ListActivityBase extends BaseActivity {

    CursorRecyclerViewAdapter rvAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rvAdapter = getAdapter();
        ((RecyclerView)itemView).addItemDecoration(new ItemDivider(this, R.drawable.divider,
                (int)getResources().getDimension(R.dimen.item_divider_size)));
        ((RecyclerView)itemView).setAdapter(rvAdapter);
        ItemTouchHelper.Callback callback = new MyTouchHelper(rvAdapter, this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView((RecyclerView)itemView);
    }

    @Override
    int findLayout() {
        return R.layout.activity_lists;
    }

    @Override
    View setItemView() {
        return findViewById(R.id.recycler_view);
    }

    abstract CursorRecyclerViewAdapter getAdapter();

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        rvAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        rvAdapter.swapCursor(null);
    }

    abstract int getRowNumber();

    @Override
    public int adjustHeight() {
        return (int)getResources().getDimension(R.dimen.list_item_size);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (editIsActive) {
            createItemAnimation(true);
        }
    }

    @Override
    public int getHSV() {
        return Color.HSVToColor(StaticValues.hsvValues(rvAdapter.getItemCount()-1));
    }
}
