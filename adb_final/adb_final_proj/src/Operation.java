
public class Operation {
	private int value;
	private int variableID;
	private String operationType;
	private int operationTime;
	
	public Operation(int val, int varID, String opType, int opTime) {
		this.value = val;
		this.variableID = varID;
		this.operationType = opType;
		this.operationTime = opTime;
	}
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getVariableID() {
		return variableID;
	}
	public void setVariableID(int variableID) {
		this.variableID = variableID;
	}
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	public int getOperationTime() {
		return operationTime;
	}
	public void setOperationTime(int operationTime) {
		this.operationTime = operationTime;
	}

}
