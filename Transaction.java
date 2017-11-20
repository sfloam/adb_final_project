
public class Transaction {
	public int age;
	public String transName;
	public boolean readLock;
	public boolean writeLock;
	

	public Transaction(String transactionStr, String transName){
		this.age = 1;
		this.transName = transName;
		readLock = false; 
		writeLock = false; 
	}

	public int compareTo(Transaction other_transaction){
		int res = this.age - other_transaction.age;
		return res;
	}

	public String read(String varName, LockTable table){
		if (!readLock){
			return table.get(varName);
		}
		else {
			return "locked";
		}
		
	}

	public void write(String varName, int value, LockTable table){
		if (!writeLock){
			table.set(varName, value);
		}
	}
}