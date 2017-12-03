
import java.util.Queue;
import java.util.Set;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author scottfloam and pratikkarnik
 * 
 *         <h1>Transaction</h1> The Transaction keeps track of the transaction
 *         details. It holds information such as whether it is blocked, how long
 *         it has been running, the id of the transaction, and the variables 
 *         corresponding to that Transaction. In addition, it also keeps a log of 
 *         its operations in the operations queue. We need to think about if
 *         we want this to be a stack instead. It may make more sense to
 *         pop out most recent transactions to revert actions. TBD.
 *
 */
public class Transaction {
	private int age;
	private String transName;
	private String txnID;
	private HashSet<Integer> correspondingVars;
	private boolean blocked;
	private String transactionType;
	private ArrayList<LockObj> locksHeldByTransaction;
	private String transactionWaitingForCurrentTransaction;
	private Set<String> transactionsWhichCurrentTransactionWaitsFor;

	// used for rollbacks
	public Queue<Operation> operations;

	public Transaction(String id, String txnType) {
		this.age = 0;
		this.txnID = id;
		this.operations = new LinkedList<Operation>();
		this.correspondingVars = new HashSet<Integer>();
		this.blocked = false;
		this.transactionType = txnType;
		this.locksHeldByTransaction = new ArrayList<LockObj>();
		this.transactionsWhichCurrentTransactionWaitsFor = new HashSet<String>();
	}

	/**
	 * compareTo would be necessary if you needed to sort transactions in some
	 * manner. May not be necessary. Could be possibly used for age sorting?
	 * 
	 * @param other_transaction
	 * @return
	 */
	/*public int compareTo(Transaction other_transaction) {
		int res = this.id - other_transaction.id;
		System.out.println(this + " compared to " + other_transaction);
		return res;
	}*/

	/**
	 * gets id of the transaction
	 * 
	 * @return
	 */
	public String getID() {
		return this.txnID;
	}

	/**
	 * gets age of the transaction
	 * age is simply the initial order in which it was inserted into the running queue
	 * 
	 * @return
	 */
	public Integer getAge() {
		return this.age;
	}

	/**
	 * setAge should only be done once when Transaction is initialized
	 * @param age
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * getTransName() Gets the name of a transaction such as T1
	 * 
	 * @return
	 */
	public String getTransName() {
		return this.transName;
	}

	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * getCorrespondingVars keeps a HashSet of all Variable IDs used by this Transaction used for 
	 * determining which Site(s) need to be updated at commit time. We then use these Variable IDs
	 * to find the Site(s) that have variables that we need to update. We could also use this as a 
	 * means to determine next available free site quickly.
	 * @return
	 */
	public HashSet<Integer> getCorrespondingVars() {
		return this.correspondingVars;
	}

	/**
	 * addToCorrespondingVars adds a corresponding Variable id to the list of correspondingVariables associated with a given transaction
	 * @param varID
	 */
	public void addToCorrespondingVars(int varID) {
		this.correspondingVars.add(varID);
	}

	/**
	 * setBlocked simply toggles the blocked boolean on and off (blocked is initialized to false)
	 * 
	 */
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	/**
	 * isBlocked returns if Transaction is blocked by another Transaction
	 * @return boolean
	 *  
	 */
	public boolean isBlocked() {
		return this.blocked;
	}

	/**
	 * toString() prints out the Transaction information
	 */
	public String toString() {
		
		String output = "transName: "+ transName +"\n"
							+" Age: " + age +"\n"
							+" id: " + txnID +"\n"
							+" correspondingVars: " + correspondingVars +"\n"
							+" blocked: " + blocked +"\n"
							+" operations: "+ operations +"\n";
							
				
		
		return output;
	}

	public boolean isWriteLockPresentOnVariable(int varID) {
		for(LockObj eachLock : locksHeldByTransaction) {
			if(eachLock.getLockType().equals(GlobalConstants.writeLock)
					&& eachLock.getVariableID() == varID) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<LockObj> getLocksHeldByTransaction() {
		return locksHeldByTransaction;
	}

	public void setLocksHeldByTransaction(ArrayList<LockObj> locksHeldByTransaction) {
		this.locksHeldByTransaction = locksHeldByTransaction;
	}

	public void addLockToLocksHeldByTransaction(int varID, String lockType) {
		LockObj newLock = new LockObj(lockType, this.txnID, varID);
		locksHeldByTransaction.add(newLock);
	}

	public void removeLockFromLocksHeldByTransaction(int varID) {
		for(LockObj eachLock : locksHeldByTransaction) {
			if(eachLock.getVariableID() == varID) {
				locksHeldByTransaction.remove(eachLock);
			}
		}
	}

	public void addOperation(Operation opObj) {

	}

	public String getTransactionWaitingForCurrentTransaction() {
		return transactionWaitingForCurrentTransaction;
	}

	public void setTransactionWaitingForCurrentTransaction(String transactionWaitingForCurrentTransaction) {
		this.transactionWaitingForCurrentTransaction = transactionWaitingForCurrentTransaction;
	}

	public Set<String> getTransactionsWhichCurrentTransactionWaitsFor() {
		return transactionsWhichCurrentTransactionWaitsFor;
	}

	public void setTransactionsWhichCurrentTransactionWaitsFor(Set<String> transactionsWhichCurrentTransactionWaitsFor) {
		this.transactionsWhichCurrentTransactionWaitsFor = transactionsWhichCurrentTransactionWaitsFor;
	}
	
	public ArrayList<LockObj> getAllLocksForVariable(int varID, String lockType) {
		ArrayList<LockObj> allLocksForVariable = new ArrayList<LockObj>();
		for(LockObj eachLock : locksHeldByTransaction) {
			if(eachLock.getLockType().equals(lockType) && eachLock.getVariableID() == varID) {
				allLocksForVariable.add(eachLock);
			}
		}
		return allLocksForVariable;
	}

}