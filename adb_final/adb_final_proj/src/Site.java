import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author scottfloam and pratikkarnik
 *         <h1>Site</h1> The Site class creates a Site and instantiates a lock table for that site.
 *         Of the 20 existing Variables, Variables with even variable IDs are replicated across all
 *         sites. Variables with odd variable IDs are replicated across sites with the site id (1 +
 *         (varID % 10)). The initialization of sites with variables is conducted in the Site class
 *         during the Site's instantiation. A list of Sites is provided in the DataManager's
 *         ArrayList<Site> sites.
 */
public class Site {
  private int id;
  private LockTable lt;
  private DataTable dataTable;
  private HashMap<String,HashMap<Integer,Variable>> RODataTable;
  private boolean isUp;
  private boolean previouslyFailed;

  public Site(int id) {
    this.id = id;
    this.lt = new LockTable();
    this.dataTable = new DataTable(id);
    this.RODataTable = new HashMap<String,HashMap<Integer,Variable>>();
    this.isUp = true;
    this.previouslyFailed = false;
    
  }

  /**
   * toString
   * 
   * @return site's id
   */
  @Override
  public String toString() {
    StringBuilder siteInformation = new StringBuilder();
    siteInformation.append("Site ID: " + this.id + "\n");

    for (int i = 0; i <= this.dataTable.getDT().size(); i++) {
      String variableInformation = "";
      if (this.dataTable.getDT().containsKey(i)) {
        variableInformation = this.dataTable.getDT().get(i).getID() + " = "
            + this.dataTable.getDT().get(i).getValue();
        siteInformation.append(" - " + variableInformation + "\n");
      }
    }
    return siteInformation.toString();
  }

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
   * fail Sets LockTable (lt) to null
   * 
   * @return null
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
   * getLT
   * 
   * @returns the LockTable associated with the Site
   */
  public LockTable getLT() {
    return this.lt;
  }

  /**
   * getDataTable
   * 
   * @returns the getDataTable associated with the Site
   */
  public DataTable getDataTable() {
    return this.dataTable;
  }


  /**
   * getID
   * 
   * @return the numeric id value of this Site
   */
  public int getID() {
    return this.id;
  }

  /**
   * hasVariable
   * 
   * @return true if a variable is present, false if not.
   */
  public boolean hasVariable(int varID) {
    return this.dataTable.getDT().containsKey(varID);
  }

  public ArrayList<Variable> getVariablesOnSite() {
    ArrayList<Variable> variablesOnSite = new ArrayList<Variable>();
    for(int varID : this.dataTable.getDT().keySet()) {
      variablesOnSite.add(this.dataTable.getDT().get(varID));
    }
    return variablesOnSite;
  }

  public boolean isUp() {
    return isUp;
  }

  public void setUp(boolean isUp) {
    this.isUp = isUp;
  }

  public void initiateWriteToVariables(int varID, int value) {
    Variable currentVariable = dataTable.getDT().get(varID);
    currentVariable.setIntermediateValue(value);
    this.dataTable.updateIntermediateValue(varID, value);
  }
  
  //Sets deep copy of DT in RODT
  public void setRODataTable(String txnID) {
	  HashMap<Integer,Variable> copy = new HashMap<Integer,Variable>(this.dataTable.getDT());
	  this.RODataTable.put(txnID, copy);
  }
  
  public HashMap<Integer,Variable> getRODataTable(String txnID){
	  return this.RODataTable.get(txnID);
  }
  
  public boolean isPreviouslyFailed() {
    return this.previouslyFailed;
  }

}
