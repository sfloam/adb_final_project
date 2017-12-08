import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * <h1>Transaction Manager</h1> 
 * <span>The {@link TransactionManager} assigns each {@link Operation} provided by the user's</span>
 * <span>text file or {@link Operation} entered directly by the user. The {@link TransactionManager} reads the parsed  </span>
 * <span>{@link Operation} and determines if it is a begin, beginRO, read, write, dump, fail, recover, end. </span>
 * <span> Depending on the {@link Operation}, the {@link TransactionManager} executes an action.</span>
 * 
 */
public class TransactionManager {
  
	private LinkedList<Transaction> aborted;
	private ArrayList<DataManager> dmList;
	private int age;
	private HashMap<Integer, Site> allSitesMap;
	private HashMap<String, Transaction> currentTransactions;
	private int time;
	private HashMap<String, ArrayList<Operation>> transactionWaitList;
	private HashMap<String, Transaction> blockedTransactions;

	public TransactionManager() {
		this.time = 0;
		this.aborted = new LinkedList<Transaction>();
		currentTransactions = new HashMap<String, Transaction>();
		blockedTransactions = new HashMap<String, Transaction>();
		transactionWaitList = new HashMap<String, ArrayList<Operation>>();
		allSitesMap = new HashMap<Integer, Site>();
		this.age = 0;
		this.dmList = new ArrayList<DataManager>();
		for (int i = 0; i < 11; i++) {
			if (i == 0) {
				dmList.add(null);
			} else {
				dmList.add(new DataManager(i));
				allSitesMap.put(i, dmList.get(i).getSite());
			}
		}
	}

	/**
	 * <strong>assignTransaction</strong>: assigns operations (a tick from the text file or user input (i.e. begin(T1)) to perform an {@link Operation}
	 * @param an operation such as (i.e. begin(T1), end(T1), W(T1, x4, 200), beginRO(T1), R(T1, x2), dump(), dump(x4), dump(4), fail(1), recover(1))
	 */
	public void assignTransaction(String operationLine) {
		if (operationLine.startsWith("dump()")) {
			dump();
		}  else if (operationLine.startsWith("dump(x")) {
            int variableIndex = Integer.parseInt(operationLine.substring(6, operationLine.length() - 1));
            dumpX(variableIndex);
		} else if (operationLine.startsWith("dump(")) {
            int siteIndex = Integer.parseInt(operationLine.substring(5, operationLine.length() - 1));
            dumpI(siteIndex);
		} else if (operationLine.startsWith("begin(")) {
			String transactionName = operationLine.substring(6, operationLine.length() - 1);
			startTransaction(transactionName, GlobalConstants.readWriteBegin);
			age++;
		} else if (operationLine.startsWith("beginRO(")) {
			String transactionName = operationLine.substring(8, operationLine.length() - 1);
			startTransaction(transactionName, GlobalConstants.readOnlyBegin);
			createROTablesForTransaction(transactionName);
		} else if (operationLine.startsWith("R(")) {
			String[] transactionInfo = operationLine.substring(2, operationLine.length() - 1).split(",");
			int varIDIndex = transactionInfo[1].indexOf("x") + 1;
			int varID = Integer.parseInt(transactionInfo[1].substring(varIDIndex));
			int value = readTransaction(transactionInfo[0], varID);
			if (value != -1 && value != -2 && value != -3) {
			  //System.out.println("Transaction "+transactionInfo[0]+" read variable "+varID+" with value "+value);
			} else if (value == -1) {
			  makeTransactionWaitForTransactionWithLock(transactionInfo[0], varID);
	            Operation newOperation =
	                new Operation(time, GlobalConstants.readOperation, varID, value);
	            insertToWaitList(transactionInfo[0], newOperation);
	            checkIfCycleExists(transactionInfo[0]);
			} else if(value == -2){
		        System.out.println("An abortion on" + transactionInfo[0]
		            + " occured because it could not execute its read in this scenario.");
				abort(currentTransactions.get(transactionInfo[0]));
			}
		} else if (operationLine.startsWith("W(")) {
			String[] transactionInfo = operationLine.substring(2, operationLine.length() - 1).split(",");
			int varIDIndex = transactionInfo[1].indexOf("x") + 1;
			int varID = Integer.parseInt(transactionInfo[1].substring(varIDIndex));
			int valueToBeWritten = Integer.parseInt(transactionInfo[2].trim());
			writeTransaction(transactionInfo[0], varID, valueToBeWritten);
		} else if (operationLine.startsWith("end(")) {
			String transactionName = operationLine.substring(4, operationLine.length() - 1);
			endTransaction(transactionName);
		} else if (operationLine.startsWith("fail(")) {
			int siteID = Integer.parseInt(operationLine.substring(5, operationLine.length() - 1));
			failSite(siteID);
		} else if (operationLine.startsWith("recover(")) {
			int siteID = Integer.parseInt(operationLine.substring(8, operationLine.length() - 1));
			recoverSite(siteID);
		}
	}

	/**
	 *<strong>startTransaction</strong>: starts the {@link Transaction}, sets the age, and the type of {@link Transaction}, and puts it into the currentTransaction HashMap
	 *@param txnID - id of the {@link Transaction}
	 *@param transactionType - type of {@link Transaction}
	 */
	private void startTransaction(String txnID, String transactionType) {
		if (!currentTransactions.containsKey(txnID)) {
			Transaction newTransaction = new Transaction(txnID, transactionType);
			newTransaction.setAge(age);
			currentTransactions.put(txnID, newTransaction);
		}
	}
	
