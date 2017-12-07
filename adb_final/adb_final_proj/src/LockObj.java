/**
 * <h1>Lock Object</h1>
 * <span>The {@link LockObj} stores information about a lock held by a {@link Transaction}.</span>
 * <span> These locks are used to determine if a {@link Transaction} can access a variable a particular {@link Site}.</span>
 * @author scottfloam and pratikkarnik
 *
 */
public class LockObj {
  private int variableID;
  private String transactionID;
  private String lockType;

  public LockObj(String typeOfLock, String txnID, int varID) {
    this.variableID = varID;
    this.transactionID = txnID;
    this.lockType = typeOfLock;
  }

  /**
   * <strong>getVariableID</strong>: gets the ID of the {@link Variable} associated with this lock
   * @return
   */
  public int getVariableID() {
    return variableID;
  }

  /**
   * <strong>setVariableID</strong>: sets the ID of the {@link Variable} associated with this lock
   * @param variableID
   */
  public void setVariableID(int variableID) {
    this.variableID = variableID;
  }

  /**
   * <strong>getTransactionID</strong>: gets the ID of the {@link Transaction} associated with this lock
   * @return
   */
  public String getTransactionID() {
    return transactionID;
  }

  /**
   * <strong>setTransactionID</strong>: sets the ID of the {@link Transaction} associated with this lock
   */
  public void setTransactionID(String transactionID) {
    this.transactionID = transactionID;
  }

  /**
   * <strong>getLockType</strong>: gets the locktype (i.e. R, W, etc.) of the {@link Transaction} associated with this lock
   * @return
   */
  public String getLockType() {
    return lockType;
  }

  public void setLockType(String lockType) {
    this.lockType = lockType;
  }

  @Override
  public String toString() {
    return this.variableID + " " + this.transactionID + " " + this.lockType;
  }

  @Override
  public boolean equals(Object obj) {
    boolean isEqual = false;
    LockObj currentLock = (LockObj) obj;
    if(this.transactionID.equals(currentLock.getTransactionID())
        && this.lockType.equals(currentLock.getLockType()) && this.variableID == currentLock.getVariableID()) {
      isEqual = true;
    }
    return isEqual;
  }

}
