package mytimer.julianpeters.xyz.timedlists;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {

    private String id;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        id = (String) getIntent().getSerializableExtra("id");
        String[] proj = {Item.Items.ITEM_ID, Item.Items.TITLE, Item.Items.TIME};
        String sel = Item.Items.ITEM_ID + " = ?";
        String[] args = {id};
        Cursor cursor = getContentResolver().query(Item.Items.CONTENT_URI, proj, sel, args, null);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(Item.Items.TITLE));
        txt = (TextView) findViewById(R.id.textView);
        txt.setText(name);
    }
}
