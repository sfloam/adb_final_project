import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;

public class TransactionManager {

	public LinkedList<Transaction> running;
	public ArrayList<Site> sites;
	public int age;


	//SITES IS starting from 1 NOT zero
	public TransactionManager(){

		//May not need all of these
		this.running = new LinkedList<Transaction>();
		this.sites = new ArrayList<Site>();
		sites.add(null); //leaves site0 empty will make it easier later
		for (int x = 1; x < 11; x++){
			sites.add(new Site(x));
			System.out.println(sites.get(x).lt);
		}

		System.out.println(sites);
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

			//Checks if this is a new transaction and not an existing one
			if (isNewTransaction){
				String transName = transaction.get(1).replaceAll("T","");
				Transaction tObj = new Transaction (Integer.parseInt(transName));
				tObj.age = this.age;
				this.age++;
				running.add(tObj);
			}
		}

		else if (transaction.get(0).equalsIgnoreCase("W") || transaction.get(0).equalsIgnoreCase("R")){
			int index;
			String transName = transaction.get(1);
			boolean alreadyFound = false;
			
			for (Transaction t : running){
				if ((t.transName).equals(transName)) {
					t.operations.add(transaction);
					//TODO: Execute INstruction Operation
					executeInstruction(transaction);
					alreadyFound = true;
					break;
				}	
			}

		}
		else if (transaction.get(0).equalsIgnoreCase("end")){
			
		}
	}

	public void executeInstruction(ArrayList<String> operation){


	}
}
