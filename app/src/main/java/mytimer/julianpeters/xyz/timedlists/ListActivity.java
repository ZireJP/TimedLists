package mytimer.julianpeters.xyz.timedlists;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by julian on 09.05.17.
 */

public class ListActivity extends MainActivity {

    private Button runButton;
    private Context context = this;
    private String _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _id = getIntent().getStringExtra("_id");
        Uri uri = Uri.parse(Item.Items.CONTENT_URI + "/" + _id);
        Cursor c = getContentResolver().query(uri, new String[]{Item.Items.TITLE}, null, null, null);
        c.moveToFirst();
        name.setText(c.getString(0));
        c.close();

        setEditable(name);
        runButton = (Button) findViewById(R.id.run_button);
        runButton.setOnClickListener(runButtonListener());
        runButton.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] proj = {ItemInItem.ItemInItems.ITEM_ID, ItemInItem.ItemInItems.NAME,
                ItemInItem.ItemInItems.FOREIGN_KEY, ItemInItem.ItemInItems.REPEAT};
        return new CursorLoader(this,
                ItemInItem.ItemInItems.CONTENT_URI,
                proj,
                "table_" + getIntent().getStringExtra("_id"),
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
        Uri content_uri = Uri.parse(ItemInItem.ItemInItems.CONTENT_URI + "/" + getIntent().getStringExtra("_id"));
        getContentResolver().insert(content_uri, values);
        editText.setText("");
        createItemAnimation();
    }

    private View.OnClickListener runButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RunPopUp.class);
                intent.putExtra("_id", _id);
                startActivity(intent);
            }
        };
    }

    @Override
    public void showNotes(View v) {
            Intent intent = new Intent(this, NotePopUp.class);
            intent.putExtra("_id", _id);
            startActivity(intent);
    }

    @Override
    protected void setEditable(View v) {
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editName(v);
                return true;
            }
        });
    }

    @Override
    public void editName(View v) {
        String editable = "editable placeholder";
        ((TextView)v).setText(editable);
    }


}
