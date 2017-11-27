import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;

public class TransactionManager {

    private LinkedList<Transaction> running;
    private int age;
    private DataManager dm;


    //SITES IS starting from 1 NOT zero
    public TransactionManager(DataManager dm) {
        //May not need all of these
        this.running = new LinkedList<Transaction>();
        this.age = 0;
        this.dm = dm;
    }

    public void assignTransaction(ArrayList<String> transaction) {

        if (transaction.get(0).equalsIgnoreCase("begin")) {
            boolean isNewTransaction = true;

            //Traverses lists to see if transaction exists
            for (Transaction t : running) {
                if (t.getTransName().equals(transaction.get(1))) {
                    System.out.println("Transaction Exists! Multiple begins for same transaction!");
                    isNewTransaction = false;
                    break;
                }
            }

            //Checks if this is a new transaction and not an existing one
            if (isNewTransaction) {
                String transName = transaction.get(1).replaceAll("T", "");
                Transaction tObj = new Transaction(Integer.parseInt(transName));
                tObj.setAge(this.age);
                this.age++;
                running.add(tObj);
            }
        }

        //not complete
        else if (transaction.get(0).equalsIgnoreCase("W")) {
            int index;
            String transName = transaction.get(1);
            boolean alreadyFound = false;

            for (Transaction t : running) {
                if ((t.getTransName()).equals(transName)) {
                    t.operations.add(transaction);
                    //TODO: Execute INstruction Operation
                    executeWriteInstruction(transaction);
                    break;
                }
            }
        }

        //not complete
        else if (transaction.get(0).equalsIgnoreCase("R")) {
            int index;
            String transName = transaction.get(1);
            boolean alreadyFound = false;

            for (Transaction t : running) {
                if ((t.getTransName()).equals(transName)) {
                    t.operations.add(transaction);
                    //TODO: Execute INstruction Operation
                    executeReadInstruction(transaction);
                    break;
                }
            }
        }

        //may need to address missing sites and null pointers later on
        else if (transaction.get(0).equalsIgnoreCase("fail")) {
            int siteID = Integer.parseInt(transaction.get(1));
            dm.getSites().get(siteID).fail();
        }

        //not complete
        else if (transaction.get(0).equalsIgnoreCase("end")) {

        }

        //not complete
        else if (transaction.get(0).equalsIgnoreCase("dump")) {

        }

        //not complete
        else {

            System.out.println("Something wasn't covered: " + transaction);
        }
    }

    public void executeWriteInstruction(ArrayList<String> operation) {
        Integer varInt = Integer.parseInt(operation.get(2).replaceAll("x", ""));
        Integer transInt = Integer.parseInt(operation.get(1).replaceAll("T", ""));
        lockVariable(varInt, transInt); // variable name, transactionID
        updateLockTables(varInt);
    }

    public void lockVariable(Integer varInt, Integer transInt) {

        if ((dm.getVars().get(varInt).isLocked()) && (transInt != dm.getVars().get(varInt).getPreviousTransactionID())) {
            System.out.println("LOCKED");
        } else {
            dm.getVars().get(varInt).setLock();
            dm.getVars().get(varInt).setPreviousTransactionID(transInt);
            dm.getVars().get(varInt).addTransaction(transInt);
            System.out.println("x" + varInt + " is not locked yet");
        }
    }

    public void updateLockTables(Integer varInt) {
    }

    public void executeReadInstruction(ArrayList<String> operation) {

    }
    


}
