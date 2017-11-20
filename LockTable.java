
import java.util.concurrent.ConcurrentHashMap;


public class LockTable extends ConcurrentHashMap<Integer, Integer>{
	int id;

	LockTable (int id){
		this.id = id;
	}
	
	public String toString(){
		return "LT_" + id;
	}

}