	/**
     *<strong>createROTablesForTransaction</strong>: creates a read only table
     *@param txnID - id of the {@link Transaction}
     */
	private void createROTablesForTransaction(String transactionName) {
		for (int i = 1; i < dmList.size(); i++) {
			if(dmList.get(i).getSite()!= null){
				dmList.get(i).getSite().setRODataTable(transactionName);
			} else {
				System.out.println("Site Down. Called from createROTablesForTransaction!");
			}
		}
	}

   /**
    * <strong>endTransaction</strong>: This function is called when a particular {@link Transaction} gets an end call.</span>
    * <span>Whenever the {@link Transaction} ends, if the we update the existing value with the intermediate value of the {@link Variable}.</span>
    * <span>Also we remove the {@link LockObj} present on the existing {@link Variable}. If no {@link Site} is available to commit the values,</span>
    * <span>the {@link Transaction} is aborted. Once the {@link Transaction} ends, we execute the {@link Transaction}(s) which are </span>
    * <span>waiting for the {@link Transaction} that just ended.</span>
    * @param txnID - id of the {@link Transaction}
    */
	private void endTransaction(String txnID) {
	  if(currentTransactions.containsKey(txnID)) {
	      Set<Integer> ctSet = currentTransactions.get(txnID).getCorrespondingVars();
	      Integer[] ctIntArr = ctSet.toArray(new Integer[ctSet.size()]);
	      for (int ctIndex = 0; ctIndex < ctIntArr.length; ctIndex++) {
	        int numberOfSitesAvaialbe = 0;
	        int nextVar = ctIntArr[ctIndex];
	        for (int dmIndex = 1; dmIndex < dmList.size(); dmIndex++) {
	          if (dmList.get(dmIndex).getSite().getDataTable().getDT() != null 
	              && dmList.get(dmIndex).getSite().getDataTable().getDT().containsKey(nextVar)) {

	            numberOfSitesAvaialbe++;

	            // update variable value in site DT
	            dmList.get(dmIndex).getSite().getDataTable().getDT().get(nextVar)
	              .setValue(dmList.get(dmIndex).getSite().getDataTable().getDT().get(nextVar).getIntermediateValue());

	            // remove all txn locks
	            dmList.get(dmIndex).getSite().getLT().removeLockOnTransactionID(txnID);
	          }
	        }
	        if (numberOfSitesAvaialbe == 0) {
	          System.out.println("Transaction " + txnID
	              + "aborted because there were no sites avaiable when trying to commit.");
	          abort(currentTransactions.get(txnID));
	          break;
	        }
	      }
	         System.out.println("Transaction " + txnID + " committed");
	        executeOrInformWaitingTransaction(txnID);
	        currentTransactions.remove(txnID);
	  }
	}

	/**
     * <strong>getActiveSitesHavingVariable</strong>This function returns all the {@link Site}(s) which are currently active and have the particular {@link Variable}
     * @param varID - id of the {@link Variable}
     * @return sitesHavingVariable - all the {@link Site}(s) which are currently active and have the particular {@link Variable}
     */
	private ArrayList<Site> getActiveSitesHavingVariable(int varID) {
		ArrayList<Site> sitesHavingVariable = new ArrayList<Site>();
		for (int i = 1; i <= GlobalConstants.sites; i++) {
			Site currentSite = allSitesMap.get(i);
			if (currentSite.isUp()) {
				HashMap<Integer, Variable> varOnSite = currentSite.getDataTable().getDT();
				if (varOnSite.containsKey(varID)) {
					sitesHavingVariable.add(currentSite);
				}
			}
		}
		return sitesHavingVariable;
	}

	/**
	 * <strong>checkIfOlderTransactionHasLockOnVariable</strong> 
	 * <span> This function checks if an existing an older {@link Transaction} </span>
	 * <span> has a {@link LockObj} on a {@link Variable}. To do this, it iterates over all the {@link LockObj}(s) which are present on </span>
	 * <span> the {@link Variable} and gets their respective {@link Transaction}(s). If the age of any of the {@link Transaction} is less than the age of the</span>
	 * <span> {@link Transaction} under consideration and if that older {@link Transaction} holds a write {@link LockObj} on the {@link Variable},</span>
	 * <span> we return the {@link Transaction} ID of that older {@link Transaction} for further processing. If not then the {@link Transaction} </span>
	 * <span> ID of the current {@link Transaction} is returned.
	 * 
	 * @param transactionAge - the age of a {@link Transaction}
	 * @param varID - the id  of a {@link Variable}
	 * @param currentTxnID - the current ID of a {@link Transaction}
	 * @return a {@link Transaction} ID
	 */
  private String checkIfOlderTransactionHasLockOnVariable(int transactionAge, int varID, String currentTxnID) {
    for (int i = 1; i <= GlobalConstants.sites; i++) {
      Site currentSite = allSitesMap.get(i);
      ArrayList<LockObj> locksOnVariable = currentSite.getLT().getAllLocksForVariable(varID);
      for (LockObj eachLock : locksOnVariable) {
        String lockTxnID = eachLock.getTransactionID();
        int lockTxnAge = currentTransactions.get(lockTxnID).getAge();
        if (lockTxnAge < transactionAge
            && eachLock.getLockType().equals(GlobalConstants.writeLock)) {
          return lockTxnID;
        }
      }
    }
    return currentTxnID;
  }

