
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
public class Transaction {
	public int age;
	public String transName;
	public int id;
	public boolean readLock;
	public boolean writeLock;
	public Queue<ArrayList<String>> operations;


	public Transaction(int id){
		this.age = 0;
		this.transName = "T"+id;
		this.id = id;
		readLock = false; 
		writeLock = false; 
		this.operations = new LinkedList<ArrayList<String>> ();
	}

	public int compareTo(Transaction other_transaction){
		int res = this.id - other_transaction.id;
		return res;
	}


	public String read(Integer varName, LockTable table){
		if (!readLock){
			return ""+table.get(varName);
		}
		return "locked";
	}

	public void write(Integer varName, Integer value, LockTable table){
		if (!writeLock){
			table.put(varName, value);
		}
	}

	public String toString(){

		return "T"+this.id;
	}
}