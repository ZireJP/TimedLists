package mytimer.julianpeters.xyz.timedlists;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    private TextView textView;
    protected EditText editText;
    private MaxListView listView;
    private boolean editIsActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = adapter();
        setListAdapter(adapter);
        listView = (MaxListView) findViewById(android.R.id.list);
        editText = (EditText) findViewById(R.id.edit_text);
        editText.setVisibility(View.GONE);
        textView = (TextView) findViewById(R.id.empty);
        textView.setOnClickListener(notListListener());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] proj = {Item.Items.ITEM_ID, Item.Items.TITLE, Item.Items.IS_LIST};
        String selection = Item.Items.TAG + " = ?";
        String[] selectionArgs = {"fav"};
        CursorLoader loader = new CursorLoader(this,
                Item.Items.CONTENT_URI,
                proj,
                selection,
                selectionArgs,
                Item.Items.ITEM_ID + " DESC");
        return loader;
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
        editText.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slidein);
        Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pushdown);
        editText.startAnimation(anim);
        textView.startAnimation(anim2);
    }

    private void slideOutAnimation() {
        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slideout);
        Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pushup);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listView.setVisibility(View.VISIBLE);
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
        values.put(Item.Items.TAG, "fav");
        getContentResolver().insert(Item.Items.CONTENT_URI, values);
        editText.setText("");
        createItemAnimation();
    }

    protected void createItemAnimation() {
        listView.setVisibility(View.VISIBLE);
        editText.setVisibility(View.GONE);
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

    @Override
    protected void onDestroy() {
        // TODO: Clean database here
        // --> delete/drop all items not tagged "fav" and not referenced in other item
        super.onDestroy();
    }
}
