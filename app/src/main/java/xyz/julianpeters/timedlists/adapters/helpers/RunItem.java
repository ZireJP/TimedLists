package xyz.julianpeters.timedlists.adapters.helpers;


import java.util.ArrayList;

/**
 * Created by julian on 17.05.17.
 */

public class RunItem {

    private String _id;
    private String name;
    private ArrayList<RunItem> items;
    private int time;
    private int size;
    private int totalSize;
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

    public int calculateTimes() {
        int time = 0;
        if (!isList) {
            return this.time;
        }
        for (RunItem x : items) {
            time += x.calculateTimes() * x.repeat;
        }
        this.time = time;
        return time;
    }

    public int calculateSize() {
        if (!isList) {
            this.size = 1;
            return 1;
        }
        int i = 0;
        for (RunItem x : items) {
            i += x.calculateTotalSize();
        }
        this.size = i;
        return i;
    }

    public int calculateTotalSize() {
        if (!isList) {
            totalSize = repeat;
            return totalSize;
        }
        int i = 0;
        for (RunItem x : items) {
            i += x.calculateTotalSize();
        }
        this.totalSize = i * repeat;
        return totalSize;
    }

    public int getSize() {
        return size;
    }

    public int getTotalSize() {
        return totalSize;
    }
}