  /**
   * <strong>getAllLocksFromAllSitesForVariable</strong>: This method returns a list of all the write locks on a particular {@link Variable} from all of the {@link Site}(s)
   * @param varID - the id  of a {@link Variable}
   * @return a list of {@link LockObj}(s)
   */
  private ArrayList<LockObj> getAllLocksFromAllSitesForVariable(int varID) {
    ArrayList<LockObj> allLocks = new ArrayList<LockObj>();
    for (int i = 1; i <= GlobalConstants.sites; i++) {
      ArrayList<LockObj> locksAtSite = allSitesMap.get(i).getLT().getAllLocksForVariable(varID);
      for (LockObj eachLock : locksAtSite) {
        if(eachLock.getLockType().equals(GlobalConstants.writeLock)) {
          allLocks.add(eachLock);
        }
      }
    }
    return allLocks;
  }
  
  /**
   * <strong>getAllUniqueLocksFromAllSitesForVariable</strong>: this function returns a list of all the unique {@link LockObj}(s) for a particular {@link Variable} from all the {@link Site}(s)
   * @param varID - the id  of a {@link Variable}
   * @return a list of {@link LockObj}(s)
   */
  private ArrayList<LockObj> getAllUniqueLocksFromAllSitesForVariable(int varID) {
    ArrayList<LockObj> allLocks = new ArrayList<LockObj>();
    for (int i = 1; i <= GlobalConstants.sites; i++) {
      ArrayList<LockObj> locksAtSite = allSitesMap.get(i).getLT().getAllLocksForVariable(varID);
      for (LockObj eachLock : locksAtSite) {
        if (!allLocks.contains(eachLock)) {
          allLocks.add(eachLock);
        }
      }
    }
    return allLocks;
  }

  /**
   * <strong>obtainWriteLocksOnAllVariablesOnActiveSites</strong>: this function tries to obtain write {@link LockObj}(s) on all copies of a {@link Variable}(s) across all the active {@link Site}(s)
   * @param txnID - the id  of a {@link Transaction}
   * @param varID - the id  of a {@link Variable}
   */
  private void obtainWriteLocksOnAllVariablesOnActiveSites(String txnID, int varID) {
    for (int i = 1; i <= GlobalConstants.sites; i++) {
      Site currentSite = allSitesMap.get(i);
      if (currentSite.isUp()) {
        if (currentSite.hasVariable(varID)) {
          currentSite.getLT().addLock(GlobalConstants.writeLock, txnID, varID);
        }
      }
    }
  }

  /**
   * <strong>initiateActualWriteOnSites</strong>: once a write lock is obtained, we initiate the actual writes on the {@link Variable}(s) at the {@link Sites}(s). The value is stored as intermediate values in the {@link Variable} as the {@link Transaction} has not been committed yet.
   * @param txnID - the id  of a {@link Transaction}
   * @param varID - the id  of a {@link Variable}
   * @param value - the value of a {@link Variable}
   */
  private void initiateActualWriteOnSites(String txnID, int varID, int value) {
    currentTransactions.get(txnID).addToCorrespondingVars(varID);
    for (int i = 1; i <= GlobalConstants.sites; i++) {
      Site currentSite = allSitesMap.get(i);
      if (currentSite.hasVariable(varID) && currentSite.isUp()
          && currentSite.getLT().isLockWithTransactionIDPresent(txnID)) {
        currentSite.initiateWriteToVariables(varID, value,txnID);
        ArrayList<Integer> activeSites = getSitesAccessedByTransaction(varID);
        currentTransactions.get(txnID).setSiteAccessedByTransaction(activeSites);
      }
    }
  }

  /**
   * <strong>makeTransactionWaitForTransactionWithLock</strong>: if a transaction cannot obtain a write lock on a {@link Variable}, it needs to wait. However, it has to ensure that it is waiting for the correct {@link Transaction}. This method iterates through all the {@link LockObj}(s) present on a {@link Variable} and if the current {@link Transaction} does not hold a {@link LockObj} on it, and
   * the {@link Transaction} holding the lock does not have any {@link Transaction} waiting for it, then it puts the current {@link Transaction} in the waiting list of the {@link Transaction} holding a {@link LockObj} on it. If the {@link Transaction}
   * holding the {@LockObj} has a {@link Transaction} waiting for it, then we check to see if a deadlock will happen or not if the current {@link Transaction} waits for the {@link Transaction} holding the lock.
   * @param txnID - the id  of a {@link Transaction}
   * @param varID - the id  of a {@link Variable}
   */
  private void makeTransactionWaitForTransactionWithLock(String txnID, int varID) {
    Transaction presentTransaction = this.currentTransactions.get(txnID);
    for (int i = 1; i <= GlobalConstants.sites; i++) {
      Site currentSite = allSitesMap.get(i);
      if (currentSite.isUp()) {
        if (currentSite.getLT().isLockWithVariableIDPresent(varID)) {
          ArrayList<LockObj> allLocksAtSite = currentSite.getLT().getAllLocksForVariable(varID);
          for (LockObj eachLock : allLocksAtSite) {
            String lockTxnID = eachLock.getTransactionID();
            if (!lockTxnID.equals(txnID)) {
              Transaction transactionUnderConsideration = this.currentTransactions.get(lockTxnID);
              if (transactionUnderConsideration
                  .getTransactionWaitingForCurrentTransaction() == null) {
                transactionUnderConsideration.setTransactionWaitingForCurrentTransaction(txnID);
                //presentTransaction.setBlocked(true);
                //blockedTransactions.put(presentTransaction.getID(), presentTransaction);
                presentTransaction.getTransactionsWhichCurrentTransactionWaitsFor().add(lockTxnID);
              } else {
                checkIfDeadlocked(txnID, transactionUnderConsideration.getID());
              }
            }
          }
        }
      }
    }
  }

