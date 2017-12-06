import java.util.ArrayList;
import java.util.Set;

public class ScrapWork {
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
	
	private void RETROACTIVEWRONGendTransaction(String txnID) {
	  
	  

	    // TODO: Identify what needs to happen during an abortion
	    // TODO: If you need to revert a value back, you should use dm.getvars() because
	    // its last commited and 2pl states nothing could have written to it
	    // TODO: b/c its locked and vars is updated to last commmited
//	  public void resolveDeadLock() {
//	      boolean isDeadlocked = false;
//	      Transaction youngestTransaction = null;
//	      Integer youngestTransactionAge = -1;
	//
//	      Iterator<String> eachTransaction = blockedTransactions.keySet().iterator();
//	      while (eachTransaction.hasNext()) {
//	          String currTrans = eachTransaction.next();
//	          if (blockedTransactions.get(currTrans).getAge() > youngestTransactionAge) {
//	              youngestTransactionAge = blockedTransactions.get(currTrans).getAge();
//	              youngestTransaction = blockedTransactions.get(currTrans);
	//
//	          }
//	          if (!blockedTransactions.get(currTrans).isBlocked()) {
//	              isDeadlocked = false;
//	              System.out.println("NO DEADLOCK");
//	          }
//	      }
	//
//	      if (isDeadlocked) {
//	          System.out.println("Recovered Site !");
//	          abort(youngestTransaction);
//	      }
//	  }

//      Set<Integer> ctSet = currentTransactions.get(txnID).getCorrespondingVars();
//      Integer[] ctIntArr = ctSet.toArray(new Integer[ctSet.size()]);
//      for (int ctIndex = 0; ctIndex < ctIntArr.length; ctIndex++) {
//        int numberOfSitesAvaialbe = 0;
//        int nextVar = ctIntArr[ctIndex];
//        for (int dmIndex = 1; dmIndex < dmList.size(); dmIndex++) {
//          if (dmList.get(dmIndex).getSite().getDataTable().getDT() != null 
//              && dmList.get(dmIndex).getSite().getDataTable().getDT().containsKey(nextVar)) {
//            
//            if (dmList.get(dmIndex).getSite().isPreviouslyFailed()) {
//              if (updateRecoveredSites(nextVar)!=null) {
//                dmList.get(dmIndex).getSite().getDataTable().getDT().get(nextVar).setIntermediateValue(updateRecoveredSites(nextVar));
//              }
//              else {
//                System.out.println("FAILURE HAPPENED- NO AVAILABLE SITES WHEN TRYING TO COMMIT");
//                abort(currentTransactions.get(txnID));
//                numberOfSitesAvaialbe = 0;
//                break;
//              }
//            }
//           
//            numberOfSitesAvaialbe++;
//            
//            // update variable value in site DT
//            dmList.get(dmIndex).getSite().getDataTable().getDT().get(nextVar)
//              .setValue(dmList.get(dmIndex).getSite().getDataTable().getDT().get(nextVar).getIntermediateValue());
//            
//            // remove all txn locks
//            dmList.get(dmIndex).getSite().getLT().removeLockOnTransactionID(txnID);
//          }
//        }
//        if (numberOfSitesAvaialbe == 0) {
//          System.out.println("FAILURE HAPPENED- NO AVAILABLE SITES WHEN TRYING TO COMMIT");
//          abort(currentTransactions.get(txnID));
//          break;
//        }
//      }
//        currentTransactions.remove(txnID);
    }

