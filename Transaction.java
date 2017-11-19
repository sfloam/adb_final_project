
public class Transaction {
	public int age;

	public Transaction(String transactionStr){
		this.age = 1;
	}

	public int compareTo(Transaction other_transaction){
		int res = this.age - other_transaction.age;
		return res;
	}
 


}