  /**<strong>insertToWaitList</strong>: this function adds a particular {@link Transaction} and its {@link Operation} to the waitlist.
   * @param txnID - the id  of a {@link Transaction}
   * @param newOp - an {@link Operation}
   */
  private void insertToWaitList(String txnID, Operation newOp) {
    ArrayList<Operation> transactionOps;
    if (transactionWaitList.containsKey(txnID)) {
      transactionOps = transactionWaitList.get(txnID);
      transactionOps.add(newOp);
    } else {
      transactionOps = new ArrayList<Operation>();
      transactionOps.add(newOp);
      transactionWaitList.put(txnID, transactionOps);
    }
  }

  /**
   * <strong>executeOrInformWaitingTransaction</strong>: this function is called whenever a {@link Transaction} ends or aborts. Essentially this function pops a {@link Transaction} from the waiting
   * queue of the current {@link Transaction} and executes its {@link Operation}(s) which were waiting for the ended or aborted {@link Transaction}.
   * @param txnID - the id  of a {@link Transaction}
   */
  private void executeOrInformWaitingTransaction(String txnID) {
    Transaction presentTransaction = this.currentTransactions.get(txnID);
    if (presentTransaction.getTransactionWaitingForCurrentTransaction() != null) {
      Transaction waitingTransaction = this.currentTransactions
          .get(presentTransaction.getTransactionWaitingForCurrentTransaction());

      if(waitingTransaction != null) {
        String waitingTxnID = waitingTransaction.getID();
        if (transactionWaitList.size() > 0 && transactionWaitList.containsKey(waitingTxnID)) {
          waitingTransaction.getTransactionsWhichCurrentTransactionWaitsFor().remove(txnID);
          ArrayList<Operation> waitingTxnOpList = transactionWaitList.get(waitingTxnID);
          Operation firstWaitingOperation = waitingTxnOpList.get(0);
          String firstWaitingOperationType = firstWaitingOperation.getOperationType();
          int firstWaitingOperationVariableID = firstWaitingOperation.getVariableID();
          if (firstWaitingOperationType.equals(GlobalConstants.writeOperation)) {
            obtainAllPossibleWriteLocksOnVariable(firstWaitingOperationVariableID, waitingTxnID);
          }
          if (waitingTransaction.getTransactionsWhichCurrentTransactionWaitsFor().size() == 0) {
            transactionWaitList.remove(waitingTransaction.getID());
            if (firstWaitingOperationType.equals(GlobalConstants.readOperation)) {
              //waitingTransaction.setBlocked(false);
              readTransaction(waitingTxnID, firstWaitingOperationVariableID);
            } else if (firstWaitingOperationType.equals(GlobalConstants.writeOperation)) {
              //waitingTransaction.setBlocked(false);
              writeTransaction(waitingTxnID, firstWaitingOperationVariableID,
                  firstWaitingOperation.getValue());
            }
          }
        }
      }
    }
  }
  
  /**<strong>checkIfCycleExists</strong>: when making a {@link Transaction} wait for another {@link Transaction}, 
   * there is a possibility of a cycle leading to a deadlock. This function gets the waiting {@link Transaction} 
   * of a given {@link Transaction}. Then it iteratively loops and finds the waiting {@link Transaction} of the waiting {@link Transaction}. 
   * If it comes across a null value, it means there is no cycle, however, if we get the same value which we started off with, 
   * then a cycle exists and we abort the youngest {@link Transaction} from the deadlock cycle.
   * 
   * @param txnID - the id  of a {@link Transaction}
   */
  private void checkIfCycleExists(String txnID) {
    boolean cycleExists = false;
    Transaction currentTxn = this.currentTransactions.get(txnID);
    ArrayList<String> txnInCycle = new ArrayList<String>();
    if(currentTxn.getTransactionWaitingForCurrentTransaction() != null) {
      txnInCycle.add(txnID);
      String waitingTxnID = currentTxn.getTransactionWaitingForCurrentTransaction();
      Transaction waitingTxn = this.currentTransactions.get(waitingTxnID);
      txnInCycle.add(waitingTxnID);
      while(!cycleExists) {
        waitingTxnID = waitingTxn.getTransactionWaitingForCurrentTransaction();
        waitingTxn = this.currentTransactions.get(waitingTxnID);
        if(waitingTxn == null) {
          break;
        } else if(waitingTxn.getID().equals(txnID)) {
          cycleExists = true;
        }
        if(!cycleExists) {
          txnInCycle.add(waitingTxnID);
        }
      }
    }
    if(cycleExists) {
      checkFindYoungestTransaction(txnInCycle);
    }
  }

