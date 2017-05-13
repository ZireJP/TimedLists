package mytimer.julianpeters.xyz.timedlists;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by julian on 10.05.17.
 */

public class Popup extends Activity {

    NumberPicker np;
    String _id;
    String table_id;
    Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popup);
        np = (NumberPicker) findViewById(R.id.popup_picker);
        np.setMinValue(0);
        np.setMaxValue(99);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Float d = getResources().getDimension(R.dimen.list_item_size);
        Float b = getResources().getDimension(R.dimen.border_size);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * .8), Math.round(4*d+ 2*b));
        table_id = getIntent().getStringExtra("table_id");
        _id = getIntent().getStringExtra("_id");
        uri = Uri.parse(ItemInItem.ItemInItems.CONTENT_URI + "/" + table_id);
        Cursor c = getContentResolver().query(uri, new String[]{ItemInItem.ItemInItems.REPEAT}, null, new String[]{_id}, null);
        c.moveToFirst();
        int repeat = c.getInt(0);
        c.close();
        np.setValue(repeat);
    }

    public void setRepeatTime(View v) {
        ContentValues values = new ContentValues();
        String[] id = {getIntent().getStringExtra("_id")};
        values.put("table_id", ItemInItem.ItemInItems.table(getIntent().getStringExtra("table_id")));
        values.put(ItemInItem.ItemInItems.REPEAT, np.getValue());
        String selection = ItemInItem.ItemInItems.ITEM_ID + " = ?";
        getContentResolver().update(ItemInItem.ItemInItems.CONTENT_URI, values, selection, id);
        finish();
    }

}
