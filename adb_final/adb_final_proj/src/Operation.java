/**
 * @author pratikkarnik and scottfloam
 * <h1>Operation</h1>
 * <span>The {@link Operation} class stores information about each action performed in the system. </span>
 * <span>For example, the {@link Operation}, R(T1,x1), indicates that {@link Transaction} 1 </span>
 * <span>would like to read {@link Variable} x1.</span>
 */
public class Operation {
  private Integer value;
  private int variableID;
  private String operationType;
  private int operationTime;

  public Operation(int opTime, String opType, int varID, Integer val) {
    this.value = val;
    this.variableID = varID;
    this.operationType = opType;
    this.operationTime = opTime;
  }

  public Operation(int opTime, String opType, int varID) {
    this(opTime, opType, varID, null);
  }

  /**
   * <strong>getValue</strong>: gets the value associated with an {@link Operation}
   * @return the value associated with an {@link Operation}
   */
  public int getValue() {
    return value;
  }

  /**
   * <strong>setValue</strong>: sets the value associated with an {@link Operation}
   * @param value - the value associated with an {@link Operation}
   */
  public void setValue(int value) {
    this.value = value;
  }

  /**
   * <strong>getVariableID</strong> gets the ID of a {@link Variable} associated with this {@link Operation}
   * @return the ID of a {@link Variable} associated with this {@link Operation}
   */
  public int getVariableID() {
    return variableID;
  }

  /**
   * <strong>setVariableID</strong> sets the ID of a {@link Variable} associated with this {@link Operation}
   * @param variableID
   */
  public void setVariableID(int variableID) {
    this.variableID = variableID;
  }

  /**
   * <strong>getOperationType</strong>: gets the operation type (i.e. R, W, etc.) associated with this {@link Operation}
   * @return the type of {@link Operation} performed (i.e. R, W, etc)
   */
  public String getOperationType() {
    return operationType;
  }

  /**
   * <strong>setOperationType</strong>: sets the operation type (i.e. R, W, etc.) associated with this {@link Operation}
   * @param operationType - the type of {@link Operation} performed (i.e. R, W, etc)
   */
  public void setOperationType(String operationType) {
    this.operationType = operationType;
  }

  /**
   * <strong>getOperationTime</strong>: gets the time at which an {@link Operation} occurs
   * @return the time at which an {@link Operation} occurs
   */
  public int getOperationTime() {
    return operationTime;
  }

  /**
   * <strong>setOperationTime</strong>: sets the time at which an {@link Operation} occurs
   * @param operationTime - the time at which an {@link Operation} occurs
   */
  public void setOperationTime(int operationTime) {
    this.operationTime = operationTime;
  }

}
