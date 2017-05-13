package mytimer.julianpeters.xyz.timedlists;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class ItemActivity extends Activity {

    String _id;
    TextView name;
    TextView textView;
    Boolean editIsActive;
    EditText editText;
    Button timeButton;
    ScrollView scrollView;
    View overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.list_switch_in, R.anim.list_switch_out);
        setContentView(R.layout.activity_item);
        _id = getIntent().getStringExtra("_id");
        findViews();
        editText.setVisibility(View.GONE);
        editIsActive = false;
        setText(getItemCursor());
    }

    private Cursor getItemCursor() {
        String[] projection = {Item.Items.TITLE, Item.Items.TIME};
        String selection = Item.Items._ID + " = ?";
        return getContentResolver().query(Item.Items.CONTENT_URI, projection, selection, new String[]{_id}, null);
    }

    private void setText(Cursor c) {
        c.moveToFirst();
        setTimeText(c.getInt(c.getColumnIndex(Item.Items.TIME)));
        name.setText(c.getString(c.getColumnIndex(Item.Items.TITLE)));
        c.close();
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
        scrollView.startAnimation(anim2);
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
                scrollView.clearAnimation();
                editText.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        editText.startAnimation(anim);
        scrollView.startAnimation(anim2);
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
        Uri content_uri = Uri.parse(ItemInItem.ItemInItems.CONTENT_URI + "/" + getIntent().getStringExtra("_id"));
        getContentResolver().insert(content_uri, values);
        ContentValues type = new ContentValues();
        type.put(Item.Items.IS_LIST, true);
        Uri uri = Uri.parse(Item.Items.CONTENT_URI + "/" + _id);
        getContentResolver().update(uri, type, null, null);
        editText.setText("");
        createItemAnimation();
    }

    public void showTimeSpinner(View v) {
        if (editIsActive) {
            checkEdit();
        } else {
            Intent spinner = new Intent(this, SetTimePopUp.class);
            spinner.putExtra("_id", _id);
            startActivity(spinner);
        }
    }

    protected void createItemAnimation() {
        Intent launchActivity = new Intent(this, ListActivity.class);
        launchActivity.putExtra("_id", _id);
        startActivity(launchActivity);
        finish();
    }

    public void notListListener(View v) {
        if (!editIsActive) {
            slideInAnimation();
            editIsActive = focusEdit();
        } else {
            checkEdit();
        }
    }

    private void checkEdit() {
        editIsActive = unfocusEdit();
        if (!editText.getText().toString().equals("")) {
            createItem();
        } else {
            slideOutAnimation();
        }
    }

    private void findViews() {
        name = (TextView) findViewById(R.id.item_item_name);
        textView = (TextView) findViewById(R.id.item_tw_bottom);
        editText = (EditText) findViewById(R.id.item_edit_text);
        overlay = findViewById(R.id.item_overlay);
        scrollView = (ScrollView) findViewById(R.id.item_scroll);
        timeButton = (Button) findViewById(R.id.item_time_button);
    }

    private void setTimeText(int seconds) {
        timeButton.setText(Time.getTimeString(seconds));
    }

    public void runItem(View v) {
            String _id = getIntent().getStringExtra("_id");
            Intent intent = new Intent(this, RunPopUp.class);
            intent.putExtra("_id", _id);
            startActivity(intent);
    }
    
    public void showNotes(View v) {
        Intent intent = new Intent(this, NotePopUp.class);
        intent.putExtra("_id", _id);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        setText(getItemCursor());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.list_switch_in_back,R.anim.list_switch_out_back);
    }
}
