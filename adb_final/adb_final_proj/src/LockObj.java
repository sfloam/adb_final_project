
public class LockObj {
	private int varID;
	private String txnID;
	private String lockType;

	public LockObj(String lockType, String txnID, int varID) {
		this.varID = varID;
		this.txnID = txnID;
		this.lockType = lockType;
	}

	public String getTransactionID() {
		return this.txnID;
	}

	public int getVariableID() {
		return this.varID;
	}

	public String getLockType() {
		return lockType;
	}

}
