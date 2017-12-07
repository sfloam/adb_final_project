import java.util.HashMap;

/**
 * @author scottfloam and pratikkarnik
 *
 * <h1>Data Table</h1>
 * <span> The {@link DataTable} holds {@link Variable}(s) at a {@link Site}.</span>
 * <span> All {@link Site}(s) have a {@link DataTable} that holds even {@link Variables} </span>
 * <span> (x2, x4, x6, x8, x10, x12, x14, x16, x18, x20).</span>
 * <span> Odd variables (x1, x3, x5, x7, x9, x11, x13, x15, x17, x19) are not replicated,</span>
 * <span> and are found only at a site location equivalent to ((1 + index) mod 10).</span>
 */
public class DataTable {
  private HashMap<Integer, Variable> dt;

  public DataTable(int id) {
    this.dt = new HashMap<Integer, Variable>();
    // even sites get all Variables, odd sites use algorithm to assign variables
    for (int i = 1; i < 21; i++) {
      if ((i % 2) == 0) {
        (this.dt).put(i, new Variable(i));
      } else {
        if ((1 + (i % 10)) == id) {
          (this.dt).put(i, new Variable(i));
        }
      }
    }
  }

  /**
   * <strong>updateVar:</strong> updates a {@link Variable} with value
   */
  public void updateVar(int varID, int value) {
    this.dt.get(varID).setValue(value);
  }

  /**
   * <strong>updateIntermediateValue:</strong> updates a {@link Variable}'s potential write value
   * @param varID
   * @param intermediateVal
   */
  public void updateIntermediateValue(int varID, int intermediateVal) {
    this.dt.get(varID).setIntermediateValue(intermediateVal);
  }

/**
 * <strong>getDT</strong>: gets a HashMap of {@link Variable} IDs and their values.
 * @return {@link HashMap} <{@link Integer},{@link Variable}>
 */
  public HashMap<Integer, Variable> getDT() {
    return this.dt;
  }

  public void clearDT() {
    this.dt = null;
  }
}
