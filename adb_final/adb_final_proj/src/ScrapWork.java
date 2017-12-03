import java.util.ArrayList;

public class ScrapWork {
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
