
public class LockObj {
	private int variableID;
	private String transactionID;
	private String lockType;

	public LockObj(String typeOfLock, String txnID, int varID) {
		this.variableID = varID;
		this.transactionID = txnID;
		this.lockType = typeOfLock;
	}

	public int getVariableID() {
		return variableID;
	}

	public void setVariableID(int variableID) {
		this.variableID = variableID;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getLockType() {
		return lockType;
	}

	public void setLockType(String lockType) {
		this.lockType = lockType;
	}

	@Override
	public String toString() {
		return this.variableID + " " + this.transactionID + " " +this.lockType;
	}

}
