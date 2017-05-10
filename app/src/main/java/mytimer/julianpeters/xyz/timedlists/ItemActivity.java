package mytimer.julianpeters.xyz.timedlists;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ItemActivity extends Activity {

    String id;
    Button time;
    Button right;
    TextView name;
    TextView textView;
    Boolean editIsActive;
    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        id = getIntent().getStringExtra("id");
        time = (Button) findViewById(R.id.item_button_left);
        right = (Button) findViewById(R.id.item_button_right);
        name = (TextView) findViewById(R.id.item_name);
        textView = (TextView) findViewById(R.id.item_tw_bottom);
        editText = (EditText) findViewById(R.id.item_edit_text);
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
        time.setText(c.getString(c.getColumnIndex(Item.Items.TIME)));
        name.setText(c.getString(c.getColumnIndex(Item.Items.TITLE)));
    }

    private void slideInAnimation() {
        editText.setVisibility(View.VISIBLE);
        name.setVisibility(View.GONE);
        time.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
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
                name.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
                right.setVisibility(View.VISIBLE);
                editText.setVisibility(View.GONE);
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
        values.put("table", "table_" + getIntent().getStringExtra("id"));
        getContentResolver().insert(ItemInItem.ItemInItems.CONTENT_URI, values);
        ContentValues type = new ContentValues();
        type.put(Item.Items.IS_LIST, true);
        String selection = Item.Items._ID + " = ?";
        getContentResolver().update(Item.Items.CONTENT_URI, type, selection, new String[]{id});
        editText.setText("");
        createItemAnimation();
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
}