 /**<strong>checkFindYoungestTransaction</strong>: the function aborts the youngest {@link Transaction} in a particular deadlock cycle
  * @param txnInCycle - a list of {@link Transaction}(s) in a cycle
  */
  private void checkFindYoungestTransaction(ArrayList<String> txnInCycle) {
    int youngestAge = Integer.MIN_VALUE;
    ArrayList<String> allTransactions = new ArrayList<String>(txnInCycle);
    String youngestTxnID = this.currentTransactions.get(allTransactions.get(0)).getID();
    for(String txnID : allTransactions) {
      Transaction currentTxn = this.currentTransactions.get(txnID);
      if(currentTxn.getAge() > youngestAge) {
        youngestAge = currentTxn.getAge();
        youngestTxnID = currentTxn.getID();
      }
    }
    System.out.println("Transaction " + youngestTxnID
        + " aborted because it was the youngest transaction and there was deadlock.");
    this.abort(this.currentTransactions.get(youngestTxnID));;
  }
  
  /**
   * <strong>checkIfDeadlocked</strong>: this method checks if a deadlock exists. We compare two {@link Transaction}(s) and check if a deadlock exists between them. 
   * If the {@link Transaction}(s) are waiting on each other, then we abort the younger {@link Transaction}. If there is no {@link Transaction} waiting for the older {@link Transaction} 
   * then we ensure the current {@link Transaction} waits for the older {@link Transaction}. However if the older {@link Transaction} already has a {@link Transaction} waiting for it, 
   * then the current {@link Transaction} waits for the waiting {@link Transaction} of the older {@link Transaction}.
   * @param txnID - the id of a {@link Transaction}
   * @param olderTxnID - the id of an older {@link Transaction}
   */
  private void checkIfDeadlocked(String txnID, String olderTxnID) {
    Transaction txn1 = this.currentTransactions.get(txnID);
    Transaction txn2 = this.currentTransactions.get(olderTxnID);
    if(txn2.getTransactionsWhichCurrentTransactionWaitsFor().contains(txnID)
        && txn1.getTransactionsWhichCurrentTransactionWaitsFor().contains(olderTxnID)) {
      // deadlock
      if (txn1.getAge() < txn2.getAge()) {

      } else {
        System.out.println("Transaction " + txn1.getID()
        + " aborted because it was the youngest transaction and there was deadlock.");
        this.abort(txn1);
      }
    } else if(txn2.getTransactionWaitingForCurrentTransaction() != null) {
      if(!txn2.getTransactionWaitingForCurrentTransaction().equals(txnID)) {
        Transaction waitingTransaction = this.currentTransactions
            .get(txn2.getTransactionWaitingForCurrentTransaction());
        String waitingTxnID = waitingTransaction.getID();
        checkIfDeadlocked(txnID,waitingTxnID);
        //waitingTransaction.setTransactionWaitingForCurrentTransaction(txnID);
        //txn1.getTransactionsWhichCurrentTransactionWaitsFor().add(waitingTxnID);
      } else {
        Transaction waitingTransaction = this.currentTransactions
            .get(txn2.getTransactionWaitingForCurrentTransaction());
        String waitingTxnID = waitingTransaction.getID();
        if(!waitingTxnID.equals(txnID)) {
          waitingTransaction.setTransactionWaitingForCurrentTransaction(txnID);
          txn1.getTransactionsWhichCurrentTransactionWaitsFor().add(waitingTxnID);
        }
      }
    } else{
      txn2.setTransactionWaitingForCurrentTransaction(txnID);
      //txn1.setBlocked(true);
      //blockedTransactions.put(txn1.getID(),txn1);
      txn1.getTransactionsWhichCurrentTransactionWaitsFor().add(txn2.getID());
    }
  }

  /**
   * <strong>obtainAllPossibleWriteLocksOnVariable</strong> In case the {@link Transaction} cannot obtain all write locks on a {@link Variable}, this function tries to obtain
   * as many write locks as possible on it.
   * @param varID - the id of a {@link Variable}
   * @param txnID - the id of a {@link Transaction}
   */
  private void obtainAllPossibleWriteLocksOnVariable(int varID, String txnID) {
    Transaction presentTxn = this.currentTransactions.get(txnID);
    for (int i = 1; i <= GlobalConstants.sites; i++) {
      Site currentSite = allSitesMap.get(i);
      if (currentSite.isUp() && currentSite.hasVariable(varID)) {
        if (!currentSite.getLT().isLockWithVariableIDPresent(varID)) {
          currentSite.getLT().addLock(GlobalConstants.writeLock, txnID, varID);
          presentTxn.addLockToLocksHeldByTransaction(varID, GlobalConstants.writeLock);
        }
      }
    }
  }

