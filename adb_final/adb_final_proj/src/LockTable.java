import java.util.ArrayList;

/**
 * 
 * @author scottfloam and pratikkarnik
 *         <h1>Lock Table</h1>
 * 
 */
public class LockTable {
	private ArrayList<LockObj> lockTable;

	public LockTable() {
		lockTable = new ArrayList<LockObj>();
	}

	public ArrayList<LockObj> getLockTable() {
		return lockTable;
	}

	@Override
	public String toString() {
		StringBuilder lockTableInfo = new StringBuilder();
		for(LockObj eachLock : lockTable) {
			lockTableInfo.append(eachLock + " \n");
		}
		return lockTableInfo.toString();
	}

	public void addLock(String lockType, String txnID, int varID) {
		LockObj newLock = new LockObj(lockType, txnID, varID);
		lockTable.add(newLock);
	}

	public boolean isLockWithTransactionIDPresent(String txnId) {
		for(LockObj eachLock : lockTable) {
			if(eachLock.getTransactionID().equals(txnId)) {
				return true;
			}
		}
		return false;
	}

	public boolean isLockWithVariableIDPresent(int varID) {
		for(LockObj eachLock : lockTable) {
			if(eachLock.getVariableID() == varID) {
				return true;
			}
		}
		return false;
	}

	public boolean isLockPresent(String lockType, String txnId, int varID) {
		for(LockObj eachLock : lockTable) {
			if(eachLock.getLockType().equals(lockType) &&
					eachLock.getTransactionID().equals(txnId) && eachLock.getVariableID() == varID) {
				return true;
			}
		}
		return false;
	}

	public void removeLock(String lockType, String txnId, int varID) {
		for(LockObj eachLock : lockTable) {
			if(eachLock.getLockType().equals(lockType) &&
					eachLock.getTransactionID().equals(txnId) && eachLock.getVariableID() == varID) {
				lockTable.remove(eachLock);
			}
		}
	}

	public void removeLockOnTransactionID(String txnID) {
		for(LockObj eachLock : lockTable) {
			if(eachLock.getTransactionID().equals(txnID)) {
				lockTable.remove(eachLock);
			}
		}
	}

	public ArrayList<LockObj> getAllLocksForVariable(int varID) {
		ArrayList<LockObj> variableLocks = new ArrayList<LockObj>();
		for(LockObj eachLock : lockTable) {
			if(eachLock.getVariableID() == varID) {
				variableLocks.add(eachLock);
			}
		}
		return variableLocks;
	}

	public boolean isReadLockPossible(String txnID, int varID) {
		ArrayList<LockObj> allVariableLocks = this.getAllLocksForVariable(varID);
		if(allVariableLocks.size() == 0) {
			return true;
		}
		boolean doesAnotherTransactionHaveWriteLock = false;
		for(LockObj eachLock : allVariableLocks) {
			if(eachLock.getLockType().equals(GlobalConstants.writeLock)
					&& !eachLock.getTransactionID().equals(txnID)) {
				doesAnotherTransactionHaveWriteLock = true;
			}
		}
		return !doesAnotherTransactionHaveWriteLock;
	}

	public void obtainReadLock(String txnID, int varID) {
		if(isReadLockPossible(txnID, varID)) {
			addLock(GlobalConstants.readLock, txnID, varID);
		}
	}
}