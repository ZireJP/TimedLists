package mytimer.julianpeters.xyz.timedlists;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SimpleCursorAdapter;

/**
 * Created by julian on 09.05.17.
 */

public class ListCursorAdapter extends SimpleCursorAdapter {

    ListCursorAdapter(Context context, Cursor c) {
        super(context, R.layout.list_item, c, new String[]{ItemInItem.ItemInItems.NAME}, new int[]{R.id.list_label}, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Context con = context;
        final String table_id = ((Activity)con).getIntent().getStringExtra("id");
        final String id = cursor.getString(cursor.getColumnIndex(ItemInItem.ItemInItems.ITEM_ID));
        final String time = cursor.getString(cursor.getColumnIndex(ItemInItem.ItemInItems.REPEAT));
        final String foreign = cursor.getString(cursor.getColumnIndex(ItemInItem.ItemInItems.FOREIGN_KEY));
        Cursor foreignCursor = getItemCursor(con, foreign);
        foreignCursor.moveToFirst();
        final String name = foreignCursor.getString(foreignCursor.getColumnIndex(Item.Items.TITLE));
        final boolean isList = foreignCursor.getInt(foreignCursor.getColumnIndex(Item.Items.IS_LIST)) > 0;
        foreignCursor.close();
        Button title = (Button) view.findViewById(R.id.list_label);
        Button repeat = (Button) view.findViewById(R.id.list_repeat);
        Button del = (Button) view.findViewById(R.id.list_button);

        title.setText(name);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchActivity = whichActivity(con, isList);
                launchActivity.putExtra("id", foreign);
                con.startActivity(launchActivity);
            }
        });

        repeat.setText(time);
        repeat.setOnClickListener(showRepeatSetter(con, id, table_id));

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver resolver = con.getContentResolver();
                String sel = Item.Items.ITEM_ID + " = ?";
                String inItemSel = ItemInItem.ItemInItems.ITEM_ID + " = ?";
                resolver.call(Item.Items.CONTENT_URI, "deleteAllItemsOf", foreign, null);
                String table = "table_" + table_id;
                resolver.delete(ItemInItem.ItemInItems.CONTENT_URI, inItemSel, new String[]{table, id});
                Bundle rows = resolver.call(Item.Items.CONTENT_URI, "getRows", table, null);
                int i = rows.getInt("rows");
                if (i==0) {
                    ContentValues type = new ContentValues();
                    type.put(Item.Items.IS_LIST, false);
                    String selection = Item.Items._ID + " = ?";
                    resolver.update(Item.Items.CONTENT_URI, type, selection, new String[]{table_id});
                    Intent launchActivity = new Intent(con, ItemActivity.class);
                    launchActivity.putExtra("id", table_id);
                    con.startActivity(launchActivity);
                    ((Activity) con).finish();
                }
            }
        });

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((Activity) context).getLayoutInflater().inflate(R.layout.list_item, parent, false);
    }

    private Intent whichActivity(Context context, Boolean isList) {
        if (isList) {
            return new Intent(context, ListActivity.class);
        } else {
            return new Intent(context, ItemActivity.class);
        }
    }

    private Cursor getItemCursor(Context context, String foreign) {
        ContentResolver resolver = context.getContentResolver();
        String[] projection = {Item.Items.TITLE, Item.Items.IS_LIST};
        String selection = Item.Items._ID + " = ?";
        String[] args = {foreign};
        return resolver.query(Item.Items.CONTENT_URI, projection, selection, args, null);
    }

    public View.OnClickListener showRepeatSetter(final Context con, final String _id, final String table_id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con, Popup.class);
                intent.putExtra("_id", _id);
                intent.putExtra("table_id", table_id);
                con.startActivity(intent);
            }
        };
    }

}
