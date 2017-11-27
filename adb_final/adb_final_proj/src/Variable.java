import java.util.HashSet;
import java.util.ArrayList;

public class Variable {
    private int id;
    private int value;
    private boolean lock;
    private int previousTransactionID;
    private HashSet<Integer> correspondingTransactions;
    private ArrayList<Integer> siteLocations;

    public Variable(int id) {
        this.id = id;
        this.value = id * 10;
        
        //does a transaction have a this variable
        this.lock = false;
        
        //which transactions use this variable
        correspondingTransactions = new HashSet<Integer>();
        
        //use to read next site for variable when first fails
        siteLocations = new ArrayList<Integer>();

        if (id % 2 == 0) {
            for (int i = 1; i < 11; i++) {
                siteLocations.add(i);
            }
        } else {
            siteLocations.add((1 + (id % 10)));
        }
    }

    public void addTransaction(int transactionID) {
        correspondingTransactions.add(transactionID);
    }

    public String toString() {
        return "[ID: " + id
                + " Value: " + value
                + " isLocked: " + lock
                + " corTrans: " + correspondingTransactions
                + " siteLocs: " + siteLocations + "]";
    }
    
    public int getID() {
    		return this.id;
    }
    public int getValue() {
		return this.value;
    }
    public boolean isLocked() {
    		return lock;
    }
    public void setLock() {
    		this.lock = !this.lock;
    }
    public int getPreviousTransactionID() {
    		return this.previousTransactionID;
    }
    public void setPreviousTransactionID(int previousTransactionID) {
    		this.previousTransactionID = previousTransactionID;
    }
    public HashSet<Integer> getCorrespondingTransactions() {
		return this.correspondingTransactions;
    }
    
    public ArrayList<Integer> getSiteLocations(){
    		return this.siteLocations;
    }

}