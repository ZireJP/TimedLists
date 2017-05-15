package mytimer.julianpeters.xyz.timedlists;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import mytimer.julianpeters.xyz.timedlists.CustomAdapters.SubListCursorAdapter;
import mytimer.julianpeters.xyz.timedlists.CustomViews.MaxListView;
import mytimer.julianpeters.xyz.timedlists.providers.ProviderHelperClasses.Item;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    protected EditText name;
    protected EditText editText;
    public boolean editIsActive = false;
    protected View overlay;
    protected MaxListView listView;
    protected RecyclerView rV;
    protected SubListCursorAdapter rVAdap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.list_switch_in, R.anim.list_switch_out);
        setContentView(R.layout.activity_main);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        ItemTouchHelper iTouch = new ItemTouchHelper(simpleCallback);
        //iTouch.attachToRecyclerView();
        adapter = adapter();
        setListAdapter(adapter);
        name = (EditText) findViewById(R.id.edit_title);
        editText = (EditText) findViewById(R.id.edit_text);
        editText.setVisibility(View.GONE);
        name.setEnabled(false);
        listView = (MaxListView) getListView();
        View footer = ((LayoutInflater)this.getSystemService(this.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null);
        listView.addFooterView(footer);
        overlay = findViewById(R.id.main_overlay);
        rV = (RecyclerView) findViewById(R.id.recycler_view);
        rVAdap = new SubListCursorAdapter(this, null);
        rV.setAdapter(rVAdap);
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
                focusEdit();
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
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
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
                listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
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

    protected boolean unfocusEdit(View v) {
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
        slideOutAnimation();
    }

    public void showAddItem(View v) {
        if (!editIsActive) {
            slideInAnimation();
            editIsActive = focusEdit();
        } else {
            checkEdit();
        }
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
        editIsActive = unfocusEdit(editText);
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

    @Override
    public void onBackPressed() {
       super.onBackPressed();
        overridePendingTransition(R.anim.list_switch_in_back,R.anim.list_switch_out_back);
    }

    public void showNotes(View v) {

    }

    public void disableElse(View v) {

    }

    public void unFocusTitle(View v) {

    }

}
