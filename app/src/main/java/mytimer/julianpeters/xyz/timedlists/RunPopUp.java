package mytimer.julianpeters.xyz.timedlists;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by julian on 10.05.17.
 */

public class RunPopUp extends Activity {

    ArrayList<String[]> allItems;
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        textView = (TextView) findViewById(R.id.run_text);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));
        String _id = getIntent().getStringExtra("_id");
        newArray(_id);
        String text = "";
        Log.d("Size", Integer.toString(allItems.size()));
        for (String[] duo : allItems) {
            text += duo[0] + " : " + duo[1] + "\n";
        }
        textView.setText(text);
    }

    private void newArray(String _id) {
        allItems = new ArrayList<>();
        fillArray(_id, 1);
    }

    private Cursor getAllItems(String table_id) {
        String[] projection = {ItemInItem.ItemInItems.FOREIGN_KEY,
                ItemInItem.ItemInItems.REPEAT};
        return getContentResolver().query(ItemInItem.ItemInItems.CONTENT_URI, projection, table_id, null, null);
    }

    private void fillArray(String _id, int repeat) {
        String[] projection = {Item.Items.TITLE, Item.Items.TIME, Item.Items.IS_LIST};
        String selection = Item.Items.ITEM_ID + " = ?";
        Cursor cursor = getContentResolver().query(Item.Items.CONTENT_URI, projection, selection, new String[]{_id}, null);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(Item.Items.TITLE));
        if (cursor.getInt(cursor.getColumnIndex(Item.Items.IS_LIST)) == 0) {
            for(int i = 0; i < repeat; i++) {
                allItems.add(new String[]{name, cursor.getString(cursor.getColumnIndex(Item.Items.TIME))});
            }
        } else {
            cursor.close();
            cursor = getAllItems("table_" + _id);
            for(int i = 0; i < repeat; i++) {
                if (cursor.moveToFirst()) {
                    do {
                        fillArray(cursor.getString(cursor.getColumnIndex(ItemInItem.ItemInItems.FOREIGN_KEY)),
                                cursor.getInt(cursor.getColumnIndex(ItemInItem.ItemInItems.REPEAT)));
                    } while(cursor.moveToNext());
                }
            }
        }

    }
}