  /**<strong>writeTransaction</strong> This handles the functionality of the write operation of a {@link Transaction}. If a {@link Transaction} 
   * does not have a write lock present on a {@link Variable}, it finds the {@link Site}(s) which have the {@link Variable}.
   * If there is an older {@link Transaction} that has a write lock on the {@link Variable} which the current {@link Transaction} needs to write on,
   * we make the current {@link Transaction} wait and check for deadlock. If there is no older {@link Transaction} holding a lock,
   * we check if any locks exist on the {@link Variable} at all. If no locks exists, the current {@link Transaction} obtains the locks and writes to it.
   * If the locks exist and if they are by the same {@link Transaction} and are read locks, then we remove the read locks and obtain write locks on it and
   * execute the write operation. However if some other {@link Transaction} has the locks, then the current {@link Transaction} waits for the {@link Transaction} holding
   * the lock and deadlock is checked. If the current {@link Transaction} had a write lock in the first place, we check if it has all the write locks on the {@link Variable}, 
   * if yes then we execute the write or else the {@link Transaction} waits till it obtains all the write locks. Essentially whenever a {@link Transaction} waits, 
   * we check if a deadlock has occurred and check for cycles and abort the youngest {@link Transaction} in any given deadlock cycle.
   * 
   * @param txnID - the id of a {@link Transaction}
   * @param varID - the id of a {@link Variable}
   * @param value - the value of a {@link Variable}
   */
  private void writeTransaction(String txnID, int varID, int value) {
    // currentTransactions.get(txnID).addToCorrespondingVars(varID);
    if (currentTransactions.containsKey(txnID)) {
      Transaction presentTransaction = currentTransactions.get(txnID);
      if (presentTransaction.getTransactionType().equals(GlobalConstants.readWriteBegin)) {
        if (!presentTransaction.isWriteLockPresentOnVariable(varID)) {
          // If transaction has no write locks
          ArrayList<Site> sitesHavingVariable = getActiveSitesHavingVariable(varID);
          if (sitesHavingVariable.size() > 0) {
            // There are sites which are active and have the variable
            String olderTxnID = checkIfOlderTransactionHasLockOnVariable(
                presentTransaction.getAge(), varID, presentTransaction.getID());
            if (!olderTxnID.equals(presentTransaction.getID())) {
              // older transaction holding lock, so should
              // check for deadlock and wait
              // checkIfDeadlocked(txnID,olderTxnID);
              makeTransactionWaitForTransactionWithLock(txnID, varID);
              checkIfDeadlocked(txnID, olderTxnID);
            } else {
              // If no older transaction holding lock
              ArrayList<LockObj> locksOnVariable = getAllUniqueLocksFromAllSitesForVariable(varID);
              if (locksOnVariable.size() == 0) {
                // No locks on variable so we can do write lock
                obtainWriteLocksOnAllVariablesOnActiveSites(txnID, varID);
                presentTransaction.addLockToLocksHeldByTransaction(varID,
                    GlobalConstants.writeLock);
                initiateActualWriteOnSites(txnID, varID, value);
                Operation newOperation =
                    new Operation(time, GlobalConstants.writeOperation, varID, value);
                presentTransaction.addOperation(newOperation);

              } else if (locksOnVariable.size() == 1) {
                // One lock on variable
                if (locksOnVariable.get(0).getTransactionID().equals(txnID)
                    && locksOnVariable.get(0).getLockType().equals(GlobalConstants.readLock)) {
                  // Lock is by current transaction so delete old lock and get new one
                  for (int i = 1; i <= GlobalConstants.sites; i++) {
                    allSitesMap.get(i).getLT().removeLock(GlobalConstants.readLock, txnID, varID);
                  }
                  presentTransaction.removeLockFromLocksHeldByTransaction(varID);
                  obtainWriteLocksOnAllVariablesOnActiveSites(txnID, varID);
                  presentTransaction.addLockToLocksHeldByTransaction(varID,
                      GlobalConstants.writeLock);
                  initiateActualWriteOnSites(txnID, varID, value);
                  Operation newOperation =
                      new Operation(time, GlobalConstants.writeOperation, varID, value);
                  presentTransaction.addOperation(newOperation);
                } else {
                  // different transaction has write lock so wait
                  makeTransactionWaitForTransactionWithLock(txnID, varID);
                  Operation newOperation =
                      new Operation(time, GlobalConstants.writeOperation, varID, value);
                  insertToWaitList(txnID, newOperation);
                  checkIfCycleExists(txnID);
                  obtainAllPossibleWriteLocksOnVariable(varID, txnID);
                }
              } else {
                // many locks on different sites held by different transaction so wait
                makeTransactionWaitForTransactionWithLock(txnID, varID);
                Operation newOperation =
                    new Operation(time, GlobalConstants.writeOperation, varID, value);
                insertToWaitList(txnID, newOperation);
                checkIfCycleExists(txnID);
                obtainAllPossibleWriteLocksOnVariable(varID, txnID);
              }
            }
          } else {
            // all sites having variable are not active so wait
            makeTransactionWaitForTransactionWithLock(txnID, varID);
            Operation newOperation =
                new Operation(time, GlobalConstants.writeOperation, varID, value);
            insertToWaitList(txnID, newOperation);
            checkIfCycleExists(txnID);
            obtainAllPossibleWriteLocksOnVariable(varID, txnID);
          }
        } else {
          // If transaction has atleast one write lock
          int numberOfLocksOnVariableByTransaction =
              presentTransaction.getAllLocksForVariable(varID, GlobalConstants.writeLock).size();
          int numberOfSitesWithVariable = getAllLocksFromAllSitesForVariable(varID).size();
          if (numberOfLocksOnVariableByTransaction == numberOfSitesWithVariable) {
            // Transaction has all the write lock it needs
            initiateActualWriteOnSites(txnID, varID, value);
            Operation newOperation =
                new Operation(time, GlobalConstants.writeOperation, varID, value);
            presentTransaction.addOperation(newOperation);
          } else {
            // Transaction does not have all the write lock it needs
            obtainAllPossibleWriteLocksOnVariable(varID, txnID);
            makeTransactionWaitForTransactionWithLock(txnID, varID);
            Operation newOperation =
                new Operation(time, GlobalConstants.writeOperation, varID, value);
            insertToWaitList(txnID, newOperation);
            checkIfCycleExists(txnID);
          }
        }
      }
    }
  }

