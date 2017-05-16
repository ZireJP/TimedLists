package xyz.julianpeters.timedlists.activities.main;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.julianpeters.timedlists.helpers.DisplayDimension;
import xyz.julianpeters.timedlists.helpers.Time;
import xyz.julianpeters.timedlists.providers.helpers.Item;
import xyz.julianpeters.timedlists.providers.helpers.ItemInItem;
import xyz.julianpeters.timedlists.R;
import xyz.julianpeters.timedlists.adapters.RunArrayAdapter;

/**
 * Created by julian on 10.05.17.
 */

public class RunActivity extends Activity {

    int SCROLL_TIME = 200;

    ArrayList<String[]> allItems;
    TextView countdown;
    Button run_continue;
    public int current;
    int secondsLeft;
    Ringtone ringtone;
    ProgressBar bar;
    ListView listView;
    RunArrayAdapter list;
    boolean paused;
    long saveTime;
    int size;
    CountDownTimer cd = null;
    ObjectAnimator animation;
    ImageButton pauseButton;
    MediaPlayer mp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        countdown = (TextView) findViewById(R.id.run_countdown);
        run_continue = (Button) findViewById(R.id.run_continue);
        bar = (ProgressBar) findViewById(R.id.run_progressbar);
        listView = (ListView) findViewById(R.id.run_listview);
        pauseButton = (ImageButton) findViewById(R.id.play_button);

        //Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //ringtone = RingtoneManager.getRingtone(this, notification);
        mp = MediaPlayer.create(this, R.raw.short_sound);

        setProgressbarSize();
        String _id = getIntent().getStringExtra("_id");
        newArray(_id);
        list = new RunArrayAdapter(this, R.layout.adapter_item_run, allItems);
        listView.setAdapter(list);
        int max = allItems.size();
        size = allItems.size();
        String[] item = allItems.get(0);
        timer(item, 0, max);
    }

    private void setProgressbarSize() {
        Point height = DisplayDimension.getDisplayDimensions(this);
        int h = (height.y - ((int) getResources().getDimension(R.dimen.list_item_size) * 2)) / 2;
        bar.setLayoutParams(new LinearLayout.LayoutParams(h, h));
    }

    public void back(View v) {
        if (cd != null) {
            cd.cancel();
            animation.cancel();
        }
        run_continue.setVisibility(View.GONE);
        current--;
        if (current < 0) {
            current = 0;
        }
        list.setCurrent(current);
        list.notifyDataSetChanged();
        listView.smoothScrollToPositionFromTop(current, 0, SCROLL_TIME);
        timer(allItems.get(current), current, size);
    }

    public void skip(View v) {
        if (cd != null) {
            cd.cancel();
            animation.cancel();
        }
        run_continue.setVisibility(View.GONE);
        current++;
        if (current > size-1) {
            current = size - 1;
        }
        list.setCurrent(current);
        list.notifyDataSetChanged();
        listView.smoothScrollToPositionFromTop(current, 0, SCROLL_TIME);
        timer(allItems.get(current), current, size);
    }

    public void pause(View v) {
        if (paused) {
            pauseButton.setImageDrawable(getDrawable(R.drawable.pause));
            if (saveTime != 0) {
                cd = new MyCd(saveTime).start();
                setProgressAnimate(bar, saveTime);
            }
        } else {
            pauseButton.setImageDrawable(getDrawable(R.drawable.play));
        }
        paused = !paused;
    }

    private void timer(final String[] item, final int i, final int items) {
        if (item[1].equals("0")) {
            saveTime = 0;
            run_continue.setOnClickListener(newClick(i, items));
            run_continue.setText(item[0] + "\n" + getString(R.string.run_continue));
            run_continue.setVisibility(View.VISIBLE);
        } else {
            secondsLeft = 0;
            int time = Integer.parseInt(item[1]);
            setProgressMax(bar, 1000);
            setProgressAnimate(bar, time * 1000);
            cd = new MyCd(Integer.parseInt(item[1]) * 1000);
            cd.start();
        }
    }

    private class MyCd extends CountDownTimer {

        MyCd(long millisInFuture) {
            super(millisInFuture, 250);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (paused) {
                //
                this.cancel();
                animation.cancel();
                saveTime = millisUntilFinished;
            }
            if (Math.round((float) millisUntilFinished / 1000.0f) != secondsLeft) {
                secondsLeft = Math.round((float) millisUntilFinished / 1000.0f);
                countdown.setText(allItems.get(current)[0] + "\n" + Time.getTimeString(secondsLeft));
            }
        }

        @Override
        public void onFinish() {
            //ringtone.play();
            mp.start();
            if (current < size - 1) {
                final String[] item = allItems.get(current + 1);
                current++;
                list.setCurrent(current);
                list.notifyDataSetChanged();
                listView.smoothScrollToPositionFromTop(current, 0, SCROLL_TIME);
                timer(item, current, size);
            } else {
                countdown.setText("Finished");
            }
        }
    }

    private View.OnClickListener newClick(final int i, final int items) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run_continue.setVisibility(View.GONE);
                if (i < items - 1) {
                    final String[] item = allItems.get(i + 1);
                    current++;
                    list.setCurrent(current);
                    list.notifyDataSetChanged();
                    listView.smoothScrollToPositionFromTop(current, 0, SCROLL_TIME);
                    timer(item, i + 1, items);
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
        return getContentResolver().query(ItemInItem.ItemInItems.CONTENT_URI, projection, table_id, null, ItemInItem.ItemInItems.ORDER);
    }

    private void fillArray(String _id, int repeat) {
        String[] projection = {Item.Items.ITEM_ID, Item.Items.TITLE, Item.Items.TIME, Item.Items.IS_LIST};
        String selection = Item.Items.ITEM_ID + " = ?";
        Cursor cursor = getContentResolver().query(Item.Items.CONTENT_URI, projection, selection, new String[]{_id}, null);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(Item.Items.TITLE));
        if (cursor.getInt(cursor.getColumnIndex(Item.Items.IS_LIST)) == 0) {
            for (int i = 0; i < repeat; i++) {
                allItems.add(new String[]{name, cursor.getString(cursor.getColumnIndex(Item.Items.TIME)), cursor.getString(cursor.getColumnIndex(Item.Items.ITEM_ID))});
            }
        } else {
            cursor.close();
            cursor = getAllItems("table_" + _id);
            for (int i = 0; i < repeat; i++) {
                if (cursor.moveToFirst()) {
                    do {
                        fillArray(cursor.getString(cursor.getColumnIndex(ItemInItem.ItemInItems.FOREIGN_KEY)),
                                cursor.getInt(cursor.getColumnIndex(ItemInItem.ItemInItems.REPEAT)));
                    } while (cursor.moveToNext());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            cd.cancel();
        } catch (NullPointerException e) {
            Log.d("NullPointerException" ,"timer already finished");
        } finally {
            super.onDestroy();
        }
    }

    private void setProgressMax(ProgressBar pb, int max) {
        pb.setMax(max);
        pb.setProgress(max);
    }

    private void setProgressAnimate(ProgressBar pb, long duration) {
        animation = ObjectAnimator.ofInt(pb, "progress", pb.getProgress(), -1);
        animation.setDuration(duration);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public void stop(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setProgressbarSize();
    }
}
