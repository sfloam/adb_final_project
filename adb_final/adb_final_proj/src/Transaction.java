
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * 
 * @author scottfloam and pratikkarnik
 * <h1>Transaction</h1>
 * <span> The {@link Transaction} class keeps track of the {@link Transaction} details. </span>
 * <span> It holds information such as whether it is blocked, how long it has been </span>
 * <span> running, the id of the {@link Transaction}, and the {@link Variable}(s) corresponding to that</span>
 * <span> {@link Transaction}. In addition, it also keeps a log of its {@link Operation}(s).</span>
 *
 */
public class Transaction {
  private int age;
  private String txnID;
  private HashSet<Integer> correspondingVars;
  private boolean blocked;
  private String transactionType;
  private ArrayList<LockObj> locksHeldByTransaction;
  private String transactionWaitingForCurrentTransaction;
  private Set<String> transactionsWhichCurrentTransactionWaitsFor;
  private ArrayList<Integer> sitesAccessedByTransaction;
  private boolean readOnly;
  // used for rollbacks
  public Queue<Operation> operations;

  public Transaction(String id, String txnType) {
    this.txnID = id;
    this.operations = new LinkedList<Operation>();
    this.correspondingVars = new HashSet<Integer>();
    this.blocked = false;
    this.transactionType = txnType;
    this.locksHeldByTransaction = new ArrayList<LockObj>();
    this.transactionsWhichCurrentTransactionWaitsFor = new HashSet<String>();
    this.readOnly = txnType.equals("RO");
    this.sitesAccessedByTransaction = new ArrayList<Integer>();
  }

  /**
   * <strong>getID</strong>: gets id of the {@link Transaction}
   * @return the id of the {@link Transaction}
   */
  public String getID() {
    return this.txnID;
  }

  /**
   * <strong>getAge</strong>: gets the age of a {@link Transaction}
   * <span> Note, the age is incremented by one when a new {@link Transaction} begins.
   * <span> Thus, older {@link Transaction}(s) have smaller ages.
   * @return the age of a {@link Transaction}
   */
  public Integer getAge() {
    return this.age;
  }

  /**
   * <strong>setAge</strong>: sets the age of a {@link Transaction}
   * @param age - the age of a {@link Transaction}
   */
  public void setAge(int age) {
    this.age = age;
  }

  /**
   * <strong>getTransactionType</strong>: gets the type of {@link Transaction} 
   * @return the type of {@link Transaction} (i.e. RO, RW)
   */
  public String getTransactionType() {
    return transactionType;
  }

  /**
   * <strong>getCorrespondingVars</strong>: gets a HashSet of all {@link Variable} IDs used by this {@link Transaction}
   * @return a HashSet of all {@link Variable} IDs used by this {@link Transaction}
   */
  public HashSet<Integer> getCorrespondingVars() {
    return this.correspondingVars;
  }

  /**
   * <strong>addToCorrespondingVars</strong>: adds a corresponding {@link Variable} id to the list of correspondingVariables
   * associated with this {@link Transaction}
   * @param varID - ID of a {@link Variable}
   */
  public void addToCorrespondingVars(int varID) {
    this.correspondingVars.add(varID);
  }

  /**
   * <strong>setBlocked</strong>: sets a {@link Transaction} to blocked or not blocked
   */
  public void setBlocked(boolean blocked) {
    this.blocked = blocked;
  }

  /**
   * <strong>getBlocked</strong>: determines if a {@link Transaction} to blocked or not blocked
   * @return true if a {@link Transaction} is blocked, otherwise false
   */
  public boolean isBlocked() {
    return this.blocked;
  }

  /**
   * <strong>toString</strong>: outputs information about the {@link Transaction}
   * return information about the {@link Transaction}
   */
  public String toString() {

    String output = "\n TransactionID: " + this.txnID + "\n" + " Age: " + age + "\n"
                + " CorrespondingVars: " + correspondingVars + "\n" + " Blocked: " + blocked + "\n"
                + " Operations: " + operations + "\n";
    return output;
  }

  /**
   * <strong>isWriteLockPresentOnVariable</strong> determines if there is a write-lock present on a particular {@link Variable}</strong>
   * @param varID - the ID of a {@link Variable}
   * @return
   */
  public boolean isWriteLockPresentOnVariable(int varID) {
    for (LockObj eachLock : locksHeldByTransaction) {
      if (eachLock.getLockType().equals(GlobalConstants.writeLock)
          && eachLock.getVariableID() == varID) {
        return true;
      }
    }
    return false;
  }

  /**
   * <strong>getLocksHeldByTransaction</strong>: gets a list of {@link LockObj}(s) held by this {@link Transaction}
   * @return a list of {@link LockObj}(s) held by this {@link Transaction}
   */
  public ArrayList<LockObj> getLocksHeldByTransaction() {
    return locksHeldByTransaction;
  }

  /**
   * <strong>setLocksHeldByTransaction</strong>: sets the list of {@link LockObj}(s) held by this {@link Transaction}
   * @param locksHeldByTransaction a list of {@link LockObj}(s) held by this {@link Transaction}
   */
  public void setLocksHeldByTransaction(ArrayList<LockObj> locksHeldByTransaction) {
    this.locksHeldByTransaction = locksHeldByTransaction;
  }

