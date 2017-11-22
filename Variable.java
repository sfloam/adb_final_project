import java.util.HashSet;
import java.util.ArrayList;

public class Variable {
	public int id;
	public int value;
	public boolean isLocked;
	public HashSet<Integer> correspondingTransactions;
	public ArrayList<Integer> siteLocations;

	public Variable (int id, int value, int transactionID){
		this.id = id;
		this.value = value;
		
		correspondingTransactions = new HashSet<Integer>();
		correspondingTransactions.add(transactionID);

		siteLocations = new ArrayList<Integer>();
		if (id % 2 == 0){
			for (int i = 1; i < 11; i++){
				siteLocations.add(i); 
			}
		}
		else{
			siteLocations.add((1+(id%10)));
		}
	}

}