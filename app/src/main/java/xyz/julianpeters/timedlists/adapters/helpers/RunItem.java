package xyz.julianpeters.timedlists.adapters.helpers;


import java.util.ArrayList;

/**
 * Created by julian on 17.05.17.
 */

public class RunItem {

    private String _id;
    private boolean visible = false;
    private String name;
    private ArrayList<RunItem> items;
    private int time;
    private int totalTime;
    private int size;
    private int totalSize;
    int repeat;
    private boolean isList;
    private boolean highlight = false;

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
            this.totalTime = this.time * repeat;
            return this.time;
        }
        for (RunItem x : items) {
            time += x.calculateTimes() * x.repeat;
        }
        this.time = time;
        this.totalTime = time * repeat;
        return time;
    }

    public int calculateSize() {
        if (!isList) {
            this.size = 1;
            this.totalSize = 1;
            return 1;
        }
        int i = 0;
        for (RunItem x : items) {
            i += x.calculateSize();
        }
        this.size = i;
        this.totalSize = i * repeat;
        return totalSize;
    }

    public int getSize() {
        return size;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public boolean getHighlight() {
        return highlight;
    }

    public void changeVisibility() {
        visible = !visible;
    }

    public void setVisibility(boolean vis) {
        visible = vis;
    }

    public boolean getVisibility() {
        return visible;
    }

    public int getRepeat() {
        return repeat;
    }
}
