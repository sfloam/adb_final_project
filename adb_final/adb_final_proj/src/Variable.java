import java.util.ArrayList;
import java.util.HashSet;

public class Variable {
	private int id;
	private int value;
	private int intermediateValue;

	// whether it is locked by a transaction
	private boolean lock;

	// last transaction to interact with it
	private int previousTransactionID;

	// All transactions that have interacted with it
	private HashSet<Integer> correspondingTransactions;

	// site locations where transaction can be found
	private ArrayList<Integer> siteLocations;

	public Variable(int id) {
		this.id = id;
		this.value = id * 10;

		// does a transaction have a this variable
		this.lock = false;

		// which transactions use this variable
		correspondingTransactions = new HashSet<Integer>();

		// use to read next site for variable when first fails
		siteLocations = new ArrayList<Integer>();

		if ((id % 2) == 0) {
			for (int i = 1; i < 11; i++) {
				siteLocations.add(i);
			}
		} else {
			siteLocations.add((1 + (id % 10)));
		}
	}

	public String toString() {
		return "[ID: " + id + " Value: " + value + " isLocked: " + lock + " corTrans: " + correspondingTransactions
				+ " siteLocs: " + siteLocations + "]";
	}

	public int getID() {
		return this.id;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isLocked() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

	public int getPreviousTransactionID() {
		return this.previousTransactionID;
	}

	public void setPreviousTransactionID(Integer previousTransactionID) {
		this.previousTransactionID = previousTransactionID;
	}

	public HashSet<Integer> getCorrespondingTransactions() {
		return this.correspondingTransactions;
	}

	public void addToCorrespondingTransaction(int transactionID) {
		correspondingTransactions.add(transactionID);
	}

	public ArrayList<Integer> getSiteLocations() {
		return this.siteLocations;
	}

	public int getIntermediateValue() {
		return intermediateValue;
	}

	public void setIntermediateValue(int intermediateValue) {
		this.intermediateValue = intermediateValue;
	}

}