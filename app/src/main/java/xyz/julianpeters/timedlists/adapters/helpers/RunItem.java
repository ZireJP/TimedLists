package xyz.julianpeters.timedlists.adapters.helpers;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by julian on 17.05.17.
 */

public class RunItem {

    private String _id;
    private String name;
    private ArrayList<RunItem> items;
    private int time;
    int repeat;
    private boolean isList;

    public RunItem(String _id, String name, int time, int repeat) {
        this._id = _id;
        this.name = name;
        this.time = time;
        this.repeat = repeat;
        isList = false;
    }

    public RunItem(String _id, String name, int repeat) {
        this._id = _id;
        this.name = name;
        this.repeat = repeat;
        isList = true;
        items = new ArrayList<>();
    }

    public boolean isList() {
        return isList;
    }

    public int getTime() {
        return time;
    }

    public ArrayList<RunItem> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public String get_id() {
        return _id;
    }

    public int getChildrenTimes() {
        int time = 0;
        if (!isList) {
            return this.time * repeat;
        } else {
            for (RunItem x : items) {
                time += x.getChildrenTimes();
            }
        }
        return time;
    }
}
