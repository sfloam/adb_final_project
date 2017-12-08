import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author scottfloam and pratikkarnik
 * <h1>Variable</h1>
 * <span> The {@link Variable} class contains information about the data being stored across the sites. </span>
 * <span> Each {@link Variable} has an ID, value, intermediate value (used to hold information before it commits),</span>
 * <span> whether it is exclusive to the {@link Site}, correspondingTransactions ({@link Transactions} that use this variable),</span>
 * <span> and other critical information about the data used in this system.</span>
 */
public class Variable {
  private int id;
  private int value;
  private int intermediateValue;
  private boolean isFreeToRead;
  private boolean isExclusiveToSite;
  private String intermediateValueSetBy;

  // whether it is locked by a transaction
  private boolean lock;

  // last transaction to interact with it
  private int previousTransactionID;

  // All transactions that have interacted with it
  private HashSet<Integer> correspondingTransactions;

  // site locations where transaction can be found
  private ArrayList<Integer> siteLocations;

  public Variable(int id) {
    this.id = id;
    this.value = id * 10;
    this.isFreeToRead = true;
    this.intermediateValue = this.value;
    // does a transaction have a this variable
    this.lock = false;

    // which transactions use this variable
    correspondingTransactions = new HashSet<Integer>();

    // use to read next site for variable when first fails
    siteLocations = new ArrayList<Integer>();

    if ((id % 2) == 0) {
      for (int i = 1; i < 11; i++) {
        siteLocations.add(i);
        this.isExclusiveToSite = false;
      }
    } else {
      siteLocations.add((1 + (id % 10)));
      this.isExclusiveToSite = true;
    }
  }

  /**
   * <strong>toString</strong>: gets information about the {@link Variable}
   * @return information about the {@link Variable}
   */
  public String toString() {
    return "[ID: " + id + " Value: " + value + " isLocked: " + lock + " corTrans: "
        + correspondingTransactions + " siteLocs: " + siteLocations + "]";
  }

  /**
   * <strong>getID</strong>: gets the id of the {@link Variable}
   * @return the id of the {@link Variable}
   */
  public int getID() {
    return this.id;
  }

  /**
   * <strong>getValue</strong>: gets the value of the {@link Variable}
   * @return the value of the {@link Variable}
   */
  public int getValue() {
    return this.value;
  }

  /**
   * <strong>setValue</strong>: sets the value of the {@link Variable}
   * @param value - the value of the {@link Variable}
   */
  public void setValue(int value) {
    this.value = value;
  }

  /**
   * <strong>isLocked</strong>: determines if the {@link Variable} is locked by a {@link Transaction}
   * @return true if the {@link Variable} is locked by a {@link Transaction}, otherwise false
   */
  public boolean isLocked() {
    return lock;
  }

  /**
   * <strong>setLock</strong>: sets the {@link Variable}'s state to locked if a {@link Transaction} acquires a lock on the {@link Variable}
   * @param boolean value to set the {@link Variable}'s state to locked or unlocked
   */
  public void setLock(boolean lock) {
    this.lock = lock;
  }

  /**
   * <strong>getPreviousTransactionID</strong>: gets the {@link Transaction} ID of the last {@link Transaction} that interacted with it
   * @return the {@link Transaction} ID of the last {@link Transaction} that interacted with it
   */
  public int getPreviousTransactionID() {
    return this.previousTransactionID;
  }

  /**
   * <strong>getPreviousTransactionID</strong>: gets the {@link Transaction} ID of the last {@link Transaction} that locked this {@link Variable}
   * @param the ID of the last {@link Transaction} that locked this {@link Variable}
   */
  public void setPreviousTransactionID(Integer previousTransactionID) {
    this.previousTransactionID = previousTransactionID;
  }