	public void endTxnOldLogic(){
    // Iterator<Integer> eachVar = currentTransactions.get(txnID).getCorrespondingVars().iterator();
    // Integer nextVar;
    // while (eachVar.hasNext()) {
    // nextVar = eachVar.next();
    // int numberOfSitesAvaialbe = 0;
    // for (DataManager eachDM : dmList) {
    // if (eachDM == null) {
    // // skip the first and failed sites
    // continue;
    // } else if (eachDM.getSite().getDataTable().getDT() != null
    // && eachDM.getSite().getDataTable().getDT().containsKey(nextVar)) {
    // numberOfSitesAvaialbe++;
    // // update variable value in site DT
    // eachDM.getSite().getDataTable().getDT().get(nextVar)
    // .setValue(eachDM.getSite().getDataTable().getDT().get(nextVar).getIntermediateValue());
    // // remove all txn locks
    // eachDM.getSite().getLT().removeLockOnTransactionID(txnID);
    // }
    // }
    // if (numberOfSitesAvaialbe == 0) {
    // System.out.println("FAILURE HAPPENED- NO AVAILABLE SITES WHEN TRYING TO COMMIT");
    // abort(currentTransactions.get(txnID));
    // break;
    // }
    // currentTransactions.remove(txnID);
    // }
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

	/*
	 * public void assignTransaction(ArrayList<String> operation) { // TODO: need to
	 * address abortions if (operation.get(0).equalsIgnoreCase("begin")) { boolean
	 * isNewTransaction = true;
	 * 
	 * // traverses lists to see if transaction exists for (Transaction t : running)
	 * { if (t.getTransName().equals(operation.get(1))) { System.out.
	 * println("Transaction Exists! Multiple begins for same transaction!");
	 * isNewTransaction = false; break; } }
	 * 
	 * // checks if this is a new transaction and not an existing one if
	 * (isNewTransaction) { String transName = operation.get(1).replaceAll("T", "");
	 * Transaction tObj = new Transaction(Integer.parseInt(transName));
	 * tObj.setAge(this.age); this.age++; running.add(tObj); } }
	 * 
	 * // TODO: NOT FINISHED LOCKING else if
	 * (operation.get(0).equalsIgnoreCase("W")) { String transName =
	 * operation.get(1);
	 * 
	 * for (Transaction t : running) {
	 * 
	 * if ((t.getTransName()).equals(transName)) {
	 * 
	 * // log transaction operations in transaction may come in handy for recovery
	 * t.operations.add(operation);
	 * 
	 * // add the variable id to the transaction's correspondingVars
	 * HashSet<Integers> // to determine which variables to unlock at the end (when
	 * commits)
	 * t.addToCorrespondingVars(Integer.parseInt(operation.get(2).replaceAll("x",
	 * "")));
	 * 
	 * // perform write instructions executeWriteInstruction(operation);
	 * resolveDeadLock(); break; } } }
	 * 
	 * // TODO: NOT FINISHED LOCKING else if
	 * (operation.get(0).equalsIgnoreCase("R")) { String transName =
	 * operation.get(1);
	 * 
	 * for (Transaction t : running) { if ((t.getTransName()).equals(transName)) {
	 * t.operations.add(operation);
	 * 
	 * // TODO: Execute Instruction Operation executeReadInstruction(operation);
	 * break; } } }
	 * 
	 * else if (operation.get(0).equalsIgnoreCase("fail")) { int siteID =
	 * Integer.parseInt(operation.get(1)); try {
	 * dmList.get(siteID).getSite().fail(); } catch (Exception e) {
	 * System.out.println("Site Does Not Exist! Something went wrong with Failure!"
	 * ); }
	 * 
	 * }
	 * 
	 * // TODO: NOT COMPLETE else if (operation.get(0).equalsIgnoreCase("end")) {
	 * 
	 * // parsing transName String transName = operation.get(1).replaceAll("T", "");
	 * int transID = Integer.parseInt(transName);
	 * 
	 * // temp var HashSet<Integer> transVars = null;
	 * 
	 * // go through existing trans to find it and get its vars for (Transaction t :
	 * this.running) { if (t.getID() == transID && !t.isBlocked()) { transVars =
	 * t.getCorrespondingVars(); break; } }
	 * 
	 * // using transaction's HashSet of vars to avoid having to go through all
	 * sites if (transVars != null) { Iterator<Integer> varIDs =
	 * transVars.iterator();
	 * 
	 * // temp dataVar Variable dataVar; while (varIDs.hasNext()) {
	 * 
	 * int varID = varIDs.next(); dataVar = dm.getVars().get(varID);
	 * Iterator<Integer> siteIDs = dataVar.getSiteLocations().iterator();
	 * 
	 * // if size is zero, then no sites are avail or we are out of bounds meaning
	 * no // sites are avail while (siteIDs.hasNext()) {
	 * 
	 * int siteID = siteIDs.next();
	 * 
	 * if (dmList.get(siteID).getSite().getLT() != null) { int val =
	 * dmList.get(siteID).getSite().getLT().get(varID).getValue();
	 * 
	 * // unlock locked Variables in each site's LockTable associated with
	 * Transaction // that ended
	 * dmList.get(siteID).getSite().getLT().get(varID).setLock(false);
	 * dm.getVars().get(varID).setValue(val);
	 * 
	 * // unlock locked Variables in DataManager's vars associated with Transaction
	 * // that ended dm.getVars().get(varID).setLock(false); } } } }
	 * System.out.println(running); }
	 * 
	 * // TODO: NOT COMPLETE else if (operation.get(0).equalsIgnoreCase("dump")) {
	 * if (operation.size() == 1) { dump(); } else if (operation.size() == 2) { try
	 * { if (operation.get(1).contains(".")) { String[] varAndSite =
	 * operation.get(1).split("."); int varID = Integer.parseInt(varAndSite[0]); int
	 * siteID = Integer.parseInt(varAndSite[1]); dumpX(varID); } else { int siteID =
	 * Integer.parseInt(operation.get(1)); dumpI(siteID); } } catch (Exception e) {
	 * System.out.println("Dump Parser Failed."); } } else {
	 * System.out.println("Invalid Dump Format."); } }
	 * 
	 * // TODO: NOT COMPLETE else { System.out.println("Something wasn't covered: "
	 * + operation); } }
	 */
//	public void executeWriteInstruction(ArrayList<String> operation) {
//		// TODO: do we need read locks and write locks separated
//		Integer varID = Integer.parseInt(operation.get(2).replaceAll("x", ""));
//		Integer transID = Integer.parseInt(operation.get(1).replaceAll("T", ""));
//		Integer varValue = Integer.parseInt(operation.get(3));
//
//		if (isWriteInstructionNotAllowed(varID, transID)) {
//			// TODO: something when write is not allowed
//			for (Transaction t : running) {
//				if (t.getID() == transID) {
//					t.setBlocked(true);
//					break;
//				}
//			}
//
//		} else {
//			// TODO: SET LOCKING
//			for (int i = 1; i < 11; i++) {
//				if (dmList.get(i).getSite().hasVariable(varID)) {
//					dmList.get(i).replicate(varID, varValue);
//				}
//
//			}
//
//		}
}
