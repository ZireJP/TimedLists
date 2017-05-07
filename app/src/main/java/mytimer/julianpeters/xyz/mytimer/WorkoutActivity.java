package mytimer.julianpeters.xyz.mytimer;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class WorkoutActivity extends AppCompatActivity {

    private int id;
    private TextView txt;
    private ItemDb mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        mDbHelper = new ItemDb(this);
        mDbHelper.open();
        id = (int) getIntent().getSerializableExtra("id");
        Log.d("id", Integer.toString(id));
        Cursor cursor = mDbHelper.selectItem(id);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(mDbHelper.COL_ITEM_NAME));
        txt = (TextView) findViewById(R.id.textView);
        txt.setText(name);
    }


    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }
}
