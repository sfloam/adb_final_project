import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author scottfloam and pratikkarnik
 * <h1>Site</h1> 
 * <span>The Site class consists of a {@link DataTable} that holds {@link Variable}(s)</span>
 * <span>and a {@link LockTable} corresponding with those {@link Variable}(s). See</span>
 * <span>{@link DataTable} and {@link LockTable} for more information about </span>
 * <span>these respective classes.</span>
 */
public class Site {
  private int id;
  private LockTable lt;
  private DataTable dataTable;
  private HashMap<String,HashMap<Integer,Integer>> RODataTable;
  private boolean isUp;
  private boolean previouslyFailed;

  public Site(int id) {
    this.id = id;
    this.lt = new LockTable();
    this.dataTable = new DataTable(id);
    this.RODataTable = new HashMap<String,HashMap<Integer,Integer>>();
    this.isUp = true;
    this.previouslyFailed = false;
    
  }

  /**
   * <strong>toString</strong>: gets information about the {@link Site}
   * @return the {@link Site}'s ID and its {@link Variable}(s) with their respective values
   */
  @Override
  public String toString() {
    StringBuilder siteInformation = new StringBuilder();
    siteInformation.append("Site ID: " + this.id + "\n");

    for (int i = 1; i <= 20; i++) {
      String variableInformation = "";
      if (this.dataTable.getDT().containsKey(i)) {
        variableInformation = this.dataTable.getDT().get(i).getID() + " = "
            + this.dataTable.getDT().get(i).getValue();
        siteInformation.append(" - " + variableInformation + "\n");
      }
    }
    return siteInformation.toString();
  }

  /**
   * <strong>getVariableWithID</strong>: gets a particular variable from a {@link Site}
   * @param varID - ID of a {@link Variable}
   * @return a particular variable from a {@link Site}
   */
  private Variable getVariableWithID(int varID) {
    ArrayList<Variable> varList = this.getVariablesOnSite();
    for (Variable eachVariable : varList) {
      if (eachVariable.getID() == varID) {
        return eachVariable;
      }
    }
    return null;
  }

  /**
   * <strong>fail</strong>: simulates the failure of a {@link Site}. 
   * <span> When failure occurs, this method sets the {@link Site}'s <strong>isUp</strong></span>
   * <span> flag to false to indicate that the {@link Site} is down.</span>
   * <span> It also removes all {@link LockObj} from the {@link LockTable}.</span>
   * <span> Furthermore, it sets <strong>previouslyFailed</strong> to true to indicate that the {@link Site} failed</span>
   * <span> at some point during the run of the program. The site also replaces the intermediate value</span>
   * <span> which may have been a pending write value, with the last committed value.</span>
   */
  public void fail() {
    this.isUp = false;
    this.previouslyFailed = true;
    ArrayList<LockObj> allLockTableObj = lt.getLockTable();
    for (LockObj eachLock : allLockTableObj) {
      // set intermediate value back to actual value
      int lockedVariableID = eachLock.getVariableID();
      Variable lockedVariable = getVariableWithID(lockedVariableID);
      int lockedVariableValue = lockedVariable.getValue();
      lockedVariable.setIntermediateValue(lockedVariableValue);
    }
    lt.getLockTable().clear();
  }

  /**
   * <strong>recover</strong>: simulates the recovery of a {@link Site}.
   * <span> When recovery occurs, this method will mark the {@link Site} as </span>
   * <span> active and mark whether a {@link Variable} </span>
   * <span> can be read post recovery.</span>
   */
  public void recover() {
    if (!this.isUp) {
      this.isUp = true;
      ArrayList<Variable> varList = this.getVariablesOnSite();
      for(Variable eachVar : varList) {
        if(eachVar.isExclusiveToSite()) {
          eachVar.setFreeToRead(true);
        } else {
          eachVar.setFreeToRead(false);
        }
      }
    }
  }

  /**
   * <strong>getLT</strong>: gets the {@link LockTable} associated with this {@link Site}
   * @returns the {@link LockTable} associated with the {@link Site}
   */
  public LockTable getLT() {
    return this.lt;
  }

  /**
   * <strong>getDataTable</strong>: gets the {@link DataTable} associated with this {@link Site}
   * @returns the {@link DataTable} associated with this {@link Site}
   */
  public DataTable getDataTable() {
    return this.dataTable;
  }


  /**
   * <strong>getID</strong>: gets the {@link Site} ID associated with this {@link Site}
   * @return the numeric ID value of this {@link Site}
   */
  public int getID() {
    return this.id;
  }

  /**
   * <strong>hasVariable</strong>: determines if this {@link Site} has a particular {@link Variable}
   * @return true if a {@link Variable} is present, false if not.
   */
  public boolean hasVariable(int varID) {
    return this.dataTable.getDT().containsKey(varID);
  }

  
  /**
   * <strong>getVariablesOnSite</strong> gets a list of {@link Variables}(s) at this {@link Site}
   * @return a list of {@link Variables}(s) at this {@link Site}
   */
  public ArrayList<Variable> getVariablesOnSite() {
    ArrayList<Variable> variablesOnSite = new ArrayList<Variable>();
    for(int varID : this.dataTable.getDT().keySet()) {
      variablesOnSite.add(this.dataTable.getDT().get(varID));
    }
    return variablesOnSite;
  }

  /**
   * <strong>isUp</strong> determines if the {@link Site} is active
   * @return returns true if the {@link Site} is active, false if not
   */
  public boolean isUp() {
    return isUp;
  }
  
  /**
   * <strong>setUp</strong> marks whether a {@link Site} is active
   * @param isUp - boolean to determine if the {@link Site} is active
   */
  public void setUp(boolean isUp) {
    this.isUp = isUp;
  }

  /**
   * <strong>initiateWriteToVariables</strong> writes to {@link Variable}(s) at a {@link Site}
   * @param varID - ID of a {@link Variable} 
   * @param value - the value of a {@link Variable}
   * @param txnID - ID of a {@link Transaction}
   */
  public void initiateWriteToVariables(int varID, int value, String txnID) {
    Variable currentVariable = dataTable.getDT().get(varID);
    currentVariable.setIntermediateValue(value);
    currentVariable.setIntermediateValueSetBy(txnID);
    this.dataTable.updateIntermediateValue(varID, value);
  }
  
  /**
   * <strong>setRODataTable</strong> adds a {@link Variable} to the RODataTable
   * @param txnID - ID of a {@link Transaction}
   */
  public void setRODataTable(String txnID) {
    HashMap<Integer,Integer> copy = new HashMap<Integer,Integer>();
    for(Integer varID : this.dataTable.getDT().keySet()) {
      int varValue = this.dataTable.getDT().get(varID).getValue();
      copy.put(varID, varValue);
    }
    this.RODataTable.put(txnID, copy);
  }
  
  /**
   * <strong>getRODataTable</strong> a table to preserve the values of {@link Variable}(s) when a ROTransaction begins. </span>
   * <span> This table only contains committed values.</span>
   * @param txnID -ID of a {@link Transaction}
   * @return
   */
  public HashMap<Integer,Integer> getRODataTable(String txnID){
	  return this.RODataTable.get(txnID);
  }
  
  /**
   * <strong>isPreviouslyFailed</strong> determines if the {@link Site} failed at some point in the program.
   * @return true if the {@link Site} failed at some point in the program, otherwise false
   */
  public boolean isPreviouslyFailed() {
    return this.previouslyFailed;
  }

}
