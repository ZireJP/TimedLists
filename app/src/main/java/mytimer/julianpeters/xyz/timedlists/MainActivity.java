package mytimer.julianpeters.xyz.timedlists;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.zip.Inflater;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    private TextView textView;
    protected TextView name;
    protected EditText editText;
    public boolean editIsActive = false;
    private View overlay;
    protected MaxListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = adapter();
        setListAdapter(adapter);
        name = (TextView) findViewById(R.id.item_name);
        editText = (EditText) findViewById(R.id.edit_text);
        editText.setVisibility(View.GONE);
        textView = (TextView) findViewById(R.id.empty);
        textView.setOnClickListener(notListListener());
        listView = (MaxListView) getListView();
        View footer = ((LayoutInflater)this.getSystemService(this.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null);
        listView.addFooterView(footer);
        overlay = findViewById(R.id.main_overlay);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] proj = {Item.Items.ITEM_ID, Item.Items.TITLE, Item.Items.IS_LIST};
        String selection = Item.Items.TAG + " = ?";
        String[] selectionArgs = {"fav"};
        return new CursorLoader(this,
                Item.Items.CONTENT_URI,
                proj,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    protected SimpleCursorAdapter adapter() {
        return new MainCursorAdapter(this, null);
    }

    private void slideInAnimation() {
        final Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slidein);
        int test = listView.getHeight();
        Animation anim2 = new TranslateAnimation(0, 0, 0, -test);
        anim2.setDuration(400);
        anim2.setFillAfter(true);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                editText.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                editText.startAnimation(anim);
                listView.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                editText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                editText.clearAnimation();
                overlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        listView.startAnimation(anim2);
    }

    private void slideOutAnimation() {
        int test = listView.getHeight();
        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slideout);
        final Animation anim2 = new TranslateAnimation(0, 0, -test, 0);
        anim2.setDuration(400);
        anim2.setFillAfter(true);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listView.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                overlay.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                editText.setVisibility(View.GONE);
                editText.clearAnimation();
                listView.startAnimation(anim2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        editText.startAnimation(anim);
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
        values.put(Item.Items.TAG, "fav");
        getContentResolver().insert(Item.Items.CONTENT_URI, values);
        editText.setText("");
        createItemAnimation();
    }

    protected void createItemAnimation() {
        slideOutAnimation();
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

    public void addItemMenu(View v) {
        if (!editIsActive) {
            slideInAnimation();
            editIsActive = focusEdit();
        } else {
            checkEdit();
        }

    }

    public void checkEdit() {
        editIsActive = unfocusEdit();
        if (!editText.getText().toString().equals("")) {
            createItem();
        } else {
            slideOutAnimation();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
