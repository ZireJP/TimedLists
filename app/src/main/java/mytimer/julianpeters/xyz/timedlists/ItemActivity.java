package mytimer.julianpeters.xyz.timedlists;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
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
    RelativeLayout layout;
    View overlay;

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
        Point dimension = DisplayDimension.getDisplayDimensions(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(dimension.x, dimension.y));
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
        editText.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slidein);
        Animation anim2 = new TranslateAnimation(0, 0, -name.getMeasuredHeight(), 0);
        anim2.setDuration(400);
        anim2.setFillAfter(true);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                editText.clearAnimation();
                textView.clearAnimation();
                overlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        editText.startAnimation(anim);
        layout.startAnimation(anim2);
    }

    private void slideOutAnimation() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slideout);
        Animation anim2 = new TranslateAnimation(0, 0, 0, -editText.getHeight());
        anim2.setDuration(400);
        anim2.setFillAfter(true);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                overlay.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                editText.clearAnimation();
                layout.clearAnimation();
                editText.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        editText.startAnimation(anim);
        layout.startAnimation(anim2);
    }

    private boolean focusEdit() {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        return true;
    }

    private boolean unfocusEdit() {
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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
        if (editIsActive) {
            checkEdit();
        } else {
            v.setVisibility(View.INVISIBLE);
        }
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
                    checkEdit();
                }
            }
        };
    }

    private void checkEdit() {
        editIsActive = unfocusEdit();
        if (!editText.getText().toString().equals("")) {
            createItem();
        } else {
            slideOutAnimation();
        }
    }

    private void setTimeRange() {
        np1.setMaxValue(99);
        np2.setMaxValue(59);
        np3.setMaxValue(59);
    }

    private void findViews() {
        time = (Button) findViewById(R.id.item_button_left);
        right = (Button) findViewById(R.id.item_button_right);
        name = (TextView) findViewById(R.id.item_item_name);
        textView = (TextView) findViewById(R.id.item_tw_bottom);
        editText = (EditText) findViewById(R.id.item_edit_text);
        layout = (RelativeLayout) findViewById(R.id.item_layout);
        overlay = findViewById(R.id.item_overlay);
        np1 = (NumberPicker) findViewById(R.id.item_hours);
        np2 = (NumberPicker) findViewById(R.id.item_minute);
        np3 = (NumberPicker) findViewById(R.id.item_second);
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
        int second = Time.getSeconds(seconds);
        int minute = Time.getMinutes(seconds);
        int hour = Time.getHours(seconds);
        np1.setValue(hour);
        np2.setValue(minute);
        np3.setValue(second);
    }

    public void showNotes(View v) {
        if (editIsActive) {
            checkEdit();
        } else {
            Intent intent = new Intent(this, NotePopUp.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    public void runItem(View v) {
            String _id = getIntent().getStringExtra("id");
            Intent intent = new Intent(this, RunPopUp.class);
            intent.putExtra("_id", _id);
            startActivity(intent);
    }
}
