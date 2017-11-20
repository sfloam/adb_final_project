import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;

public class TransactionManager {

	public LinkedList<Transaction> running;
	public LinkedList<Transaction> ready;
	public LinkedList<Transaction> blocked;
	public int age;

	public TransactionManager(){

		//May not need all of these
		this.running = new LinkedList<Transaction>();
		this.ready = new LinkedList<Transaction>();
		this.blocked = new LinkedList<Transaction>();
		this.age = 0;
	}

	public void assignTransaction(ArrayList<String> transaction){
		
		if (transaction.get(0).equalsIgnoreCase("begin")){
			boolean isNewTransaction = true;

			//Traverses lists to see if transaction exists
			for (Transaction t: running){
				if (t.transName.equals(transaction.get(1))){
					System.out.println("Transaction Exists! Multiple begins for same transaction!");
					isNewTransaction = false;
					break;
				}
			}
			//Traverses lists to see if transaction exists
			for (Transaction t: ready){
				if (t.transName.equals(transaction.get(1))){
					System.out.println("Transaction Exists! Multiple begins for same transaction!");
					isNewTransaction = false;
					break;
				}
			}
			//Traverses lists to see if transaction exists
			for (Transaction t: blocked){
				if (t.transName.equals(transaction.get(1))){
					System.out.println("Transaction Exists! Multiple begins for same transaction!");
					isNewTransaction = false;
					break;
				}
			}

			//Checks if this is a new transaction and not an existing one
			if (isNewTransaction){
				String transName = transaction.get(1).replaceAll("T","");
				Transaction tObj = new Transaction (Integer.parseInt(transName));
				tObj.age = this.age;
				this.age++;
			}
		}

		else if (transaction.get(0).equalsIgnoreCase("W") || transaction.get(0).equalsIgnoreCase("R")){
			int index;
			String transName = transaction.get(1);
			boolean alreadyFound = false;

			for (Transaction t : running){
				if ((t.transName).equals(transName)) {
					t.operations.add(transaction);
					alreadyFound = true;
					break;
				}	
			}

			if (!alreadyFound){
				for (Transaction t : ready){
					if  ((t.transName).equals(transName)){
						t.operations.add(transaction);
						alreadyFound = true;
						break;
					}	
				}	
			}
			
			if (!alreadyFound){
				for (Transaction t : blocked){
					if ((t.transName).equals(transName)){
						t.operations.add(transaction);
						break;
					}

				}
			}	
		}

	}




}