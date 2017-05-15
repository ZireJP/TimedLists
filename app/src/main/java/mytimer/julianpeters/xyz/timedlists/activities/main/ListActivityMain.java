package mytimer.julianpeters.xyz.timedlists.activities.main;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import mytimer.julianpeters.xyz.timedlists.adapters.*;
import mytimer.julianpeters.xyz.timedlists.adapters.MainCursorAdapter;
import mytimer.julianpeters.xyz.timedlists.R;
import mytimer.julianpeters.xyz.timedlists.providers.helpers.Item;

/**
 * Created by julian on 15.05.17.
 */

public class ListActivityMain extends ListActivityBase {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showNotes.setEnabled(false);
        titleView.setEnabled(false);
        runButton.setVisibility(View.GONE);
    }

    @Override
    CursorRecyclerViewAdapter getAdapter() {
        return new MainCursorAdapter(this, null);
    }

    @Override
    void setTitle() {
        titleView.setText(getResources().getString(R.string.main_title));
    }

    @Override
    String setId() {
        return null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] proj = {Item.Items.ITEM_ID, Item.Items.TITLE, Item.Items.IS_LIST};
        String selection = Item.Items.TAG + " = ?";
        String[] selectionArgs = {"fav"};
        return new CursorLoader(this,
                Item.Items.CONTENT_URI,
                proj,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = super.getContentValues();
        values.put(Item.Items.TAG, "fav");
        return values;
    }
}
