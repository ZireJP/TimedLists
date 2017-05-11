package mytimer.julianpeters.xyz.timedlists;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

public class ItemActivity extends Activity {

    String id;
    Button time;
    Button right;
    TextView name;
    TextView textView;
    Boolean editIsActive;
    EditText editText;
    NumberPicker np1, np2, np3;
    Button set;
    Button cancel;
    NumberPicker empty;
    Button empty2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        id = getIntent().getStringExtra("id");
        findViews();
        setTimeRange();
        editText.setVisibility(View.GONE);
        editIsActive = false;
        textView.setOnClickListener(notListListener());
        setText(getItemCursor());
    }

    private Cursor getItemCursor() {
        String[] projection = {Item.Items.TITLE, Item.Items.TIME};
        String selection = Item.Items._ID + " = ?";
        return getContentResolver().query(Item.Items.CONTENT_URI, projection, selection, new String[]{id}, null);
    }

    private void setText(Cursor c) {
        c.moveToFirst();
        setTimeText(c.getInt(c.getColumnIndex(Item.Items.TIME)));
        name.setText(c.getString(c.getColumnIndex(Item.Items.TITLE)));
    }

    private void slideInAnimation() {
        switchVisibility(View.GONE, View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slidein);
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.pushdown);
        editText.startAnimation(anim);
        textView.startAnimation(anim2);
    }

    private void slideOutAnimation() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slideout);
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.pushup);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                switchVisibility(View.VISIBLE, View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        editText.startAnimation(anim);
        textView.startAnimation(anim2);
    }

    private boolean focusEdit() {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        return true;
    }

    private boolean unfocusEdit(View v) {
        v.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        return false;
    }

    protected void createItem() {
        ContentValues values = new ContentValues();
        values.put(Item.Items.TITLE, editText.getText().toString());
        values.put(Item.Items.TIME, 0);
        values.put(Item.Items.IS_LIST, false);
        Uri content_uri = Uri.parse(ItemInItem.ItemInItems.CONTENT_URI + "/" + getIntent().getStringExtra("id"));
        getContentResolver().insert(content_uri, values);
        ContentValues type = new ContentValues();
        type.put(Item.Items.IS_LIST, true);
        Uri uri = Uri.parse(Item.Items.CONTENT_URI + "/" + id);
        getContentResolver().update(uri, type, null, null);
        editText.setText("");
        createItemAnimation();
    }

    public void showTimeSpinner(View v) {
        v.setVisibility(View.INVISIBLE);
    }

    protected void createItemAnimation() {
        Intent launchActivity = new Intent(this, ListActivity.class);
        launchActivity.putExtra("id", id);
        startActivity(launchActivity);
        finish();
    }

    private View.OnClickListener notListListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!editIsActive) {
                    slideInAnimation();
                    editIsActive = focusEdit();
                } else {
                    editIsActive = unfocusEdit(v);
                    if (!editText.getText().toString().equals("")) {
                        createItem();
                    } else {
                        slideOutAnimation();
                    }
                }
            }
        };
    }

    private void switchVisibility(int from, int to) {
        editText.setVisibility(to);
        name.setVisibility(from);
        time.setVisibility(from);
        right.setVisibility(from);
        np1.setVisibility(from);
        np2.setVisibility(from);
        np3.setVisibility(from);
        set.setVisibility(from);
        cancel.setVisibility(from);
        empty.setVisibility(from);
        empty2.setVisibility(from);
    }

    private void setTimeRange() {
        np1.setMaxValue(99);
        np2.setMaxValue(59);
        np3.setMaxValue(59);
    }

    private void findViews() {
        time = (Button) findViewById(R.id.item_button_left);
        right = (Button) findViewById(R.id.item_button_right);
        name = (TextView) findViewById(R.id.item_name);
        textView = (TextView) findViewById(R.id.item_tw_bottom);
        editText = (EditText) findViewById(R.id.item_edit_text);
        np1 = (NumberPicker) findViewById(R.id.item_hours);
        np2 = (NumberPicker) findViewById(R.id.item_minute);
        np3 = (NumberPicker) findViewById(R.id.item_second);
        set = (Button) findViewById(R.id.item_set);
        cancel = (Button) findViewById(R.id.item_cancel);
        empty = (NumberPicker) findViewById(R.id.item_empty_height);
        empty2 = (Button) findViewById(R.id.item_empty_height2);
    }

    public void cancel(View w) {
        time.setVisibility(View.VISIBLE);
    }

    public void setTime(View w) {
        int hour = np1.getValue();
        int minute = np2.getValue();
        int second = np3.getValue();
        int inSeconds = Time.getSeconds(hour, minute, second);
        ContentValues value = new ContentValues();
        value.put(Item.Items.TIME, inSeconds);
        String selection = Item.Items.ITEM_ID + " = ?";
        getContentResolver().update(Item.Items.CONTENT_URI, value, selection, new String[]{id});
        setTimeText(inSeconds);
        time.setVisibility(View.VISIBLE);
    }

    private void setTimeText(int seconds) {
        time.setText(Time.getTimeString(seconds));
    }
}
