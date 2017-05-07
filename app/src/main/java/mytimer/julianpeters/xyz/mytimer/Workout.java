package mytimer.julianpeters.xyz.mytimer;

import java.io.Serializable;

/**
 * Created by julian on 05.05.17.
 */

public class Workout implements Serializable {

    public final int ID;
    public String name;

    public Workout(String name, int id) {
        this.name = name;
        this.ID = id;
    }

    public String toString(){
        return name;
    }

    public int getId() {
        return this.ID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
