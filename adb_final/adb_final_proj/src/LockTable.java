
import java.util.HashMap;


public class LockTable extends HashMap<Integer, Integer> {
    private int id;

    LockTable(int id) {
        this.id = id;
    }
    
    public int getID() {
    		return this.id;
    }

}