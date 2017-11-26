import java.util.HashSet;
import java.util.ArrayList;

public class Variable {
    public int id;
    public int value;
    public boolean isLocked;
    public int previousTransactionID;
    public HashSet<Integer> correspondingTransactions;
    public ArrayList<Integer> siteLocations;

    public Variable(int id) {
        this.id = id;
        this.value = id * 10;
        this.isLocked = false;
        correspondingTransactions = new HashSet<Integer>();
        siteLocations = new ArrayList<Integer>();

        if (id % 2 == 0) {
            for (int i = 1; i < 11; i++) {
                siteLocations.add(i);
            }
        } else {
            siteLocations.add((1 + (id % 10)));
        }
    }

    public void addTransaction(int transactionID) {
        correspondingTransactions.add(transactionID);
    }

    public String toString() {
        return "[ID: " + id
                + " Value: " + value
                + " isLocked: " + isLocked
                + " corTrans: " + correspondingTransactions
                + " siteLocs: " + siteLocations + "]";
    }

}