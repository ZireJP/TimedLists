package xyz.julianpeters.timedlists.activities.popup;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;

import xyz.julianpeters.timedlists.helpers.StaticValues;
import xyz.julianpeters.timedlists.views.CustomNumberPicker;
import xyz.julianpeters.timedlists.helpers.Time;
import xyz.julianpeters.timedlists.providers.helpers.Item;
import xyz.julianpeters.timedlists.R;

/**
 * Created by julian on 10.05.17.
 */

public class SetTimePopUp extends Activity {

    CustomNumberPicker hours;
    CustomNumberPicker minutes;
    CustomNumberPicker seconds;
    String _id;
    Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_activity_set_time);
        hours = (CustomNumberPicker) findViewById(R.id.set_item_hours);
        minutes = (CustomNumberPicker) findViewById(R.id.set_item_minutes);
        seconds = (CustomNumberPicker) findViewById(R.id.set_item_seconds);
        View set = findViewById(R.id.set_time);
        set.setBackgroundColor(Color.HSVToColor(StaticValues.hsvValues(0)));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Float d = getResources().getDimension(R.dimen.list_item_size);
        Float b = getResources().getDimension(R.dimen.border_size);
        int width = dm.widthPixels;

        getWindow().setLayout((int)(width * .8), Math.round(4*d+ 2*b));
        _id = getIntent().getStringExtra("_id");
        uri = Uri.parse(Item.Items.CONTENT_URI + "/" + _id);
        Cursor c = getContentResolver().query(uri, new String[]{Item.Items.TIME}, null, null, null);
        c.moveToFirst();
        int time = c.getInt(0);
        c.close();
        setValues(time);
    }

    public void setTime(View v) {
        ContentValues values = new ContentValues();
        int time = Time.getTimeInSeconds(hours.getValue(), minutes.getValue(), seconds.getValue());
        values.put(Item.Items.TIME, time);
        getContentResolver().update(uri, values, null, null);
        finish();
    }

    private void setValues(int time) {
        hours.setValue(Time.getHours(time));
        minutes.setValue(Time.getMinutes(time));
        seconds.setValue(Time.getSeconds(time));
    }
}

