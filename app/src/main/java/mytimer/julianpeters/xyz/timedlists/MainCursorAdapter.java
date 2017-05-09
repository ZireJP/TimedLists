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

public class MainCursorAdapter extends SimpleCursorAdapter {

    public MainCursorAdapter(Context context, Cursor c) {
        super(context, R.layout.list_item, c, new String[]{Item.Items.TITLE}, new int[]{R.id.list_label}, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Context con = context;
        final String id = cursor.getString(cursor.getColumnIndex(Item.Items.ITEM_ID));
        final String name = cursor.getString(cursor.getColumnIndex(Item.Items.TITLE));
        Button title = (Button) view.findViewById(R.id.list_label);
        Button del = (Button) view.findViewById(R.id.list_button);

        title.setText(name);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchActivity2 = new Intent(con, ItemActivity.class);
                launchActivity2.putExtra("id", id);
                con.startActivity(launchActivity2);
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sel = Item.Items.ITEM_ID + " = ?";
                con.getContentResolver().delete(Item.Items.CONTENT_URI, sel, new String[]{id});
            }
        });

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((Activity) context).getLayoutInflater().inflate(R.layout.list_item, parent, false);
    }
}
