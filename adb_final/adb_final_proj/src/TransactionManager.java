import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * <h1>Transaction Manager</h1> The TransactionManager assigns each operation provided by the user's
 * text file. The TransactionManager reads the parsed operation and determines if it is a begin,
 * read, write, read only, dump, fail, etc. Depending on the operation, the TransactionManager
 * executes an action (see below).
 * 
 * <ul>
 * <li>Properties</li>
 * <ul>
 * <li><strong>running:</strong> a linked list of Transaction objects to determine which
 * transactions are currently running</li>
 * <li><strong>age:</strong> a global integer to keep track of the order in which transactions began
 * (may not be needed)</li>
 * <li><strong>dm:</strong> the DataManager object that was passed by ExecuteTransactions</li>
 * </ul>
 * </ul>
 * 
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
		// TODO: May not need all of these
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
	 * assignTransaction
	 * 
	 * @param operations is a tick from the text file (i.e. begin(T1))
	 *        <p>
	 *        <strong>Types of Operations</strong>
	 *        <ul>
	 *        <li>Begin</li>
	 *        <ul>
	 *        <li>Parse begin, T1 (or other transaction number).</li>
	 *        <li>Check the running LinkedList to see if the transaction already began. If so, produce
	 *        an error.</li>
	 *        <li>If it is a new transaction, create a new Transaction object with it's age.</li>
	 *        <li>Update the age age count so the next Transaction will have an higher age (higher ==
	 *        younger).</li>
	 *        <li>Add the transaction object to the end of the running LinkedList.</li>
	 *        </ul>
	 *        <li>Write</li>
	 *        <ul>
	 *        <li>Parse W(T4, x4,35) so that we have [W, T4, x4, 35].</li>
	 *        <li>Check the running LinkedList to find transaction.</li>
	 *        <li>Add the operation we are performing to the transaction's operations ArrayList for
	 *        logging</li>
	 *        <li>Add the variable id to the Transaction's correspondingVars HashSet<Integer> to
	 *        determine which Variables to unlock at the end (when commit or even abort).</li>
	 *        <li>Execute write instructions</li>
	 *        <ul>
	 *        <li>parse varID, transID, and varValue</li>
	 *        <li>If the variable is locked because of another Transaction, we need some action</li>
	 *        <li>Otherwise, lock the Variable, assign previousTransactionID to the ID of the
	 *        Transaction performing the write, and that Variable's correspondingTrasactionID
	 *        HashSet<Integer> to include this Transaction's ID</li>
	 *        </ul>
	 *        </ul>
	 *        <li>Read</li>
	 *        <ul>
	 *        <li>Parse R(T4, x4) so that we have [R, T4, x4].</li>
	 *        <li>Check the running LinkedList to find transaction.</li>
	 *        <li>Add the operation we are performing to the transaction's operations ArrayList for
	 *        logging</li>
	 *        <li>Add the variable id to the Transaction's correspondingVars HashSet<Integer> to
	 *        determine which Variables to unlock at the end (when commit or even abort).</li>
	 *        <li>Execute read instructions</li>
	 *        <ul>
	 *        //TODO: WRITE THIS UP
	 *        </ul>
	 *        </ul>
	 *        <li>Fail</li>
	 *        <ul>
	 *        <li>Parse fail(2) so that we have [fail, 2].</li>
	 *        <li>If we have a site 2 (which we should) then execute the fail command via the
	 *        DataManager class on that site.</li>
	 *        <li>Fail will set the Site's LockTable to null.
	 *        </ul>
	 *        <li>Dump</li>
	 *        <ul>
	 *        <li>Parse dump() so that we have [dump].</li>
	 *        <li>Parse dump("i") [dump, siteID].</li>
	 *        <li>Parse dump("x.j") so that we have [dump, "x.j"]. Then parse "x.j" so that we have
	 *        ["varID","siteID"]</li>
	 *        <li>To simplify the parsing, we require a "." in between "x" and "j" so that we can
	 *        distinguish when the user wants to perform dump(i) v.s. dump(xj)</li>
	 *        </ul>
	 *        <li>End</li>
	 *        <ul>
	 *        <li>Parse end(T2) so that we have [end, T2].</li>
	 *        <li>Parse transID (i.e. 2 from T2).</li>
	 *        <li>Get list of variables associated with the Transaction that ended and point to
	 *        transVars. This will save us time because we will not have to go through each site. We
	 *        only have to go to sites associated with a particular Variable that were found in a
	 *        particular Transaction.</li>
	 *        <li>We check to make sure that the Transaction has variables</li>
	 *        <li>For each variableID in transVars, go to the DataManager get the Variable from
	 *        DataManager's vars, to get the site locations associated with that Variable. Then,
	 *        iterate through those site locations and unlock the variable. Also, we override the
	 *        value of the Variable's value in DataManager's vars with the Variable's value in a
	 *        replicated table to ensure the vars has the most up to date value at the end of a
	 *        committed transaction.
	 *        </ul>
	 *        <ul>
	 *        </ul>
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
			if (value != -1 && value != -2) {
				// TODO: What does a read do? I guess nothing since dump will show it's value?
			} else if (value == -1) {
			  makeTransactionWaitForTransactionWithLock(transactionInfo[0], varID);
	            Operation newOperation =
	                new Operation(time, GlobalConstants.readOperation, varID, value);
	            insertToWaitList(transactionInfo[0], newOperation);
			} else {
				abort(currentTransactions.get(transactionInfo[0]));
			}
		} else if (operationLine.startsWith("W(")) {
			String[] transactionInfo = operationLine.substring(2, operationLine.length() - 1).split(",");
			int varIDIndex = transactionInfo[1].indexOf("x") + 1;
			int varID = Integer.parseInt(transactionInfo[1].substring(varIDIndex));
			int valueToBeWritten = Integer.parseInt(transactionInfo[2].trim());
			// TODO: You need to save the operation
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

	private void startTransaction(String txnID, String transactionType) {
		if (!currentTransactions.containsKey(txnID)) {
			Transaction newTransaction = new Transaction(txnID, transactionType);
			newTransaction.setAge(age);
			currentTransactions.put(txnID, newTransaction);
		}
	}
	
	private void createROTablesForTransaction(String transactionName) {
		for (int i = 1; i < dmList.size(); i++) {
			if(dmList.get(i).getSite()!= null){
				dmList.get(i).getSite().setRODataTable(transactionName);
			} else {
				System.out.println("Site Down. Called from createROTablesForTransaction!");
			}
		}
	}

	// TODO find out what is going on with writes and why not copying
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
	          System.out.println("FAILURE HAPPENED- NO AVAILABLE SITES WHEN TRYING TO COMMIT");
	          abort(currentTransactions.get(txnID));
	          break;
	        }
	      }
	        executeOrInformWaitingTransaction(txnID);
	        currentTransactions.remove(txnID);
	  }
	}

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

  private String checkIfOlderTransactionHasLockOnVariable(int transactionAge, int varID, String currentTxnID) {
    for (int i = 1; i <= GlobalConstants.sites; i++) {
      Site currentSite = allSitesMap.get(i);
      ArrayList<LockObj> locksOnVariable = currentSite.getLT().getAllLocksForVariable(varID);
      for (LockObj eachLock : locksOnVariable) {
        String lockTxnID = eachLock.getTransactionID();
        int lockTxnAge = currentTransactions.get(lockTxnID).getAge();
        if (lockTxnAge < transactionAge && eachLock.getLockType().equals(GlobalConstants.writeLock)) {
          return lockTxnID;
        }
      }
    }
    return currentTxnID;
  }

	private ArrayList<LockObj> getAllLocksFromAllSitesForVariable(int varID) {
		ArrayList<LockObj> allLocks = new ArrayList<LockObj>();
		for (int i = 1; i <= GlobalConstants.sites; i++) {
			ArrayList<LockObj> locksAtSite = allSitesMap.get(i).getLT().getAllLocksForVariable(varID);
			for (LockObj eachLock : locksAtSite) {
			  allLocks.add(eachLock);
			}
		}
		return allLocks;
	}

	private ArrayList<LockObj> getAllUniqueLocksFromAllSitesForVariable(int varID) {
	  ArrayList<LockObj> allLocks = new ArrayList<LockObj>();
	        for (int i = 1; i <= GlobalConstants.sites; i++) {
	            ArrayList<LockObj> locksAtSite = allSitesMap.get(i).getLT().getAllLocksForVariable(varID);
	            for (LockObj eachLock : locksAtSite) {
	                if(!allLocks.contains(eachLock)) {
	                   allLocks.add(eachLock);
	                }
	            }
	        }
	        return allLocks;
	    }

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

	private void initiateActualWriteOnSites(String txnID, int varID, int value) {
	  currentTransactions.get(txnID).addToCorrespondingVars(varID);
		for (int i = 1; i <= GlobalConstants.sites; i++) {
			Site currentSite = allSitesMap.get(i);
			if (currentSite.hasVariable(varID) && currentSite.isUp()
					&& currentSite.getLT().isLockWithTransactionIDPresent(txnID)) {
				currentSite.initiateWriteToVariables(varID, value);
			}
		}
	}

  private void makeTransactionWaitForTransactionWithLock(String txnID, int varID) {
    Transaction presentTransaction = this.currentTransactions.get(txnID);
    for (int i = 1; i <= GlobalConstants.sites; i++) {
      Site currentSite = allSitesMap.get(i);
      if (currentSite.isUp()) {
        if (currentSite.getLT().isLockWithVariableIDPresent(varID)) {
          ArrayList<LockObj> allLocksAtSite = currentSite.getLT().getAllLocksForVariable(varID);
          for (LockObj eachLock : allLocksAtSite) {
            String lockTxnID = eachLock.getTransactionID();
            if(!lockTxnID.equals(txnID)) {
              Transaction transactionUnderConsideration = this.currentTransactions.get(lockTxnID);
              if (transactionUnderConsideration.getTransactionWaitingForCurrentTransaction() == null) {
                transactionUnderConsideration.setTransactionWaitingForCurrentTransaction(txnID);
                presentTransaction.setBlocked(true);
                blockedTransactions.put(presentTransaction.getID(),presentTransaction);
                presentTransaction.getTransactionsWhichCurrentTransactionWaitsFor().add(lockTxnID);
              } else {
                checkIfDeadlocked(txnID,transactionUnderConsideration.getID());
              }
            }
          }
        }
      }
    }
  }

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
              waitingTransaction.setBlocked(false);
              readTransaction(waitingTxnID, firstWaitingOperationVariableID);
            } else if (firstWaitingOperationType.equals(GlobalConstants.writeOperation)) {
              waitingTransaction.setBlocked(false);
              writeTransaction(waitingTxnID, firstWaitingOperationVariableID,
                  firstWaitingOperation.getValue());
            }
          }
        }
      }
    }
  }
  
  private void checkIfCycleExists(String txnID) {
    boolean cycleExists = false;
    Transaction currentTxn = this.currentTransactions.get(txnID);
    if(currentTxn.getTransactionWaitingForCurrentTransaction() != null) {
      String waitingTxnID = currentTxn.getTransactionWaitingForCurrentTransaction();
      Transaction waitingTxn = this.currentTransactions.get(waitingTxnID);
      while(!cycleExists) {
        waitingTxnID = waitingTxn.getTransactionWaitingForCurrentTransaction();
        waitingTxn = this.currentTransactions.get(waitingTxnID);
        if(waitingTxn == null) {
          break;
        } else if(waitingTxn.getID().equals(txnID)) {
          cycleExists = true;
        }
      }
    }
    if(cycleExists) {
      checkFindYoungestTransaction();
    }
  }

  private void checkFindYoungestTransaction() {
    int youngestAge = Integer.MIN_VALUE;
    ArrayList<String> allTransactions = new ArrayList<String>(this.currentTransactions.keySet());
    String youngestTxnID = this.currentTransactions.get(allTransactions.get(0)).getID();
    for(String txnID : allTransactions) {
      Transaction currentTxn = this.currentTransactions.get(txnID);
      if(currentTxn.getAge() > youngestAge) {
        youngestAge = currentTxn.getAge();
        youngestTxnID = currentTxn.getID();
      }
    }
    this.abort(this.currentTransactions.get(youngestTxnID));;
  }

  private void checkIfDeadlocked(String txnID, String olderTxnID) {
    Transaction txn1 = this.currentTransactions.get(txnID);
    Transaction txn2 = this.currentTransactions.get(olderTxnID);

    if(txn2.getTransactionsWhichCurrentTransactionWaitsFor().contains(txnID)
        && txn1.getTransactionsWhichCurrentTransactionWaitsFor().contains(olderTxnID)) {
      //deadlock
      if(txn1.getAge() < txn2.getAge()) {
        
      } else {
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
      txn1.setBlocked(true);
      blockedTransactions.put(txn1.getID(),txn1);
      txn1.getTransactionsWhichCurrentTransactionWaitsFor().add(txn2.getID());
    }
  }

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

  private void writeTransaction(String txnID, int varID, int value) {
    //currentTransactions.get(txnID).addToCorrespondingVars(varID);
    if (currentTransactions.containsKey(txnID)) {
      Transaction presentTransaction = currentTransactions.get(txnID);
      if (presentTransaction.getTransactionType().equals(GlobalConstants.readWriteBegin)) {
        if (!presentTransaction.isWriteLockPresentOnVariable(varID)) {
          // If transaction has no write locks
          ArrayList<Site> sitesHavingVariable = getActiveSitesHavingVariable(varID);
          if (sitesHavingVariable.size() > 0) {
            // There are sites which are active and have the variable
            String olderTxnID = checkIfOlderTransactionHasLockOnVariable(presentTransaction.getAge(), varID,presentTransaction.getID());
            if (!olderTxnID.equals(presentTransaction.getID())) {
              // older transaction holding lock, so should
              // check for deadlock and wait
              //resolveDeadLock();
              //checkIfDeadlocked(txnID,olderTxnID);
              makeTransactionWaitForTransactionWithLock(txnID, varID);
              checkIfDeadlocked(txnID,olderTxnID);
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
          }
        }
      }
    }
  }

	/**
	 * readTransaction checks if the currentTransaction started and if it is blocked. If the
	 * transaction started and is not blocked then it goes through each site's lock table and checks
	 * if a read-lock is possible. If so, it obtains the read-lock and obtains one of the variable's
	 * values. It doens't matter var will be overwritten by copies in other tables because each site
	 * has the same value. When we are finished reading, we record the operation in the Transaction's
	 * operation list (a queue) only if the read was unsuccessful. This may be because the transaction
	 * was blocked. We can then use that list to help us remember where we left off when the
	 * transaction continues.
	 * 
	 * @param txnID
	 * @param varID
	 * @return value that was read or -1 if no read can happen )
	 */
	private int readTransaction(String txnID, int varID) {
		currentTransactions.get(txnID).addToCorrespondingVars(varID);
		Variable var = null;

		if (currentTransactions.containsKey(txnID) && !currentTransactions.get(txnID).isBlocked()) {
			for (DataManager eachDM : dmList) {
				if (eachDM == null) {
					continue;
				}else if (!currentTransactions.get(txnID).isReadOnly() && eachDM.getSite().hasVariable(varID)
						&& eachDM.getSite().getLT().isReadLockPossible(txnID, varID)) {
					eachDM.getSite().getLT().obtainReadLock(txnID, varID);
					var = eachDM.getSite().getDataTable().getDT().get(varID);
				} else if (currentTransactions.get(txnID).isReadOnly() && eachDM.getSite().getRODataTable(txnID) != null
						&& eachDM.getSite().getRODataTable(txnID).containsKey(varID)) {
					return eachDM.getSite().getRODataTable(txnID).get(varID).getValue();
				}
			}
			if (var == null) {
				return -1;
			} else {
				return var.getValue();
			}
		} else {

			Operation op = new Operation(time, "R", varID);
			time++;
			currentTransactions.get(txnID).operations.add(op);
			// needed for deadlock
			currentTransactions.get(txnID).setBlocked(true);
			blockedTransactions.put(currentTransactions.get(txnID).getID(), currentTransactions.get(txnID));

			return -2;
		}
	}

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
                System.out.println("Transaction:"+eachTransID + " Aborted because wrote to an odd variable at site that previously failed and no duplicates were available. ");
              }
            }
          }
        }
	}

	private void recoverSite(int siteID) {
		dmList.get(siteID).getSite().recover();
	}

	// TODO: Identify what needs to happen during an abortion
	// TODO: If you need to revert a value back, you should use dm.getvars() because
	// its last commited and 2pl states nothing could have written to it
	// TODO: b/c its locked and vars is updated to last commmited
	public void resolveDeadLock() {
		boolean isDeadlocked = false;
		Transaction youngestTransaction = null;
		Integer youngestTransactionAge = -1;

		Iterator<String> eachTransaction = blockedTransactions.keySet().iterator();
		while (eachTransaction.hasNext()) {
			String currTrans = eachTransaction.next();
			if (blockedTransactions.get(currTrans).getAge() > youngestTransactionAge) {
				youngestTransactionAge = blockedTransactions.get(currTrans).getAge();
				youngestTransaction = blockedTransactions.get(currTrans);

			}
			if (!blockedTransactions.get(currTrans).isBlocked()) {
				isDeadlocked = false;
				System.out.println("NO DEADLOCK");
			}
		}

		if (isDeadlocked) {
			System.out.println("DEADLOCK!");
			abort(youngestTransaction);
		}
	}

	// TODO : Fix Abort functionaltiy
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
		                 site.getDataTable().getDT().get(varID).setIntermediateValue(varID * 10);
					}
				}
			}
		}
        executeOrInformWaitingTransaction(txnID);
        this.transactionWaitList.remove(txnID);
        currentTransactions.remove(txnID);
	}

	public void dump() {
		for (int i = 1; i <= GlobalConstants.sites; i++) {
			// System.out.println(i);
			System.out.print(allSitesMap.get(i).toString());
		}
		System.out.print("\n");
	}

	public void dumpI(int siteIndex) {
		// Do we need null checks?
		if (allSitesMap.containsKey(siteIndex)) {
			System.out.print(allSitesMap.get(siteIndex).toString());
		}
		// Should we throw exception if site index is invalid?
		System.out.print("\n");
	}

	//Fixed DumpX--> was not printing correctly
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
