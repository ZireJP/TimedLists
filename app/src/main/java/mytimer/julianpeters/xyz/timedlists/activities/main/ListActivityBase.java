package mytimer.julianpeters.xyz.timedlists.activities.main;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import mytimer.julianpeters.xyz.timedlists.adapters.*;
import mytimer.julianpeters.xyz.timedlists.R;

/**
 * Created by julian on 15.05.17.
 */

abstract class ListActivityBase extends BaseActivity {

    CursorRecyclerViewAdapter rvAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rvAdapter = getAdapter();
        ((RecyclerView)itemView).setAdapter(rvAdapter);
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
}
