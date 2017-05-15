package mytimer.julianpeters.xyz.timedlists.Activities.MainActivities;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import mytimer.julianpeters.xyz.timedlists.CustomAdapters.CursorRecyclerViewAdapter;
import mytimer.julianpeters.xyz.timedlists.CustomAdapters.SubListCursorAdapter;
import mytimer.julianpeters.xyz.timedlists.providers.ProviderHelperClasses.Item;
import mytimer.julianpeters.xyz.timedlists.providers.ProviderHelperClasses.ItemInItem;

/**
 * Created by julian on 15.05.17.
 */

public class ListActivitySub extends ListActivityBase {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    CursorRecyclerViewAdapter getAdapter() {
        return new SubListCursorAdapter(this, null);
    }

    @Override
    void setTitle() {
        Cursor c = getContentResolver().query(Item.Items.getIdUri(_id),
                new String[]{Item.Items.TITLE}, null, null, null);
        c.moveToFirst();
        titleView.setText(c.getString(0));
        c.close();
    }

    @Override
    String setId() {
        return getIntent().getStringExtra("_id");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ItemInItem.ItemInItems.ITEM_ID, ItemInItem.ItemInItems.NAME,
                ItemInItem.ItemInItems.FOREIGN_KEY, ItemInItem.ItemInItems.REPEAT};
        return new CursorLoader(this,
                ItemInItem.ItemInItems.getContentUri(_id),
                projection,
                null,
                null,
                null);
    }

    @Override
    public Uri createItem() {
        Uri uri = super.createItem();
        String foreignKey = uri.getLastPathSegment();
        ContentValues values = new ContentValues();
        values.put(Item.Items.IS_LIST, true);
        getContentResolver().update(Item.Items.getIdUri(_id), values, null, null);
        values = new ContentValues();
        values.put(ItemInItem.ItemInItems.FOREIGN_KEY, foreignKey);
        values.put(ItemInItem.ItemInItems.REPEAT, 1);
        return getContentResolver().insert(ItemInItem.ItemInItems.getContentUri(_id), values);
    }
}
