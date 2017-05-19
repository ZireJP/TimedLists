package xyz.julianpeters.timedlists.activities.main;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import xyz.julianpeters.timedlists.activities.popup.CopyPopUp;
import xyz.julianpeters.timedlists.activities.popup.NotePopUp;
import xyz.julianpeters.timedlists.R;
import xyz.julianpeters.timedlists.activities.popup.RunMultiplePopUp;
import xyz.julianpeters.timedlists.helpers.ValuesForItems;
import xyz.julianpeters.timedlists.providers.helpers.Item;

/**
 * Created by julian on 14.05.17.
 */

abstract class BaseActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    final long ITEM_ANIMATION_TIME = 200;
    EditText titleView;
    View showNotes;
    View itemView;
    EditText newEditText;
    Button runButton;
    View newItemOverlay;
    View fullOverlay;
    boolean editIsActive;
    boolean titleIsActive;

    String _id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.list_switch_in, R.anim.list_switch_out);
        setContentView(findLayout());
        findBasicViews();
        titleView.setOnTouchListener(onTouch());
        //newEditText.setVisibility(View.GONE);
        editIsActive = false;
        titleIsActive = false;
        _id = setId();
        setTitle();
        getLoaderManager().initLoader(0, null, this);
    }

    abstract int findLayout();
    abstract View setItemView();
    abstract void setTitle();
    abstract String setId();

    public void findBasicViews() {
        titleView = (EditText) findViewById(R.id.edit_title);
        showNotes = findViewById(R.id.show_notes);
        newEditText = (EditText) findViewById(R.id.new_item_edit);
        itemView = setItemView();
        runButton = (Button) findViewById(R.id.run_button);
        newItemOverlay = findViewById(R.id.main_overlay);
        fullOverlay = findViewById(R.id.full_overlay);
    }

    public void slideInAnimation() {
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.slidein);
        int test = itemView.getHeight();
        Animation anim2 = new TranslateAnimation(0, 0, 0, -test+adjustHeight());
        if (test-adjustHeight()!=0) {
            anim2.setDuration(ITEM_ANIMATION_TIME);
        } else {
            anim2.setDuration(0);
        }
        anim2.setFillAfter(true);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                newEditText.setVisibility(View.VISIBLE);
                itemView.setVisibility(View.GONE);
                newEditText.startAnimation(anim);
                itemView.clearAnimation();
                focusEdit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                newEditText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                newEditText.clearAnimation();
                newItemOverlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        itemView.startAnimation(anim2);
    }

    public int adjustHeight() {
        return 0;
    }
    public void slideOutAnimation(boolean newItem) {
        int test = itemView.getHeight();
        int size = (int)getResources().getDimension(R.dimen.list_item_size);
        final Animation anim2;
        if (!newItem) {
            anim2 = createAnim(-test+size, 0);
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.slideout);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    newItemOverlay.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    newEditText.setVisibility(View.GONE);
                    newEditText.clearAnimation();
                    itemView.startAnimation(anim2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            newEditText.startAnimation(anim);
        } else {
            anim2 = createAnim(-test, -size);
            newItemOverlay.setVisibility(View.INVISIBLE);
            itemView.startAnimation(anim2);
        }
    }

    public Animation createAnim(long fromY, long toY) {
        Animation animation = new TranslateAnimation(0, 0, fromY, toY);
        animation.setDuration(ITEM_ANIMATION_TIME);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                itemView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                newEditText.setVisibility(View.GONE);
                itemView.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return animation;
    }

    public boolean focusEdit() {
        newEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(newEditText, InputMethodManager.SHOW_IMPLICIT);
        return true;
    }

    public boolean unFocusEdit(View v) {
        v.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        return false;
    }

    public Uri createItem() {
        String[] projection = {Item.Items.ITEM_ID, Item.Items.TITLE, Item.Items.TIME};
        String selection = Item.Items.TITLE + " LIKE ?" ;
        String[] arg = {"%" + newEditText.getText().toString() + "%"};
        Cursor c = getContentResolver().query(Item.Items.CONTENT_URI, projection, selection, arg, null);
        int count = c.getCount();
        c.moveToFirst();
        Uri uri = null;
        if (count > 1) {
            editIsActive = true;
            startCopyPop();
        } else if (count == 1 && !c.getString(c.getColumnIndex(Item.Items.ITEM_ID)).equals(_id)) {
            editIsActive = true;
            startCopyPop();
        } else {
            uri = getContentResolver().insert(Item.Items.CONTENT_URI, getContentValues());
            createItemAnimation(true);
        }
        c.close();
        return uri;
    }

    void startCopyPop() {
        Intent intent = new Intent(this, CopyPopUp.class);
        intent.putExtra("name", newEditText.getText().toString());
        startActivity(intent);
    }

    void createItemAnimation(boolean created) {
       newEditText.setText("");
       slideOutAnimation(created);
       editIsActive = false;
    }

    public void showAddItem(View v) {
        slideInAnimation();
        editIsActive = focusEdit();
    }

    public void checkEdit(View v) {
        editIsActive = unFocusEdit(newEditText);
        if (!newEditText.getText().toString().equals("")) {
            createItem();
        } else {
            slideOutAnimation(false);
        }
    }

    public ContentValues getContentValues() {
        return ValuesForItems.newItem(newEditText.getText().toString());
    }

    public void showNotes(View v) {
        if (!titleIsActive) {
            Intent intent = new Intent(this, NotePopUp.class);
            intent.putExtra("_id", _id);
            startActivity(intent);
        }

    }

    public void unFocusTitle(View v) {
        unFocusEdit(titleView);
        ContentValues values = new ContentValues();
        values.put(Item.Items.TITLE, titleView.getText().toString());
        getContentResolver().update(Item.Items.getIdUri(_id), values, null, null);
        fullOverlay.setVisibility(View.INVISIBLE);
        showNotes.setEnabled(true);
    }

    public void disableElse() {
        fullOverlay.setVisibility(View.VISIBLE);
        showNotes.setEnabled(false);
    }

    public View.OnTouchListener onTouch() {
        return new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                disableElse();
                return false;
            }
        };
    }

    public void runTimer(View v) {
        Intent intent = new Intent(this, RunActivity.class);
        intent.putExtra("_id", _id);
        startActivity(intent);
    }

    public void showMultiplePopUp(View v) {
        Intent intent = new Intent(this, RunMultiplePopUp.class);
        intent.putExtra("_id", _id);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.list_switch_in_back, R.anim.list_switch_out_back);
    }

}
