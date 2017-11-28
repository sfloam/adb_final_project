import java.util.LinkedList;
import java.util.ArrayList;
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

	private LinkedList<Transaction> running;
	private LinkedList<Transaction> aborted;
	private int age;
	private DataManager dm;

	public TransactionManager(DataManager dm) {
		// May not need all of these
		this.running = new LinkedList<Transaction>();
		this.aborted = new LinkedList<Transaction>();
		this.age = 0;
		this.dm = dm;
	}

	/**
	 * 
	 * assignTransaction
	 * 
	 * @param ArrayList<String>
	 *            operation - a tick from the text file (i.e. begin(T1))
	 * 
	 *            Types of Operations
	 *            <ul>
	 *            <li>begin</li>
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
	 *              <ul>
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
	 * 
	 * 
	 *  
	 */

	// TODO: need to address abortions
	public void assignTransaction(ArrayList<String> operation) {
		if (operation.get(0).equalsIgnoreCase("begin")) {
			boolean isNewTransaction = true;

			// traverses lists to see if transaction exists
			for (Transaction t : running) {
				if (t.getTransName().equals(operation.get(1))) {
					System.out.println("Transaction Exists! Multiple begins for same transaction!");
					isNewTransaction = false;
					break;
				}
			}

			// checks if this is a new transaction and not an existing one
			if (isNewTransaction) {
				String transName = operation.get(1).replaceAll("T", "");
				Transaction tObj = new Transaction(Integer.parseInt(transName));
				tObj.setAge(this.age);
				this.age++;
				running.add(tObj);
			}
		}

		// TODO: NOT FINISHED LOCKING
		else if (operation.get(0).equalsIgnoreCase("W")) {
			String transName = operation.get(1);

			for (Transaction t : running) {
				
				if ((t.getTransName()).equals(transName)) {

					// log transaction operations in transaction may come in handy for recovery
					t.operations.add(operation);

					// add the variable id to the transaction's correspondingVars HashSet<Integers>
					// to determine which variables to unlock at the end (when commits)
					t.addToCorrespondingVars(Integer.parseInt(operation.get(2).replaceAll("x", "")));

					// perform write instructions
					executeWriteInstruction(operation);
					resolveDeadLock();
					break;
				}
			}
		}

		// TODO: NOT FINISHED LOCKING
		else if (operation.get(0).equalsIgnoreCase("R")) {
			String transName = operation.get(1);

			for (Transaction t : running) {
				if ((t.getTransName()).equals(transName)) {
					t.operations.add(operation);

					// TODO: Execute Instruction Operation
					executeReadInstruction(operation);
					break;
				}
			}
		}

		else if (operation.get(0).equalsIgnoreCase("fail")) {
			int siteID = Integer.parseInt(operation.get(1));
			try {
				dm.getSites().get(siteID).fail();
			} catch (Exception e) {
				System.out.println("Site Does Not Exist! Something went wrong with Failure!");
			}

		}

		// TODO: NOT COMPLETE
		else if (operation.get(0).equalsIgnoreCase("end")) {

			// parsing transName
			String transName = operation.get(1).replaceAll("T", "");
			int transID = Integer.parseInt(transName);

			// temp var
			HashSet<Integer> transVars = null;

			// go through existing trans to find it and get its vars
			for (Transaction t : this.running) {
				if (t.getID() == transID && !t.isBlocked()) {
					transVars = t.getCorrespondingVars();
					break;
				}
			}

			// using transaction's HashSet of vars to avoid having to go through all sites
			if (transVars != null) {
				Iterator<Integer> varIDs = transVars.iterator();

				// temp dataVar
				Variable dataVar;
				while (varIDs.hasNext()) {

					int varID = varIDs.next();
					dataVar = dm.getVars().get(varID);
					Iterator<Integer> siteIDs = dataVar.getSiteLocations().iterator();

					// if size is zero, then no sites are avail or we are out of bounds meaning no
					// sites are avail
					while (siteIDs.hasNext()) {

						int siteID = siteIDs.next();

						if (dm.getSites().get(siteID).getLT() != null) {
							int val = dm.getSites().get(siteID).getLT().get(varID).getValue();

							// unlock locked Variables in each site's LockTable associated with Transaction
							// that ended
							dm.getSites().get(siteID).getLT().get(varID).setLock(false);
							dm.getVars().get(varID).setValue(val);

							// unlock locked Variables in DataManager's vars associated with Transaction
							// that ended
							dm.getVars().get(varID).setLock(false);
						}
					}
				}
			}
			System.out.println(running);
		}

		// TODO: NOT COMPLETE
		else if (operation.get(0).equalsIgnoreCase("dump")) {

		}

		// TODO: NOT COMPLETE
		else {
			System.out.println("Something wasn't covered: " + operation);
		}
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
		// TODO: do we need read locks and write locks separated
		Integer varID = Integer.parseInt(operation.get(2).replaceAll("x", ""));
		Integer transID = Integer.parseInt(operation.get(1).replaceAll("T", ""));
		Integer varValue = Integer.parseInt(operation.get(3));

		if (isWriteInstructionNotAllowed(varID, transID)) {
			// TODO: something when write is not allowed
			for (Transaction t : running) {
				if (t.getID() == transID) {
					t.setBlocked(true);
					break;
				}
			}

		} else {
			dm.getVars().get(varID).setLock(true);
			dm.getVars().get(varID).setPreviousTransactionID(transID);
			dm.getVars().get(varID).addToCorrespondingTransaction(transID);
			dm.replicate(varID, varValue);
		}
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
		return ((dm.getVars().get(varInt).isLocked())
				&& (transInt != dm.getVars().get(varInt).getPreviousTransactionID()));
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

		for (Transaction t : running) {
			if (t.getAge() > youngestTransactionAge) {
				youngestTransactionAge = t.getAge();
				youngestTransaction = t;

			}
			if (!t.isBlocked()) {
				isDeadlocked = false;
			}
		}
		
		if (isDeadlocked) {
			abort(youngestTransaction);
		}
	}

	public void abort(Transaction youngestTransaction) {
		aborted.add(youngestTransaction);
		Iterator<Integer> correspondingVarIDs = youngestTransaction.getCorrespondingVars().iterator();
		while (correspondingVarIDs.hasNext()) {
			int varID = correspondingVarIDs.next();

			// use varIDs to identify sites where locks may be issued by this transaction
			Iterator<Integer> sites = dm.getVars().get(varID).getSiteLocations().iterator();
			while (sites.hasNext()) {
				int siteID = sites.next();

				// confirm that the variable at a site was infact locked by this transaction
				if ((dm.getSites().get(siteID).getLT().get(varID).getPreviousTransactionID() == youngestTransaction
						.getID()) && (dm.getSites().get(siteID).getLT().get(varID).isLocked())) {

					// update value of copies at each site where a var is present to stale value
					// (before commit)
					dm.getSites().get(siteID).getLT().get(varID).setValue(dm.getVars().get(varID).getValue());

					// update previous transaction to null for locking purposes
					dm.getSites().get(siteID).getLT().get(varID).setPreviousTransactionID(null);

					// update locktable locked to unlocked for that variable
					dm.getSites().get(siteID).getLT().get(varID).setLock(false);

					// remove transaction from site correspondingTransactions may need to keep
					dm.getSites().get(siteID).getLT().get(varID).getCorrespondingTransactions()
							.remove(youngestTransaction.getID());

					// update vars locked
					dm.getVars().get(siteID).setLock(false);

				}
			}
		}

		for (int i = 0; i < running.size(); i++) {
			if (running.get(i).getID() == youngestTransaction.getID()) {
				running.remove(i);
			}
		}

	}

}
