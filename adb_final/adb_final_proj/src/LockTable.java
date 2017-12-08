import java.util.ArrayList;

/**
 * @author scottfloam and pratikkarnik
 * <h1>Lock Table</h1>
 * <span>The {@link LockTable} class contains a list of {@link LockObj}(s) on </span> 
 * <span>{@link Variable}(s) held by {@link Transaction}(s) at a given {@link Site}</span>
 */
public class LockTable {
  private ArrayList<LockObj> lockTable;

  public LockTable() {
    lockTable = new ArrayList<LockObj>();
  }

  /**
   * <strong>getLockTable</strong>: gets a list of {@link LockObj}(s) at a {@link Site}
   * @return
   */
  public ArrayList<LockObj> getLockTable() {
    return lockTable;
  }

  /**
   * <strong>toString</strong>: returns a list of {@link LockObj}(s) at a {@link Site}
   */
  @Override
  public String toString() {
    StringBuilder lockTableInfo = new StringBuilder();
    for (LockObj eachLock : lockTable) {
      lockTableInfo.append(eachLock + " \n");
    }
    return lockTableInfo.toString();
  }

  /**
   * <strong>addLock</strong>: adds a {@link LockObj} to the list of {@link LockObj}(s) at a {@link Site}
   * @param lockType - Type of {@link LockObj} (i.e. R, W, etc.)
   * @param txnID - ID of a {@link Transaction}
   * @param varID - ID of a {@link Variable}
   */
  public void addLock(String lockType, String txnID, int varID) {
    LockObj newLock = new LockObj(lockType, txnID, varID);
    lockTable.add(newLock);
  }

  /**
   * <strong>isLockWithTransactionIDPresent</strong>: indicates whether a {@link LockObj} held by a particular {@link Transaction} is present
   * @param txnId - ID of a {@link Transaction}
   * @return true or false depending on if a {@link LockObj} held by a particular {@link Transaction} is present
   */
  public boolean isLockWithTransactionIDPresent(String txnId) {
    for (LockObj eachLock : lockTable) {
      if (eachLock.getTransactionID().equals(txnId)) {
        return true;
      }
    }
    return false;
  }

  /**
   * <strong>isLockWithTransactionIDAndVarIDPresent</strong>: indicates whether a {@link LockObj} held by a particular {@link Transaction} with a specific {@link Variable} ID is present
   * @param txnId - ID of a {@link Transaction}
   * @param varID - ID of a {@link Variable}
   * @return true or false depending on if a {@link LockObj} held by a particular {@link Transaction} with a specific {@link Variable} ID is present
   */
  public boolean isLockWithTransactionIDAndVarIDPresent(String txnId, int varID) {
    for (LockObj eachLock : lockTable) {
      if (eachLock.getTransactionID().equals(txnId) && eachLock.getVariableID() == (varID)) {
        return true;
      }
    }
    return false;
  }

  /**
   * <strong>isLockWithVariableIDPresent</strong>: indicates whether a {@link LockObj} held by a particular {@link Variable} is present
   * @param varID - ID of a {@link Variable}
   * @return true or false depending on if a {@link LockObj} held by a particular {@link Variable} is present
   */
  public boolean isLockWithVariableIDPresent(int varID) {
    for (LockObj eachLock : lockTable) {
      if (eachLock.getVariableID() == varID) {
        return true;
      }
    }
    return false;
  }

  /**
   * <strong>isLockPresent</strong>: indicates whether a {@link LockObj} is present based on the lock-type, {@link Transaction} ID, and {@link Variable} ID provided
   * @param lockType - the type of lock (i.e. R, W, etc.)
   * @param txnId - ID of a {@link Transaction}
   * @param varID - ID of a {@link Variable}
   * @return returns true or false depending on if a {@link LockObj} is present based on the lock-type, {@link Transaction} ID, and {@link Variable} ID provided
   */
  public boolean isLockPresent(String lockType, String txnId, int varID) {
    for (LockObj eachLock : lockTable) {
      if (eachLock.getLockType().equals(lockType) && eachLock.getTransactionID().equals(txnId)
          && eachLock.getVariableID() == varID) {
        return true;
      }
    }
    return false;
  }

  /**
   * <strong>removeLock</strong>: removes a {@link LockObj} from the {@link LockTable}
   * @param lockType - type of lock (i.e. R, W, etc.)
   * @param txnId - ID of a {@link Transaction}
   * @param varID - ID of a {@link Variable}
   */
  public void removeLock(String lockType, String txnId, int varID) {
    for(int i = 0; i < lockTable.size(); i++) {
      if (lockTable.get(i).getLockType().equals(lockType) && lockTable.get(i).getTransactionID().equals(txnId)
          && lockTable.get(i).getVariableID() == varID) {
        lockTable.remove(lockTable.get(i));
      }
    }
  }

  /**
   * <strong>removeLockOnTransactionID</strong>: removes {@link LockObj}(s) from the {@link LockTable} using the {@link Transaction} ID of a {@link Transaction}
   * @param txnID - ID of a {@link Transaction}
   */
  public void removeLockOnTransactionID(String txnID) {
	for (int i = 0; i < lockTable.size(); i++) {
		if (lockTable.get(i).getTransactionID().equals(txnID)) {
			lockTable.remove(lockTable.get(i));
		}
	}
  }

  /**
   * <strong>getAllLocksForVariable</strong>: returns a list of all {@link LockObj}(s) for a given {@link Variable}
   * @param varID - ID of a {@link Variable}
   * @return a list of all {@link LockObj}(s) for a given {@link Variable}
   */
  public ArrayList<LockObj> getAllLocksForVariable(int varID) {
    ArrayList<LockObj> variableLocks = new ArrayList<LockObj>();
    for (int i = 0; i < lockTable.size(); i++) {
    		if (lockTable.get(i).getVariableID() == varID) {
    			variableLocks.add(lockTable.get(i));
    		}
    }
    return variableLocks;
  }

  /**
   * <strong>isReadLockPossible</strong>: determines if a {@link Transaction} can obtain a read lock on a {@link Variable} given a {@link Transaction} ID and {@link Variable} ID
   * @param txnID - ID of a {@link Transaction}
   * @param varID - ID of a {@link Variable}
   * @return true or false depending on if a {@link Transaction} can obtain a read lock on a {@link Variable} given a {@link Transaction} ID and {@link Variable} ID
   */
  public boolean isReadLockPossible(String txnID, int varID) {
    ArrayList<LockObj> allVariableLocks = this.getAllLocksForVariable(varID);
    if (allVariableLocks.size() == 0) {
      return true;
    }
    boolean doesAnotherTransactionHaveWriteLock = false;
    for (LockObj eachLock : allVariableLocks) {
      if (eachLock.getLockType().equals(GlobalConstants.writeLock)
          && !eachLock.getTransactionID().equals(txnID)) {
        doesAnotherTransactionHaveWriteLock = true;
      }
    }
    return !doesAnotherTransactionHaveWriteLock;
  }

  /**
   * <strong>obtainReadLock</strong>: adds a read lock to the {@link LockTable} if a read lock is possible
   * @param txnID - ID of a {@link Transaction}
   * @param varID - ID of a {@link Variable}
   */
  public void obtainReadLock(String txnID, int varID) {
    if (isReadLockPossible(txnID, varID)) {
      addLock(GlobalConstants.readLock, txnID, varID);
    }
  }
}
