import java.util.HashMap;

public class DataTable {
	private HashMap<Integer, Variable> dt;

	public DataTable() {
		this.dt = new HashMap<Integer, Variable>();
	}

	public void updateVar(int varID, int value) {
		this.dt.get(varID).setValue(value);
	}

	public HashMap<Integer, Variable> getDT() {
		return this.dt;
	}
	public void clearDT() {
		this.dt = null;
	}
}
