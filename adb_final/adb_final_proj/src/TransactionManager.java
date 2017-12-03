import java.util.LinkedList;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * <h1>Transaction Manager</h1> 
 * The TransactionManager assigns each operation provided by the user's text file. 
 * The TransactionManager reads the parsed operation and determines if it is a begin,
 * read, write, read only, dump, fail, etc. Depending on the operation, the 
 * TransactionManager executes an action (see below).
 * 
 * <ul>
 * 		<li>Properties</li>
 * 		<ul>
 * 			<li><strong>running:</strong> a linked list of Transaction objects to determine which transactions are currently running</li>
 * 			<li><strong>age:</strong> a global integer to keep track of the order in which transactions began (may not be needed)</li>
 * 			<li><strong>dm:</strong> the DataManager object that was passed by ExecuteTransactions</li>
 * 		</ul>
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

	public TransactionManager() {
		// TODO: May not need all of these
		this.time = 0;
		this.aborted = new LinkedList<Transaction>();
		currentTransactions = new HashMap<String, Transaction>();
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
	 * @param operations is a tick from the text file (i.e. begin(T1))
	 * <p>
	 * <strong>Types of Operations</strong>
	 * 			<ul>
	 * 			<li>Begin</li>
	 *            	<ul>
	 *            	<li>Parse begin, T1 (or other transaction number).</li>
	 *            	<li>Check the running LinkedList to see if the transaction already began. If so, produce an error.</li>
	 *            	<li>If it is a new transaction, create a new Transaction object with it's age.</li>
	 *           	<li>Update the age age count so the next Transaction will have an higher age (higher == younger).</li>
	 *           	<li>Add the transaction object to the end of the running LinkedList.</li>
	 *            	</ul>
	 *            <li>Write</li>
	 *            	<ul>
	 *            	<li>Parse W(T4, x4,35) so that we have [W, T4, x4, 35].</li>
	 *            	<li>Check the running LinkedList to find transaction.</li>
	 *            	<li>Add the operation we are performing to the transaction's operations ArrayList for logging</li>
	 *           	<li>Add the variable id to the Transaction's correspondingVars HashSet<Integer> to determine which Variables to unlock at the end (when commit or even abort).</li>
	 *           	<li>Execute write instructions</li>
	 *           		<ul>
	 *           		<li> parse varID, transID, and varValue</li>
	 * 					<li> If the variable is locked because of another Transaction, we need some action</li>
	 * 					<li> Otherwise, lock the Variable, assign previousTransactionID to the ID of the 
	 * 						Transaction performing the write, and that Variable's correspondingTrasactionID
	 * 						HashSet<Integer> to include this Transaction's ID</li>
	 *            		</ul>
	 *            	</ul>
	 *             <li>Read</li>
	 *            	<ul>
	 *            	<li>Parse R(T4, x4) so that we have [R, T4, x4].</li>
	 *            	<li>Check the running LinkedList to find transaction.</li>
	 *            	<li>Add the operation we are performing to the transaction's operations ArrayList for logging</li>
	 *           	<li>Add the variable id to the Transaction's correspondingVars  HashSet<Integer> to determine which Variables to unlock at the end (when commit or even abort).</li>
	 *           	<li>Execute read instructions</li>
	 *           		<ul>
	 *           		//TODO: WRITE THIS UP
	 *            		</ul>
	 *            	</ul>
	 *              <li>Fail</li>
	 *              		<ul>
	 *              		<li>Parse fail(2) so that we have [fail, 2].</li>
	 *              		<li>If we have a site 2 (which we should) then execute the fail command via the DataManager class on that site.</li>
	 *              		<li>Fail will set the Site's LockTable to null.
	 *              		</ul>
	 *              <li>Dump</li>
	 *              		<ul>
	 *              		<li>Parse dump() so that we have [dump].</li>
	 *                  <li>Parse dump("i")  [dump, siteID].</li>
	 *                  <li>Parse dump("x.j") so that we have [dump, "x.j"]. Then parse "x.j" so that we have ["varID","siteID"] </li>
	 *              		<li>To simplify the parsing, we require a "." in between "x" and "j" so that we can distinguish when the user wants to perform dump(i) v.s. dump(xj)</li>
	 *              		</ul>
	 *              <li>End</li>
	 *              		<ul>
	 *              		<li>Parse end(T2) so that we have [end, T2].</li>
	 *              		<li>Parse transID (i.e. 2 from T2).</li>
	 *              		<li>Get list of variables associated with the Transaction that ended and point to transVars. 
	 *              			This will save us time because we will not have to go through each site. We only have 
	 *              			to go to sites associated with a particular Variable that were found in a particular Transaction.</li>
	 *              		<li>We check to make sure that the Transaction has variables</li>
	 *              		<li>For each variableID in transVars, go to the DataManager get the Variable from DataManager's vars,
	 *              			to get the site locations associated with that Variable. Then, iterate through those site locations
	 *              			and unlock the variable. Also, we override the value of the Variable's value in DataManager's vars with 
	 *              			the Variable's value in a replicated table to ensure the vars has the most up to date value at the
	 *              			end of a committed transaction.
	 *              		</ul>
	 *              <ul>
	 *            </ul>
	 */
	public void assignTransaction(String operationLine) {
		if (operationLine.startsWith("dump()")) {
			dump();
		} else if (operationLine.startsWith("dump(")) {
			int siteIndex = Integer.parseInt(operationLine.substring(5, operationLine.length() - 1));
			dumpI(siteIndex);
		} else if (operationLine.startsWith("dump(x")) {
			int variableIndex = Integer.parseInt(operationLine.substring(6, operationLine.length() - 1));
			dumpX(variableIndex);
		} else if (operationLine.startsWith("begin(")) {
			String transactionName = operationLine.substring(6, operationLine.length() - 1);
			startTransaction(transactionName, GlobalConstants.readWriteBegin);
		} else if (operationLine.startsWith("beginRO(")) {
			String transactionName = operationLine.substring(8, operationLine.length() - 1);
			startTransaction(transactionName, GlobalConstants.readOnlyBegin);
		} else if (operationLine.startsWith("R(")) {
			String[] transactionInfo = operationLine.substring(2, operationLine.length() - 1).split(",");
			int varIDIndex = transactionInfo[1].indexOf("x") + 1;
			int varID = Integer.parseInt(transactionInfo[1].substring(varIDIndex));
			int value = readTransaction(transactionInfo[0], varID);
			if (value != -1) {
				// TODO: What does a read physically do. I guess nothing since dump will show
				// it's value?
			} else {
				resolveDeadLock();
			}
		} else if (operationLine.startsWith("W(")) {
			String[] transactionInfo = operationLine.substring(2, operationLine.length() - 1).split(",");
			int varIDIndex = transactionInfo[1].indexOf("x") + 1;
			int varID = Integer.parseInt(transactionInfo[1].substring(varIDIndex));
			System.out.println(transactionInfo[2]);
			int valueToBeWritten = Integer.parseInt(transactionInfo[2]);
			// TODO: You need to save the operation
			writeTransaction(transactionInfo[0], varID, valueToBeWritten);
		} else if (operationLine.startsWith("end(")) {
			String transactionName = operationLine.substring(4, operationLine.length() - 1);
			endTransaction(transactionName);
		} else if (operationLine.startsWith("fail(")) {
			int siteID = Integer.parseInt(operationLine.substring(5, operationLine.length() - 1));
			failSite(siteID);
		} else if (operationLine.startsWith("recover(")) {
			int siteID = Integer.parseInt(operationLine.substring(5, operationLine.length() - 1));
			recoverSite(siteID);
		}
	}

	private void startTransaction(String transactionType, String txnID) {
		if (!currentTransactions.containsKey(txnID)) {
			Transaction newTransaction = new Transaction(txnID, transactionType);
			currentTransactions.put(txnID, newTransaction);
		}
	}

	private void endTransaction(String txnID) {

	}

	private ArrayList<Site> getUpSitesHavingVariable(int varID) {
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

	private boolean checkIfOlderTransactionHasLockOnVariable(int transactionAge, int varID) {
		for (int i = 1; i <= GlobalConstants.sites; i++) {
			Site currentSite = allSitesMap.get(i);
			ArrayList<LockObj> locksOnVariable = currentSite.getLT().getAllLocksForVariable(varID);
			for (LockObj eachLock : locksOnVariable) {
				String lockTxnID = eachLock.getTransactionID();
				int lockTxnAge = currentTransactions.get(lockTxnID).getAge();
				if (lockTxnAge < transactionAge) {
					return true;
				}
			}
		}
		return false;
	}

	private ArrayList<LockObj> getAllLocksFromAllSitesForVariable(int varID) {
		ArrayList<LockObj> allLocks = new ArrayList<LockObj>();
		for (int i = 1; i < GlobalConstants.sites; i++) {
			ArrayList<LockObj> locksAtSite = allSitesMap.get(i).getLT().getAllLocksForVariable(varID);
			for (LockObj eachLock : locksAtSite) {
				allLocks.add(eachLock);
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
		for(int i = 1; i <= GlobalConstants.sites; i++) {
			Site currentSite = allSitesMap.get(i);
			if(currentSite.isUp()) {
				if(currentSite.getLT().isLockWithVariableIDPresent(varID)) {
					ArrayList<LockObj> allLocksAtSite = currentSite.getLT().getAllLocksForVariable(varID);
					for(LockObj eachLock : allLocksAtSite) {
						String lockTxnID = eachLock.getTransactionID();
						Transaction transactionUnderConsideration = this.currentTransactions.get(lockTxnID);
						if(transactionUnderConsideration.getTransactionWaitingForCurrentTransaction() == null) {
							transactionUnderConsideration.setTransactionWaitingForCurrentTransaction(txnID);
							presentTransaction.getTransactionsWhichCurrentTransactionWaitsFor().add(lockTxnID);
						} else {
							establishCorrectWaitingOrderOrAbort(txnID, lockTxnID);
						}
					}
				}
			}
		}
	}
	
	private void establishCorrectWaitingOrderOrAbort(String txnID1, String txnID2) {
		if(txnID1.equals(txnID2)) {
			return;
		} else {
			Transaction txn1 = this.currentTransactions.get(txnID1);
			Transaction txn2 = this.currentTransactions.get(txnID2);
			if(txn2.getTransactionWaitingForCurrentTransaction() == null) {
				txn2.setTransactionWaitingForCurrentTransaction(txnID1);
				txn1.getTransactionsWhichCurrentTransactionWaitsFor().add(txnID2);
			}
		}
	}

	private void writeTransaction(String txnID, int varID, int value) {
		if (currentTransactions.containsKey(txnID)) {
			Transaction presentTransaction = currentTransactions.get(txnID);
			if (presentTransaction.getTransactionType().equals(GlobalConstants.readWriteBegin)) {
				if (!presentTransaction.isWriteLockPresentOnVariable(varID)) {
					ArrayList<Site> sitesHavingVariable = getUpSitesHavingVariable(varID);
					if (sitesHavingVariable.size() > 0) {
						if (checkIfOlderTransactionHasLockOnVariable(presentTransaction.getAge(), varID)) {
							// abort txn or wait?
							// notify if any txn wsa waiting on this one
						} else {
							ArrayList<LockObj> locksOnVariable = getAllLocksFromAllSitesForVariable(varID);
							if (locksOnVariable.size() == 0) {
								obtainWriteLocksOnAllVariablesOnActiveSites(txnID, varID);
								presentTransaction.addLockToLocksHeldByTransaction(varID, GlobalConstants.writeLock);
								initiateActualWriteOnSites(txnID, varID, value);
								Operation newOperation = new Operation(age, GlobalConstants.writeOperation, varID,
										value);
								presentTransaction.addOperation(newOperation);
							} else if (locksOnVariable.size() == 1) {
								if (locksOnVariable.get(0).getTransactionID().equals(txnID)
										&& locksOnVariable.get(0).getLockType().equals(GlobalConstants.readLock)) {
									for (int i = 1; i <= GlobalConstants.sites; i++) {
										allSitesMap.get(i).getLT().removeLock(GlobalConstants.readLock, txnID, varID);
									}
									presentTransaction.removeLockFromLocksHeldByTransaction(varID);
									obtainWriteLocksOnAllVariablesOnActiveSites(txnID, varID);
									presentTransaction.addLockToLocksHeldByTransaction(varID,
											GlobalConstants.writeLock);
									initiateActualWriteOnSites(txnID, varID, value);
									Operation newOperation = new Operation(age, GlobalConstants.writeOperation, varID,
											value);
									presentTransaction.addOperation(newOperation);
								} else {
									// Waiting logic
								}
							} else {
								// process and wait
							}
						}
					} else {
						// all sites are down -- wait
					}
				} else {
					// check if it has all write locks if not then wait
				}
			}
		}
	}

	/**
	 * readTransaction checks if the currentTransaction started and if it is blocked. If the transaction started and is not blocked then
	 * it goes through each site's lock table and checks if a read-lock is possible. If so, it obtains the read-lock and obtains one of 
	 * the variable's values. It doens't matter var will be overwritten by copies in other tables because each site has the same value.
	 * When we are finished reading, we record the operation in the Transaction's operation list (a queue) only if the read was unsuccessful.
	 * This may be because the transaction was blocked. We can then use that list to help us remember where we left off when the transaction
	 * continues.
	 * 
	 * @param txnID
	 * @param varID
	 * @return value that was read or -1 if no read can happen
	 */
	private int readTransaction(String txnID, int varID) {
		currentTransactions.get(txnID).addToCorrespondingVars(varID);
		Variable var = null;
		if (currentTransactions.containsKey(txnID) && !currentTransactions.get(txnID).isBlocked()) {
			for (DataManager eachDM : dmList) {
				if (eachDM.getSite().hasVariable(varID) && eachDM.getSite().getLT().isReadLockPossible(txnID, varID)) {
					// TODO: we are double checking if readlock is possible in obtainReadLock
					eachDM.getSite().getLT().obtainReadLock(txnID, varID);
					var = eachDM.getSite().getDataTable().getDT().get(varID);
				}
			}
		}

		if (var != null) {
			return var.getValue();
		} else {
			Operation op = new Operation(time, "R", varID);
			time++;
			currentTransactions.get(txnID).operations.add(op);
			// needed for deadlock
			currentTransactions.get(txnID).setBlocked(true);
			return -1;
		}
	}

	private void failSite(int siteID) {
		dmList.get(siteID).getSite().fail();
	}

	private void recoverSite(int siteID) {
		// TODO: RECOVER SITE
	}

	/**
	 * executeWriteInstruction
	 * <ul>
	 * <li> parse varID, transID, and varValue</li>
	 * <li> If the variable is locked because of another Transaction, we need some action</li>
	 * <li> Otherwise, lock the Variable, assign previousTransactionID to the ID of the 
	 * 		Transaction performing the write, and that Variable's correspondingTrasactionID
	 * 		HashSet<Integer> to include this Transaction's ID</li>
	 * 
	 * I imagine that having this correspondingTransactionID will allow us to keep track of which
	 * Transactions are associated with which variables. This may not be necessary though in the
	 * long run since all Transaction(s) have associated Variable IDs in their correspondingVariables.
	 * Confirm before removing.
	 *
	 * @param operation (i.e. [W, T4, x4, 35])
	 */
	public void executeWriteInstruction(ArrayList<String> operation) {

	}

	/**
	 * isWriteInstructionNotAllowed is used to determine if we can write a value to a variable. 
	 * The method simply returns true if the Variable is locked by another Transaction or false 
	 * if it is not.
	 * 
	 * @param varInt
	 * @param transInt
	 * @return Returns weather the Variable is locked by another transaction or not
	 */
	public boolean isWriteInstructionNotAllowed(Integer varInt, Integer transInt) {
		// TODO: check if the the current variable is locked and if the current
		// tranaction doesn't hold the lock
		return true;
	}

	public void executeReadInstruction(ArrayList<String> operation) {
		// TODO: build this
	}

	// TODO: Identify what needs to happen during an abortion
	// TODO: If you need to revert a value back, you should use dm.getvars() because
	// its last commited and 2pl states nothing could have written to it
	// TODO: b/c its locked and vars is updated to last commmited
	public void resolveDeadLock() {
		boolean isDeadlocked = false;
		Transaction youngestTransaction = null;
		Integer youngestTransactionAge = -1;

		Iterator<String> currTrans = currentTransactions.keySet().iterator();
		while (currTrans.hasNext()) {
			if (currentTransactions.get(currTrans).getAge() > youngestTransactionAge) {
				youngestTransactionAge = currentTransactions.get(currTrans).getAge();
				youngestTransaction = currentTransactions.get(currTrans);
			}
			if (!currentTransactions.get(currTrans).isBlocked()) {
				isDeadlocked = false;
			}
		}

		if (isDeadlocked) {
			abort(youngestTransaction);
		}
	}

	// TODO : Fix Abort functionaltiy
	public void abort(Transaction youngestTransaction) {
		System.out.println("Aborted Trxn: " + youngestTransaction);
		aborted.add(youngestTransaction);
		String txnID = youngestTransaction.getTransName();
		Iterator<Integer> correspondingVarIDs = youngestTransaction.getCorrespondingVars().iterator();

		// get vars associated with youngest transaction
		while (correspondingVarIDs.hasNext()) {
			int varID = correspondingVarIDs.next();

			// use varIDs to identify sites where locks may be issued by this transaction
			Iterator<DataManager> eachDM = dmList.iterator();
			while (eachDM.hasNext()) {
				Site site = eachDM.next().getSite();
				// confirm that the variable at a site was in fact locked by this transaction
				if (site.getLT().isLockWithTransactionIDAndVarIDPresent(txnID, varID)) {
					currentTransactions.remove(txnID);
				}
			}
		}
	}

	public void dump() {
		for (int i = 1; i <= GlobalConstants.sites; i++) {
			System.out.println(i);
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

	public void dumpX(int variableID) {
		for (int i = 1; i <= GlobalConstants.sites; i++) {
			ArrayList<Variable> siteVariables = allSitesMap.get(i).getVariablesOnSite();
			for (Variable eachSiteVariable : siteVariables) {
				if (eachSiteVariable.getID() == variableID) {
					System.out.print("Site ID: " + i + "\n");
					System.out.print(eachSiteVariable.toString() + "\n");
				}
			}
		}
		System.out.print("\n");
	}

}
