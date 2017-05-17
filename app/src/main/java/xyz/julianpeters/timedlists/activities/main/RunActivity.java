package xyz.julianpeters.timedlists.activities.main;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
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
import java.util.Timer;
import java.util.TimerTask;

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
    Button clickToContinue;
    public int current;
    int secondsLeft;
    int totalTimeLeft;
    int totalNotTimedLeft;
    //Ringtone ringtone;
    TextView totalTime;
    ProgressBar bar;
    ListView listView;
    RunArrayAdapter list;
    Button stopwatchStop;
    Button stopwatchSave;
    Button stopwatchDiscard;
    boolean paused;
    long saveTime;
    int size;
    int runFor;
    CountDownTimer cd = null;
    StopWatch sw = null;
    ObjectAnimator animation;
    ImageButton pauseButton;
    MediaPlayer mp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        countdown = (TextView) findViewById(R.id.run_countdown);
        clickToContinue = (Button) findViewById(R.id.run_continue);
        bar = (ProgressBar) findViewById(R.id.run_progressbar);
        listView = (ListView) findViewById(R.id.run_listview);
        pauseButton = (ImageButton) findViewById(R.id.play_button);
        stopwatchDiscard = (Button) findViewById(R.id.stopwatch_discard);
        stopwatchStop = (Button) findViewById(R.id.stopwatch_stop);
        stopwatchSave = (Button) findViewById(R.id.stopwatch_save);
        totalTime = (TextView) findViewById(R.id.run_total_time);

        //Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //ringtone = RingtoneManager.getRingtone(this, notification);
        mp = MediaPlayer.create(this, R.raw.short_sound);

        setProgressbarSize();
        String _id = getIntent().getStringExtra("_id");
        newArray(_id);
        list = new RunArrayAdapter(this, R.layout.adapter_item_run, allItems);
        listView.setAdapter(list);
        size = allItems.size();
        current = 0;
        timer();
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
        if (sw != null) {
            sw.cancel();
        }
        clickToContinue.setVisibility(View.GONE);
        stopwatchStop.setVisibility(View.GONE);
        showSaveDiscard(View.GONE);
        current--;
        if (current < 0) {
            current = 0;
        }
        list.setCurrent(current);
        list.notifyDataSetChanged();
        listView.smoothScrollToPositionFromTop(current, 0, SCROLL_TIME);
        timer();
    }

    public void skip(View v) {
        if (cd != null) {
            cd.cancel();
            animation.cancel();
        }
        if (sw != null) {
            sw.cancel();
        }
        clickToContinue.setVisibility(View.GONE);
        stopwatchStop.setVisibility(View.GONE);
        showSaveDiscard(View.GONE);
        current++;
        if (current > size - 1) {
            current = size - 1;
        }
        list.setCurrent(current);
        list.notifyDataSetChanged();
        listView.smoothScrollToPositionFromTop(current, 0, SCROLL_TIME);
        timer();
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

    public void startStopwatch() {
        saveTime = 0;
        stopwatchStop.setVisibility(View.VISIBLE);
        sw = new StopWatch();
        sw.schedule(0);
    }

    public void stopwatchStop(View v) {
        runFor = sw.getRunFor();
        sw.cancel();
        stopwatchStop.setVisibility(View.GONE);
        showSaveDiscard(View.VISIBLE);
    }

    public int[] totalTimeLeft(int current) {
        int total = 0;
        int notTimed = 0;
        for (int i = current; i < size; i++) {
            int time = Integer.parseInt(allItems.get(i)[1]);
            total += time;
            if (time == 0) {
                notTimed++;
            }
        }
        return new int[]{total, notTimed};
    }

    public void setTotalTime(int time, int notTimed) {
        totalTime.setText(getResources().getString(
                R.string.total_time, Time.getTimeString(time), "" + notTimed));
    }

    public void setCurrentTotalTime() {
        int[] i = totalTimeLeft(current);
        totalTimeLeft = i[0];
        totalNotTimedLeft = i[1];
        setTotalTime(totalTimeLeft, totalNotTimedLeft);
    }

    public void stopwatchSave(View v) {
        current++;
        list.setCurrent(current);
        list.notifyDataSetChanged();
        listView.smoothScrollToPositionFromTop(current, 0, SCROLL_TIME);
        showSaveDiscard(View.GONE);
        //TODO UPDATE TABLE
        String _id = allItems.get(current - 1)[2]; // [2] is the string column, -1 because current already updated
        Bundle string = new Bundle();
        String t = getResources().getString(R.string.date_format, Time.getDate(), Time.getCurrentTime());
        string.putString("string", Item.Items.NOTES + " || \"" + t + Time.getTimeString(runFor) + "\"");
        getContentResolver().call(Item.Items.CONTENT_URI, "appendNotes", _id, string);
        timer();
    }

    public void stopwatchDiscard(View v) {
        current++;
        showSaveDiscard(View.GONE);
        list.setCurrent(current);
        list.notifyDataSetChanged();
        listView.smoothScrollToPositionFromTop(current, 0, SCROLL_TIME);
        timer();
    }

    void showSaveDiscard(int visibility) {
        stopwatchSave.setVisibility(visibility);
        stopwatchDiscard.setVisibility(visibility);
    }

    private void timer() {
        String[] item = allItems.get(current);
        setCurrentTotalTime();
        if (item[1].equals("0")) {
            startStopwatch();

            /*saveTime = 0;
            clickToContinue.setText(item[0] + "\n" + getString(R.string.run_continue));
            clickToContinue.setVisibility(View.VISIBLE);*/
        } else {
            secondsLeft = 0;
            int time = Integer.parseInt(item[1]);
            setProgressMax(bar, 1000);
            setProgressAnimate(bar, time * 1000);
            cd = new MyCd(time * 1000);
            cd.start();
        }
    }

    private class StopWatch extends Timer {

        int runFor;
        String name;
        final MyTask task;

        StopWatch() {
            runFor = 0;
            name = allItems.get(current)[0];
            task = new MyTask();
        }

        class MyTask extends TimerTask {

            Runnable runnable;
            boolean stop = false;

            public MyTask() {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        stopwatchStop.setText(getResources().getString(R.string.stopwatch_stop, name + "\n" + Time.getTimeString(runFor)));
                    }
                };
            }

            @Override
            public boolean cancel() {
                stop = true;
                return super.cancel();
            }

            @Override
            public void run() {
                while (!stop) {
                    while (paused) {
                        SystemClock.sleep(250);
                    }
                    runOnUiThread(runnable);
                    SystemClock.sleep(1000);
                    runFor++;
                }
            }
        }

        void schedule(long delay) {
            schedule(task, delay);
        }

        @Override
        public void cancel() {
            task.cancel();
            super.cancel();
        }

        public int getRunFor() {
            return runFor;
        }
    }

    private class MyCd extends CountDownTimer {

        String name;

        MyCd(long millisInFuture) {
            super(millisInFuture, 250);
            name = allItems.get(current)[0];
            secondsLeft = Integer.parseInt(allItems.get(current)[1]);
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
                countdown.setText(name + "\n" + Time.getTimeString(secondsLeft));
                totalTimeLeft -= 1;
                setTotalTime(totalTimeLeft, totalNotTimedLeft);
            }
        }

        @Override
        public void onFinish() {
            //ringtone.play();
            mp.start();
            if (current < size - 1) {
                current++;
                list.setCurrent(current);
                list.notifyDataSetChanged();
                listView.smoothScrollToPositionFromTop(current, 0, SCROLL_TIME);
                timer();
            } else {
                countdown.setText("Finished");
            }
        }
    }

    public void clickToContinue(View v) {
        clickToContinue.setVisibility(View.GONE);
        if (current < size - 1) {
            current++;
            list.setCurrent(current);
            list.notifyDataSetChanged();
            listView.smoothScrollToPositionFromTop(current, 0, SCROLL_TIME);
            timer();
        } else {
            countdown.setText("Finished");
        }
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
            Log.d("NullPointerException", "countdown already finished");
        }
        try {
            sw.cancel();
        } catch (NullPointerException e) {
            Log.d("NullPointerException", "stopwatch already finished");
        }
        super.onDestroy();
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
