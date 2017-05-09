package mytimer.julianpeters.xyz.timedlists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        final String id = cursor.getString(cursor.getColumnIndex(ItemInItem.ItemInItems.ITEM_ID));
        final String time = cursor.getString(cursor.getColumnIndex(ItemInItem.ItemInItems.REPEAT));
        final String name = cursor.getString(cursor.getColumnIndex(ItemInItem.ItemInItems.NAME));
        final String foreign = cursor.getString(cursor.getColumnIndex(ItemInItem.ItemInItems.FOREIGN_KEY));
        Button title = (Button) view.findViewById(R.id.list_label);
        Button repeat = (Button) view.findViewById(R.id.list_repeat);
        Button del = (Button) view.findViewById(R.id.list_button);

        title.setText(name + " (" + id + ")");
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchActivity2 = new Intent(con, ListActivity.class);
                launchActivity2.putExtra("id", foreign);
                con.startActivity(launchActivity2);
            }
        });

        repeat.setText(time);

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sel = Item.Items.ITEM_ID + " = ?";
                String inItemSel = ItemInItem.ItemInItems.ITEM_ID + " = ?";
                con.getContentResolver().delete(Item.Items.CONTENT_URI, sel, new String[]{foreign});
                String table = "table_" + ((Activity)con).getIntent().getStringExtra("id");
                con.getContentResolver().delete(ItemInItem.ItemInItems.CONTENT_URI, inItemSel, new String[]{table, id});
            }
        });

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((Activity) context).getLayoutInflater().inflate(R.layout.list_item, parent, false);
    }
}
