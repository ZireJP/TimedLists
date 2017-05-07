package mytimer.julianpeters.xyz.mytimer;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends ListActivity {

    private ItemDb myDb;
    private ArrayList<Workout> l;
    private ItemArrayAdapter ad;
    private TextView tw;
    private EditText et;
    private MaxListView lw;
    private boolean et_act = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new ItemDb(this);
        myDb.open();
        l = new ArrayList<>();
        ad = new ItemArrayAdapter(this, R.layout.list_item, l);
        populateList();
        lw = (MaxListView) findViewById(android.R.id.list);
        et = (EditText) findViewById(R.id.edit_text);
        et.setVisibility(View.GONE);
        tw = (TextView) findViewById(R.id.empty);
        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_act) {
                    et.setVisibility(View.VISIBLE);
                    lw.setVisibility(View.GONE);
                    Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slidein);
                    Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pushdown);
                    et.startAnimation(anim);
                    tw.startAnimation(anim2);
                    et.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                    et_act = true;
                } else {
                    et_act = false;
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (!et.getText().toString().equals("")) {
                        myDb.newItem(et.getText().toString());
                        populateList();
                        ad.notifyDataSetChanged();
                        lw.setVisibility(View.VISIBLE);
                        et.setVisibility(View.GONE);
                    } else {
                        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slideout);
                        Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pushup);
                        anim2.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                lw.setVisibility(View.VISIBLE);
                                et.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        et.startAnimation(anim);
                        tw.startAnimation(anim2);
                    }
                    et.setText("");
                }
            }
        });
        setListAdapter(ad);
    }

    private void populateList() {
        Cursor cursor = myDb.getAllItems();
        ArrayList<Workout> l = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Workout workout = new Workout(cursor.getString(1), Integer.parseInt(cursor.getString(0)));
                l.add(workout);
            } while (cursor.moveToNext());
        }
        cursor.close();
        ad.clear();
        ad.addAll(l);

    }

    public void deleteItem(int id) {
        myDb.deleteItem(Integer.toString(id));
        populateList();
    }

    @Override
    protected void onDestroy() {
        myDb.close();
        super.onDestroy();
    }
}
