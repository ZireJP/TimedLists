package mytimer.julianpeters.xyz.timedlists;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by julian on 10.05.17.
 */

public class Popup extends Activity {

    NumberPicker np;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popup);
        np = (NumberPicker) findViewById(R.id.popup_picker);
        np.setMinValue(0);
        np.setMaxValue(99);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * .8), (int)(height * .6));
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
