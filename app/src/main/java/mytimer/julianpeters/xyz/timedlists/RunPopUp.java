package mytimer.julianpeters.xyz.timedlists;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    public int current;
    int secondsLeft;
    boolean active;
    Ringtone ringtone;
    ProgressBar bar;
    ListView listView;
    RunListAdapter list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        countdown = (TextView) findViewById(R.id.run_countdown);
        run_continue = (Button) findViewById(R.id.run_continue);
        bar = (ProgressBar) findViewById(R.id.run_progressbar);
        listView = (ListView) findViewById(R.id.run_listview);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(this, notification);

        active = true;
        String _id = getIntent().getStringExtra("_id");
        newArray(_id);
        list = new RunListAdapter(this, R.layout.run_list, allItems);
        listView.setAdapter(list);
        int max = allItems.size();
        String[] item = allItems.get(0);
        timer(item, 0, max);
    }

    private void timer(final String[] item, final int i, final int items) {
        if (item[1].equals("0")) {
            run_continue.setOnClickListener(newClick(i, items));
            run_continue.setText(item[0] + "\n" + getString(R.string.run_continue));
            run_continue.setVisibility(View.VISIBLE);
        } else {
            secondsLeft = 0;
            int time = Integer.parseInt(item[1]);
            setProgressMax(bar, 1000);
            setProgressAnimate(bar, time);
            CountDownTimer cd = new CountDownTimer(Integer.parseInt(item[1]) * 1000, 250) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (Math.round((float) millisUntilFinished / 1000.0f) != secondsLeft) {
                        secondsLeft = Math.round((float) millisUntilFinished / 1000.0f);
                        countdown.setText(item[0] + "\n" + Time.getTimeString(secondsLeft));
                    }
                }

                @Override
                public void onFinish() {
                    if (active) {
                        ringtone.play();
                        if (i < items - 1) {
                            final String[] item = allItems.get(i + 1);
                            current++;
                            list.setCurrent(current);
                            list.notifyDataSetChanged();
                            listView.setSelection(current);
                            timer(item, i + 1, items);
                        } else {
                            countdown.setText("Finished");
                        }
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
                    current++;
                    list.setCurrent(current);
                    list.notifyDataSetChanged();
                    listView.setSelection(current);
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
        String[] projection = {Item.Items.ITEM_ID, Item.Items.TITLE, Item.Items.TIME, Item.Items.IS_LIST};
        String selection = Item.Items.ITEM_ID + " = ?";
        Cursor cursor = getContentResolver().query(Item.Items.CONTENT_URI, projection, selection, new String[]{_id}, null);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(Item.Items.TITLE));
        if (cursor.getInt(cursor.getColumnIndex(Item.Items.IS_LIST)) == 0) {
            for(int i = 0; i < repeat; i++) {
                allItems.add(new String[]{name, cursor.getString(cursor.getColumnIndex(Item.Items.TIME)), cursor.getString(cursor.getColumnIndex(Item.Items.ITEM_ID))});
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

    @Override
    protected void onDestroy() {
        active = false;
        super.onDestroy();
    }

    private void setProgressMax(ProgressBar pb, int max) {
        pb.setMax(max);
        pb.setProgress(max);
    }

    private void setProgressAnimate(ProgressBar pb, int duration)
    {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", pb.getProgress(), -1);
        animation.setDuration(duration*1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public void stop(View v) {
        finish();
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }
}
