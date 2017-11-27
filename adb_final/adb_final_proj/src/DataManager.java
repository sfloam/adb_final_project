import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by scottfloam on 11/26/17.
 */

/**
 * Data Manager creates 10 sites and 20 variables.
 * 
 * @author pratikkarnik
 *
 */
public class DataManager {
	private HashMap<Integer, Variable> vars;
	private ArrayList<Site> sites;

	public DataManager() {
		this.vars = new HashMap<Integer, Variable>();
		this.sites = new ArrayList<Site>();

		for (int x = 1; x < 21; x++) {
			vars.put(x, new Variable(x));
		}

		this.sites.add(null); // leaves site0 empty will make it easier later
		for (int x = 1; x < 11; x++) {
			sites.add(new Site(x));
		}
	}

	public HashMap<Integer, Variable> getVars() {
		return this.vars;
	}

	public ArrayList<Site> getSites() {
		return this.sites;
	}

	// go into every site (associated with variable's sites) and update the variable
	// may need to address scenario if the site is failed
	// should not address lock checking because that should be done before this
	// method is invoked in TM
	public void replicate(int varID, int value) {
		for (Integer siteID : this.vars.get(varID).getSiteLocations()) {
			this.sites.get(siteID).getLT().put(varID, value);
		}
	}

	// TODO: maybe check if the site is available
	public Integer read(int siteID, int varID) {
		return this.sites.get(siteID).getLT().get(varID);
	}

}