  /**<strong>getSitesAccessedByTransaction</strong>: this returns all the {@link Site}(s) accesses by a particular {@link Transaction} for a particular {@link Variable}
   * @param varID - the id of a {@link Variable}
   * @return all the {@link Site}(s) accesses by a particular {@link Transaction} for a particular {@link Variable}
   */
  private ArrayList<Integer> getSitesAccessedByTransaction(int varID) {
    ArrayList<Integer> sitesAccessedByTransaction = new ArrayList<Integer>();
    for (int i = 1; i <= 10; i++) {
      if (dmList.get(i).getSite().isUp()
          && dmList.get(i).getSite().getDataTable().getDT().containsKey(varID)) {
        sitesAccessedByTransaction.add(dmList.get(i).getSite().getID());
      }
    }
    return sitesAccessedByTransaction;
  }

  /**
   * <strong>readTransaction</strong> checks if the currentTransaction started and goes through each active site's {@link LockTable} and checks
   * if a read-lock is possible. If so, it obtains the read-lock and obtains one of the {@link Variable}'s values. When we are finished reading, 
   * we record the {@link Operation} in the {@link Transaction}'s {@link Operation} list (a queue) only if the read was unsuccessful. This may be 
   * because the {@link Transaction} was blocked. We can then use that list to help us remember where we left off when the {@link Transaction} continues. 
   * If the {@link Transaction} is trying to read on a {@link Variable} on which an older {@link Transaction} is waiting to acquire a read lock, then the
   * read waits for the write to happen. If all of the {@link Sites} containing the {@link Variable} which is to be read are down, then you abort the {@link Transaction}.
   * 
   * @param txnID - the id of a {@link Transaction}
   * @param varID - the id of a {@link Variable}
   * @return value that was read or -1 if no read can happen )
   */
  private int readTransaction(String txnID, int varID) {
    if(currentTransactions.get(txnID) != null) {
      currentTransactions.get(txnID).addToCorrespondingVars(varID);
      Variable var = null;
      boolean allSitesHavingVarDown = true;

      //if (currentTransactions.containsKey(txnID) && !currentTransactions.get(txnID).isBlocked()) {
       if (currentTransactions.containsKey(txnID)) {
        for (int i = 1; i <= 10; i++) {
          if(dmList.get(i).getSite().isUp()) {
            if (!currentTransactions.get(txnID).isReadOnly()
                && dmList.get(i).getSite().hasVariable(varID)
                && dmList.get(i).getSite().getLT().isReadLockPossible(txnID, varID)) {

              boolean isThereAWaitingWriteLock = false;

              for(String waitingTxnID : this.transactionWaitList.keySet()) {
                Transaction waitingTxn = this.currentTransactions.get(waitingTxnID);
                ArrayList<Operation> waitingTxnOps = this.transactionWaitList.get(waitingTxn.getID());

                for(Operation ops : waitingTxnOps) {
                  if(ops.getOperationType().equals(GlobalConstants.writeOperation)
                      && ops.getVariableID() == varID) {
                    isThereAWaitingWriteLock = true;
                  }
                }
              }

              if(!isThereAWaitingWriteLock) {
                if((currentTransactions.get(txnID).getSiteAccessedByTransaction().size() == 0)) {
                  dmList.get(i).getSite().getLT().obtainReadLock(txnID, varID);
                  var = dmList.get(i).getSite().getDataTable().getDT().get(varID);
                  //System.out.println("Varid:" + varID + " val " + var.getValue());
                } else {
                  if(getActiveSitesHavingVariable(varID).size() <= currentTransactions.get(txnID).getSiteAccessedByTransaction().size()) {
                    dmList.get(i).getSite().getLT().obtainReadLock(txnID, varID);
                    var = dmList.get(i).getSite().getDataTable().getDT().get(varID);
                    //System.out.println("Varid:" + varID + " val " + var.getValue());
                  } else {
                    if(currentTransactions.get(txnID).getSiteAccessedByTransaction().contains(i)) {
                      dmList.get(i).getSite().getLT().obtainReadLock(txnID, varID);
                      var = dmList.get(i).getSite().getDataTable().getDT().get(varID);
                    }
                  }
                }
              }

            } else if (currentTransactions.get(txnID).isReadOnly()
                && dmList.get(i).getSite().getRODataTable(txnID) != null
                && dmList.get(i).getSite().getRODataTable(txnID).containsKey(varID)) {
              /*System.out.println("Varid:" + varID + " val "
                  + dmList.get(i).getSite().getRODataTable(txnID).get(varID).getValue());*/
              System.out.println("Transaction "+txnID+" read variable "+varID+" with value "+dmList.get(i).getSite().getRODataTable(txnID).get(varID));
              return dmList.get(i).getSite().getRODataTable(txnID).get(varID);
            }  else {
              continue;
            }
          }
        }

        for(int i = 1; i <= 10; i++) {
          if(dmList.get(i).getSite().isUp() && dmList.get(i).getSite().hasVariable(varID)) {
            allSitesHavingVarDown = false;
          }
        }

        if(allSitesHavingVarDown) {
          return -2;
        }

        if (var == null) {
          //no variable found
          return -1;
        } else {
          System.out.println("Transaction "+txnID+" read variable "+varID+" with value "+var.getValue());
          return var.getValue();
        }
      } else {

        Operation op = new Operation(time, "R", varID);
        time++;
        currentTransactions.get(txnID).operations.add(op);
        // needed for deadlock
        //currentTransactions.get(txnID).setBlocked(true);
        //blockedTransactions.put(currentTransactions.get(txnID).getID(),
            //currentTransactions.get(txnID));

        return -2;
      }
    } else {
      return -3;
    }
  }
  
