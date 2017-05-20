package xyz.julianpeters.timedlists.activities.main;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import xyz.julianpeters.timedlists.activities.popup.SubCopyPopUp;
import xyz.julianpeters.timedlists.adapters.CursorRecyclerViewAdapter;
import xyz.julianpeters.timedlists.adapters.SubListCursorAdapter;
import xyz.julianpeters.timedlists.providers.helpers.Item;
import xyz.julianpeters.timedlists.providers.helpers.ItemInItem;

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
    int getRowNumber() {
        return 0;
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
                ItemInItem.ItemInItems.ORDER);
    }

    @Override
    public Uri createItem() {
        Uri uri = super.createItem();
        if (uri != null) {
            String foreignKey = uri.getLastPathSegment();
            ContentValues values = new ContentValues();
            values.put(Item.Items.IS_LIST, true);
            getContentResolver().update(Item.Items.getIdUri(_id), values, null, null);
            values = new ContentValues();
            values.put(ItemInItem.ItemInItems.FOREIGN_KEY, foreignKey);
            values.put(ItemInItem.ItemInItems.REPEAT, 1);
            int rows = getContentResolver().call(Item.Items.CONTENT_URI, "getRows", _id, null).getInt("rows");
            values.put(ItemInItem.ItemInItems.ORDER, rows);
            return getContentResolver().insert(ItemInItem.ItemInItems.getContentUri(_id), values);
        }
        return null;
    }

    @Override
    void startCopyPop() {
        completeOverlay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, SubCopyPopUp.class);
        intent.putExtra("name", newEditText.getText().toString());
        intent.putExtra("table_id", _id);
        startActivity(intent);
    }
}
