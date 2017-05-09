package mytimer.julianpeters.xyz.timedlists;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

/**
 * Created by julian on 09.05.17.
 */

public class ListActivity extends MainActivity {

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] proj = {ItemInItem.ItemInItems.ITEM_ID, ItemInItem.ItemInItems.NAME,
                ItemInItem.ItemInItems.FOREIGN_KEY, ItemInItem.ItemInItems.REPEAT};
        return new CursorLoader(this,
                ItemInItem.ItemInItems.CONTENT_URI,
                proj,
                "table_" + getIntent().getStringExtra("id"),
                null,
                null);
    }

    @Override
    protected SimpleCursorAdapter adapter() {
        return new ListCursorAdapter(this, null);
    }

    @Override
    protected void createItem() {
        ContentValues values = new ContentValues();
        String name = editText.getText().toString();
        values.put(Item.Items.TITLE, name);
        values.put(Item.Items.TIME, 0);
        values.put(Item.Items.IS_LIST, false);
        values.put("table", "table_" + getIntent().getStringExtra("id"));
        getContentResolver().insert(ItemInItem.ItemInItems.CONTENT_URI, values);
        editText.setText("");
        createItemAnimation();

    }
}
