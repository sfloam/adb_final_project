
import java.util.HashMap;

/**
 * 
 * @author scottfloam and pratikkarnik
 *         <h1>Lock Table</h1> The LockTable class is simply a HashMap with
 *         customized features. It holds the Variable's id and the Variable.
 * 
 */
public class LockTable extends HashMap<Integer, Variable> {
	private int id;

	LockTable(int id) {
		this.id = id;
	}

	/**
	 * getID
	 * @return the id of the LockTable (which is the same as the Site's siteID)
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * updateVar
	 * @param varID is the variable's id
	 * @param value is the value that we will set the Variable's value to
	 */
	public void updateVar(int varID, int value) {
		this.get(varID).setValue(value);
	}

}