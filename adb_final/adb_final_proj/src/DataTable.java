import java.util.HashMap;

public class DataTable {
	private HashMap<Integer, Variable> dt;

	public DataTable(int id) {
		this.dt = new HashMap<Integer, Variable>();
		// even sites get all Variables, odd sites use algorithm to assign variables
		for (int i = 1; i < 21; i++) {
			if ((i % 2) == 0) {
				(this.dt).put(i, new Variable(i));
			} 
			else {
				if ((1 + (i % 10)) == id) {
					(this.dt).put(i, new Variable(i));
				}
			}
		}
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