  /**
   * <strong>getCorrespondingTransactions</strong>: gets a HashSet of {@link Transaction} IDs that the {@link Variable} made contact with
   * @return a HashSet of {@link Transaction} IDs that the {@link Variable} made contact with
   */
  public HashSet<Integer> getCorrespondingTransactions() {
    return this.correspondingTransactions;
  }

  /**
   * <strong>addToCorrespondingTransaction</strong>: adds the ID of a {@link Transaction} that the {@link Variable} made contact with to the correspondingTransaction set
   * @param transactionID - the ID of a {@link Transaction} that the {@link Variable} made contact with
   */
  public void addToCorrespondingTransaction(int transactionID) {
    correspondingTransactions.add(transactionID);
  }

  /**
   * <strong>getSiteLocations</strong>: gets a list of the IDs of the {@link Site}(s) where the {@link Variable} is located at
   * @return siteLocations - a list of the IDs of the {@link Site}(s) where the {@link Variable} is located at
   */
  public ArrayList<Integer> getSiteLocations() {
    return this.siteLocations;
  }

  /**
   * <strong>getIntermediateValue</strong>: gets the value that will potentially be written to a {@link Variable} before a {@link Transaction} commits
   * @return intermediateValue - the value that will potentially be written to a {@link Variable} before a {@link Transaction} commits
   */
  public int getIntermediateValue() {
    return intermediateValue;
  }

  /**
   * <strong>setIntermediateValue</strong>: sets the value that will potentially be written to a {@link Variable} before a {@link Transaction} commits
   * @param intermediateValue - the value that will potentially be written to a {@link Variable} before a {@link Transaction} commits
   */
  public void setIntermediateValue(int intermediateValue) {
    this.intermediateValue = intermediateValue;
  }

  /**
   * <strong>isFreeToRead</strong>: determines if a {@link Variable} is available for reading (used when a {@link Site} fails and {@link recovers})
   * @return isFreeToRead - true if a {@link Variable} is available for reading, otherwise false
   */
  public boolean isFreeToRead() {
    return isFreeToRead;
  }

  /**
   * <strong>setFreeToRead</strong>: sets a {@link Variable} as available for reading (used when a {@link Site} fails and {@link recovers})
   * @param isFreeToRead - true if a {@link Variable} is available for reading, otherwise false
   */
  public void setFreeToRead(boolean isFreeToRead) {
    this.isFreeToRead = isFreeToRead;
  }

  /**
   * <strong>isExclusiveToSite</strong>: determines if a {@link Variable} as isExclusiveToSite (used when a {@link Site} fails and {@link recovers})
   * @return true if a {@link Variable} as isExclusiveToSite, otherwise false
   */
  public boolean isExclusiveToSite() {
    return isExclusiveToSite;
  }

  /**
   * <strong>setExclusiveToSite</strong>: sets a {@link Variable} as isExclusiveToSite (used when a {@link Site} fails and {@link recovers})
   * @param isExclusiveToSite true if a {@link Variable} as isExclusiveToSite, otherwise false
   */
  public void setExclusiveToSite(boolean isExclusiveToSite) {
    this.isExclusiveToSite = isExclusiveToSite;
  }

  /**
   * <strong>getIntermediateValueSetBy</strong>: gets the {@link Transaction} ID of the {@link Transaction} that sets the intermediateValue of the {@link Variable}
   * @return the {@link Transaction} ID of the {@link Transaction} that sets the intermediateValue of the {@link Variable}
   */
  public String getIntermediateValueSetBy() {
    return intermediateValueSetBy;
  }

  /**
   * <strong>setIntermediateValueSetBy</strong>: sets the {@link Transaction} ID of the {@link Transaction} that sets the intermediateValue of the {@link Variable}
   * @param intermediateValueSetBy - the {@link Transaction} ID of the {@link Transaction} that sets the intermediateValue of the {@link Variable}
   */
  public void setIntermediateValueSetBy(String intermediateValueSetBy) {
    this.intermediateValueSetBy = intermediateValueSetBy;
  }

}