  /**
   * <strong>addLockToLocksHeldByTransaction</strong>: adds a single {@link LockObj} to a list of {@link LockObj}(s) held by this {@link Transaction}
   * @param varID - the ID of a {@link Variable}
   * @param lockType - the type of {@link LockObj} that one wants to add to the list of {@link LockObj}(s) held by this {@link Transaction}
   */
  public void addLockToLocksHeldByTransaction(int varID, String lockType) {
    LockObj newLock = new LockObj(lockType, this.txnID, varID);
    locksHeldByTransaction.add(newLock);
  }

  /**
   * <strong>removeLockFromLocksHeldByTransaction</strong>: removes all {@link LockObj}(s) held by a {@link Variable}
   * @param varID - the ID of a {@link Variable}
   */
  public void removeLockFromLocksHeldByTransaction(int varID) {
    for (LockObj eachLock : locksHeldByTransaction) {
      if (eachLock.getVariableID() == varID) {
        locksHeldByTransaction.remove(eachLock);
      }
    }
  }

  /**
   * <strong> addOperation</strong>: placeholder method for added functionality
   * @param opObj - an {@link Operation} object
   */
  public void addOperation(Operation opObj) {

  }

  /**
   * <strong>getTransactionWaitingForCurrentTransaction</strong>: gets the {@link Transaction} waiting for the current {@link Transaction} in question
   * @returns the {@link Transaction} waiting for the current {@link Transaction} in question
   */
  public String getTransactionWaitingForCurrentTransaction() {
    return transactionWaitingForCurrentTransaction;
  }

  /**
   * <strong>setTransactionWaitingForCurrentTransaction</strong>: sets the {@link Transaction} waiting for the current {@link Transaction} in question
   * @param transactionWaitingForCurrentTransaction - the {@link Transaction} waiting for the current {@link Transaction} in question
   */
  public void setTransactionWaitingForCurrentTransaction(
      String transactionWaitingForCurrentTransaction) {
    this.transactionWaitingForCurrentTransaction = transactionWaitingForCurrentTransaction;
  }

  /**
   * <strong>getTransactionsWhichCurrentTransactionWaitsFor</strong>: gets the IDs of the {@link Transaction}(s) which the current {@link Transaction} is waiting for
   * @return
   */
  public Set<String> getTransactionsWhichCurrentTransactionWaitsFor() {
    return transactionsWhichCurrentTransactionWaitsFor;
  }

  /**
   * <strong>setTransactionsWhichCurrentTransactionWaitsFor</strong>: adds IDs to the set of {@link Transaction} IDs which the current {@link Transaction} is waiting
   * @param transactionsWhichCurrentTransactionWaitsFor - IDs which the current {@link Transaction} is waiting
   */
  public void setTransactionsWhichCurrentTransactionWaitsFor(
      Set<String> transactionsWhichCurrentTransactionWaitsFor) {
    this.transactionsWhichCurrentTransactionWaitsFor = transactionsWhichCurrentTransactionWaitsFor;
  }

  /**
   * <strong>getAllLocksForVariable</strong>: gets all {@LockObj}(s) for a particular {@link Variable} held by a {@link Transaction}
   * @param varID - IDs of {@link Variable}(s) with {@link LockObj} held by a {@link Transaction}
   */
  public ArrayList<LockObj> getAllLocksForVariable(int varID, String lockType) {
    ArrayList<LockObj> allLocksForVariable = new ArrayList<LockObj>();
    for (LockObj eachLock : locksHeldByTransaction) {
      if (eachLock.getLockType().equals(lockType) && eachLock.getVariableID() == varID) {
        allLocksForVariable.add(eachLock);
      }
    }
    return allLocksForVariable;
  }
  
  /**
   * <strong>setSiteAccessedByTransaction</strong>:adds a list of {@link Site}(s) accessed by a {@link Transaction}
   * @param siteAccessedByTransaction - a list of {@link Site}(s) accessed by a {@link Transaction}
   */
  public void setSiteAccessedByTransaction(ArrayList<Integer>siteAccessedByTransaction) {
    for(int i = 0; i < siteAccessedByTransaction.size(); i++) {
      if (!this.sitesAccessedByTransaction.contains(siteAccessedByTransaction.get(i))) {
        this.sitesAccessedByTransaction.add(siteAccessedByTransaction.get(i));
      }
    }
  }
  
  /**
   * <strong>getSiteAccessedByTransaction</strong>:gets a list of {@link Site}(s) accessed by a {@link Transaction}
   * @return a list of {@link Site}(s) accessed by a {@link Transaction}
   */
  public ArrayList<Integer> getSiteAccessedByTransaction() {
    return this.sitesAccessedByTransaction;
  }
  
  /**
   * <strong>isReadOnly</strong>: determines if a {@link Transaction} is ReadOnly
   * @return true if a {@link Transaction} is ReadOnly, otherwise false
   */
  public boolean isReadOnly() {
	  return this.readOnly;
  }

}