  /**<strong>failSite</strong>: If a fail command is read by the parser, a {@link Site} is made inactive. When a {@link Site} becomes inactive,
   * we cannot perform read or writes on the {@link Variable}(s) in the {@link Site} until it recovers again. Thus if a {@link Transaction}
   * accessed a particular {@link Site} and then the {@link Site} failed, the {@link Transaction} aborts.
   * 
   * @param siteID - the id of a {@link Site}
   */
	private void failSite(int siteID) {
		dmList.get(siteID).getSite().fail();

        ArrayList<String> transIDs = new ArrayList<String>(currentTransactions.keySet());
        for(int i = 0; i < transIDs.size(); i++) {
          String eachTransID = transIDs.get(i);
          Set<Integer> varIDs = currentTransactions.get(eachTransID).getCorrespondingVars();
          for (Integer eachVarID : varIDs ) {
            if (dmList.get(siteID).getSite().hasVariable(eachVarID)
                && dmList.get(siteID).getSite().isPreviouslyFailed()) {
              if(currentTransactions.containsKey(eachTransID)) {
                abort(currentTransactions.get(eachTransID));
                System.out.println("Transaction: " + eachTransID
                    + " aborted because wrote to a variable at a site before the site failed");
              }
            }
          }
        }
	}

	/**
	 * </strong>recoverSite</strong>: recovers a failed {@link Site}
	 * @param siteID - the id of a {@link Site}
	 */
	private void recoverSite(int siteID) {
		dmList.get(siteID).getSite().recover();
	}

	/**<strong>abort</strong>: This function aborts a {@link Transaction}. Whenever a {@link Transaction} aborts, if the {@link Transaction} has done a write operation,
     * we revert the intermediate value of the {@link Variable} back to its original value.
     * @param youngestTransaction - the youngest {@link Transaction} in the deadlock cycle
     */
	public void abort(Transaction youngestTransaction) {
		System.out.println("Aborted Trxn: " + youngestTransaction);
		// add to list of aborted txns
		aborted.add(youngestTransaction);
		// get txnID of aborted txn
		String txnID = youngestTransaction.getID();
		// remove all locks held by txn
		for (DataManager eachDM : dmList) {
			if (eachDM == null) {
				continue;
			} else {
				eachDM.getSite().getLT().removeLockOnTransactionID(txnID);
			}
		}
		// iterate all the variables touched by a trxn to set intermediate to init value
		Iterator<Integer> correspondingVarIDs = youngestTransaction.getCorrespondingVars().iterator();
		while (correspondingVarIDs.hasNext()) {
			int varID = correspondingVarIDs.next();
			Iterator<DataManager> eachDM = dmList.iterator();
			while (eachDM.hasNext()) {
				DataManager dm = eachDM.next();
				if (dm == null) {
					continue;
				} else {
					Site site = dm.getSite();
					// Restore to intialized value
					if(site.hasVariable(varID)) {
					     if(site.getDataTable().getDT().get(varID).getIntermediateValueSetBy() != null) {
	                         if(site.getDataTable().getDT().get(varID).getIntermediateValueSetBy().equals(txnID)) {
	                             site.getDataTable().getDT().get(varID).setIntermediateValue(varID * 10);
	                         }
					     }
					}
				}
			}
		}
        executeOrInformWaitingTransaction(txnID);
        this.transactionWaitList.remove(txnID);
        currentTransactions.remove(txnID);
	}
    
   /**<strong>dump</strong>: dumps information about all the {@link Site}(s) to the standard output
    */
	public void dump() {
		for (int i = 1; i <= GlobalConstants.sites; i++) {
			// System.out.println(i);
			System.out.print(allSitesMap.get(i).toString());
		}
		System.out.print("\n");
	}

    /**<strong>dumpI</strong>: Dumps information about a particular {@link Site} to the standard output
     * @param siteIndex - the id of a {@link Site}
     */
	public void dumpI(int siteIndex) {
		// Do we need null checks?
		if (allSitesMap.containsKey(siteIndex)) {
			System.out.print(allSitesMap.get(siteIndex).toString());
		}
		// Should we throw exception if site index is invalid?
		System.out.print("\n");
	}

	/**
     * <strong>dumpX</strong> Dumps information about a particular {@link Variable} from all the {@link Site}(s) to the standard output.
     * @param variableID - the id of a {@link Variable}
     */
	public void dumpX(int variableID) {
		for (int i = 1; i <= GlobalConstants.sites; i++) {
			ArrayList<Variable> siteVariables = allSitesMap.get(i).getVariablesOnSite();
			for (Variable eachSiteVariable : siteVariables) {
				if (eachSiteVariable.getID() == variableID) {
					System.out.print("Site ID: " + i + "\n");
					System.out.print(" - " + eachSiteVariable.getID() + " = " +eachSiteVariable.getValue() + "\n");
				}
			}
		}
		System.out.print("\n");
	}

}
