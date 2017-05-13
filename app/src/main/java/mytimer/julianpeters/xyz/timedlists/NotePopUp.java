package mytimer.julianpeters.xyz.timedlists;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by julian on 11.05.17.
 */

public class NotePopUp extends Activity {

    Uri uri;
    TextView title;
    EditText notes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_popup);

        notes = (EditText) findViewById(R.id.notes_text);
        title = (TextView) findViewById(R.id.notes_id);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        String _id = getIntent().getStringExtra("_id");
        String[] projection = {Item.Items.TITLE, Item.Items.NOTES};
        uri = Uri.parse(Item.Items.CONTENT_URI + "/" + _id);
        Cursor c = getContentResolver().query(uri, projection, null, null, null);
        if (c.moveToFirst()) {
            title.setText(getResources().getString(R.string.note_title, c.getString(c.getColumnIndex(Item.Items.TITLE))));
            notes.setText(c.getString(c.getColumnIndex(Item.Items.NOTES)));
            notes.setSelection(notes.getText().length());
        }
        c.close();

    }

    public void set(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        ContentValues values = new ContentValues();
        values.put(Item.Items.NOTES, notes.getText().toString());
        getContentResolver().update(uri, values, null, null);
        super.onDestroy();
    }
}
