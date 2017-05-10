package mytimer.julianpeters.xyz.timedlists;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

/**
 * Created by julian on 09.05.17.
 */

public class ListActivity extends MainActivity {

    private Button runButton;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private View.OnClickListener runButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _id = getIntent().getStringExtra("id");
                Intent intent = new Intent(context, RunPopUp.class);
                intent.putExtra("_id", _id);
                startActivity(intent);
            }
        };
    }



}
