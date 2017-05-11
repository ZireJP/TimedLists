package mytimer.julianpeters.xyz.timedlists;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by julian on 10.05.17.
 */

public class RunPopUp extends Activity {

    ArrayList<String[]> allItems;
    TextView textView;
    TextView countdown;
    Button run_continue;
    int secondsLeft;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        textView = (TextView) findViewById(R.id.run_text);
        countdown = (TextView) findViewById(R.id.run_countdown);
        run_continue = (Button) findViewById(R.id.run_continue);

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
        int max = allItems.size();
        String[] item = allItems.get(0);
        timer(item, 0, max);
    }

    private void timer(final String[] item, final int i, final int items) {
        if (item[1].equals("0")) {
            run_continue.setOnClickListener(newClick(i, items));
            run_continue.setVisibility(View.VISIBLE);
        } else {
            secondsLeft = 0;
            CountDownTimer cd = new CountDownTimer(Integer.parseInt(item[1]) * 1000, 250) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (Math.round((float) millisUntilFinished / 1000.0f) != secondsLeft) {
                        secondsLeft = Math.round((float) millisUntilFinished / 1000.0f);
                        countdown.setText(item[0] + " : " + secondsLeft);
                    }
                }

                @Override
                public void onFinish() {
                    if (i < items - 1) {
                        final String[] item = allItems.get(i + 1);
                        timer(item, i + 1, items);
                    } else {
                        countdown.setText("Finished");
                    }
                }
            };
            cd.start();
        }
    }
    private View.OnClickListener newClick(final int i, final int items) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run_continue.setVisibility(View.GONE);
                if (i < items-1) {
                    final String[] item = allItems.get(i + 1);
                    timer(item, i+1, items);
                } else {
                    countdown.setText("Finished");
                }
            }
        };
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
