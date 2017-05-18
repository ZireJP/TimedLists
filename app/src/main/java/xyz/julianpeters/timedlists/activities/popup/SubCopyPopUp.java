package xyz.julianpeters.timedlists.activities.popup;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import xyz.julianpeters.timedlists.helpers.ValuesForItems;
import xyz.julianpeters.timedlists.providers.helpers.Item;
import xyz.julianpeters.timedlists.providers.helpers.ItemInItem;

/**
 * Created by julian on 18.05.17.
 */

public class SubCopyPopUp extends CopyPopUp {

    String table_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        table_id = getIntent().getStringExtra("table_id");
    }

    @Override
    public void newItem(View v) {
        super.newItem(v);
        String foreignKey = uriId.getLastPathSegment();
        ContentValues values = new ContentValues();
        values.put(Item.Items.IS_LIST, true);
        getContentResolver().update(Item.Items.getIdUri(table_id), values, null, null);
        values = new ContentValues();
        values.put(ItemInItem.ItemInItems.FOREIGN_KEY, foreignKey);
        values.put(ItemInItem.ItemInItems.REPEAT, 1);
        int rows = getContentResolver().call(Item.Items.CONTENT_URI, "getRows", table_id, null).getInt("rows");
        values.put(ItemInItem.ItemInItems.ORDER, rows);
        getContentResolver().insert(ItemInItem.ItemInItems.getContentUri(table_id), values);
    }

    @Override
    public void link(View v) {
        cursor.moveToPosition(selected);
        String _id = cursor.getString(0);
        int links = cursor.getInt(4);
        cursor.close();
        if (selectedView != null) {
            ContentValues values = new ContentValues();
            values.put(Item.Items.IS_LIST, true);
            getContentResolver().update(Item.Items.getIdUri(table_id), values, null, null);
            values = new ContentValues();
            values.put(Item.Items.LINKS, links + 1);
            getContentResolver().update(Item.Items.getIdUri(_id), values, null, null);
            values = new ContentValues();
            values.put(ItemInItem.ItemInItems.FOREIGN_KEY, _id);
            values.put(ItemInItem.ItemInItems.REPEAT, 1);
            int rows = getContentResolver().call(Item.Items.CONTENT_URI, "getRows", table_id, null).getInt("rows");
            values.put(ItemInItem.ItemInItems.ORDER, rows);
            getContentResolver().insert(ItemInItem.ItemInItems.getContentUri(table_id), values);
        }
        finish();
    }

    @Override
    protected ContentValues getContentValues(String name, int i) {
        return ValuesForItems.newItem(name);
    }
}
