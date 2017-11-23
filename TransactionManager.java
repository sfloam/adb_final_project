import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;

public class TransactionManager {

	public LinkedList<Transaction> running;
	public ArrayList<Site> sites;
	public HashMap<Integer,Variable> vars;
	public int age;


	//SITES IS starting from 1 NOT zero
	public TransactionManager(){

		//May not need all of these
		this.running = new LinkedList<Transaction>();
		this.sites = new ArrayList<Site>();
		this.vars = new HashMap<Integer,Variable>();
		sites.add(null); //leaves site0 empty will make it easier later
		
		for (int x = 1; x < 21; x++){
			vars.put(x, new Variable(x));
			System.out.println(vars);
		}

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

		//not complete
		else if (transaction.get(0).equalsIgnoreCase("W")){
			int index;
			String transName = transaction.get(1);
			boolean alreadyFound = false;
			
			for (Transaction t : running){
				if ((t.transName).equals(transName)) {
					t.operations.add(transaction);
					//TODO: Execute INstruction Operation
					executeWriteInstruction(transaction);
					break;
				}	
			}
		}

		//not complete
		else if (transaction.get(0).equalsIgnoreCase("R")){
			int index;
			String transName = transaction.get(1);
			boolean alreadyFound = false;
			
			for (Transaction t : running){
				if ((t.transName).equals(transName)) {
					t.operations.add(transaction);
					//TODO: Execute INstruction Operation
					executeReadInstruction(transaction);
					break;
				}	
			}
		}

		//may need to address missing sites and null pointers later on
		else if (transaction.get(0).equalsIgnoreCase("fail")){
			int siteID = Integer.parseInt(transaction.get(1));
			sites.get(siteID).fail();
		}

		//not complete
		else if (transaction.get(0).equalsIgnoreCase("end")){
			
		}
		
		//not complete
		else if (transaction.get(0).equalsIgnoreCase("dump")){

		}

		//not complete
		else {

			System.out.println("Something wasn't covered: "+ transaction);
		}
	}

	public void executeWriteInstruction(ArrayList<String> operation){
		Integer varInt = Integer.parseInt(operation.get(2).replaceAll("x",""));
		lockVariable(varInt); // variable name
		updateLockTables(varInt);
	}

	public void lockVariable(Integer varInt){

		if (this.vars.get(varInt).isLocked){
			System.out.println("LOCKED");
		}
		else {
			this.vars.get(varInt).isLocked = true;
			System.out.println( "x"+varInt +" is not locked yet");
		}
	}

	public void updateLockTables(Integer varInt){


	}
	public void executeReadInstruction(ArrayList<String> operation){

	}


}
