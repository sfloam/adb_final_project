
import java.util.concurrent.ConcurrentHashMap;


public class LockTable extends ConcurrentHashMap<String, String>{
	int id;

	LockTable (int id){
		this.id = id;
	}
	
	public String toString(){
		return "LT_" + id;
	}

}