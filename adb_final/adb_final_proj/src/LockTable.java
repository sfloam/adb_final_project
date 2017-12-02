import java.util.ArrayList;

/**
 * 
 * @author scottfloam and pratikkarnik
 *         <h1>Lock Table</h1>
 * 
 */
public class LockTable {
	private ArrayList<Lock> lockTable;

	public LockTable() {
		lockTable = new ArrayList<Lock>();
	}

	public ArrayList<Lock> getLockTable() {
		return lockTable;
	}

	@Override
	public String toString() {
		StringBuilder lockTableInfo = new StringBuilder();
		for(Lock eachLock : lockTable) {
			lockTableInfo.append(eachLock + " \n");
		}
		return lockTableInfo.toString();
	}

	public void addLock(String lockType, String txnId, int varID) {
		Lock newLock = new Lock(varID, txnId, lockType);
		lockTable.add(newLock);
	}

	public boolean isLockWithTransactionIDPresent(String txnId) {
		for(Lock eachLock : lockTable) {
			if(eachLock.getTransactionID().equals(txnId)) {
				return true;
			}
		}
		return false;
	}

	public boolean isLockWithVariableIDPresent(int varID) {
		for(Lock eachLock : lockTable) {
			if(eachLock.getVariableID() == varID) {
				return true;
			}
		}
		return false;
	}

	public boolean isLockPresent(String lockType, String txnId, int varID) {
		for(Lock eachLock : lockTable) {
			if(eachLock.getLockType().equals(lockType) &&
					eachLock.getTransactionID().equals(txnId) && eachLock.getVariableID() == varID) {
				return true;
			}
		}
		return false;
	}

	public void removeLock(String lockType, String txnId, int varID) {
		for(Lock eachLock : lockTable) {
			if(eachLock.getLockType().equals(lockType) &&
					eachLock.getTransactionID().equals(txnId) && eachLock.getVariableID() == varID) {
				lockTable.remove(eachLock);
			}
		}
	}

	public void removeLockOnTransactionID(String txnID) {
		for(Lock eachLock : lockTable) {
			if(eachLock.getTransactionID().equals(txnID)) {
				lockTable.remove(eachLock);
			}
		}
	}

	public ArrayList<Lock> getAllLocksForVariable(int varID) {
		ArrayList<Lock> variableLocks = new ArrayList<Lock>();
		for(Lock eachLock : lockTable) {
			if(eachLock.getVariableID() == varID) {
				variableLocks.add(eachLock);
			}
		}
		return variableLocks;
	}

	public boolean isReadLockPossible(String txnID, int varID) {
		ArrayList<Lock> allVariableLocks = this.getAllLocksForVariable(varID);
		if(allVariableLocks.size() == 0) {
			return true;
		}
		boolean doesAnotherTransactionHaveWriteLock = false;
		for(Lock eachLock : allVariableLocks) {
			if(eachLock.getLockType().equals(GlobalConstants.writeLock)
					&& !eachLock.getTransactionID().equals(txnID)) {
				doesAnotherTransactionHaveWriteLock = true;
			}
		}
		if(!doesAnotherTransactionHaveWriteLock) {
			return true;
		} else {
			return false;
		}
	}

	public void obtainReadLock(String txnID, int varID) {
		if(isReadLockPossible(txnID, varID)) {
			addLock(GlobalConstants.readLock, txnID, varID);
		}
	}
}