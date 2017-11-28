import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author pratikkarnik and scottfloam
 * 
 *         <h1>Data Manager</h1> 
 *         The DataManager class creates 10 sites for data replication. It also generates 20 variables replicates those
 *         variables across individual or multiple sites depending on the variable id. Variables with even variable ids 
 *         are replicated across all sites. Variables with odd variable ids are replicated across sites with the 
 *         site id (1 + (varID % 10)). The initialization of sites with variables is conducted in the Site class 
 *         during the Site's instantiation.
 * 
 */
public class DataManager {

	/**
	 * vars is used to hold a key value pair between varIDs and Variables Potential
	 * Purposes (may not be needed):
	 * <ul>
	 * 		<li>Replication</li>
	 * 		<ul>
	 * 				<li>It is used to reference which sites are associated with a particular variable.</li>
	 * 				<li>By iterating over a Variable's site locations, one can quickly identify which sites to visit to update a copy of data</li>
	 * 		</ul>
	 * 		<li>MultiVersion Read Consistency</li>
	 * 		<ul>
	 * 			<li>During replicate, we do not update the value of a Variable in vars.</li>
	 * 			<li>We do update the value of a Variable in vars when the transaction ends 
	 * 				and has not been aborted (signaling that it has committed)</li>
	 * 			<li>This also allows us to revert back changes when conflicts occur. 
	 * 				Suppose T1 gets x1, then t2 gets x2 and then T1 needs x2 and T2 
	 * 				needs x1, we could abort T2, and use vars to revert back the 
	 * 				value of T1's x2 to its original value using vars since T2 never 
	 * 				commits.</li>
	 * 		</ul>
	 * </ul>
	 * 
	 * 
	 */
	private HashMap<Integer, Variable> vars;

	/**
	 * <strong>sites:</strong> used to hold a list of all sites 
	 * 
	 * <ul>
	 * <li>Potential Purposes:</li>
	 * 		<ul>
	 * 		<li>Access a site</li>
	 * 		</ul>
	 * </ul>
	 * 
	 */
	private ArrayList<Site> sites;

	public DataManager() {
		this.vars = new HashMap<Integer, Variable>();
		this.sites = new ArrayList<Site>();

		// instantiate 20 new variables into vars
		for (int x = 1; x < 21; x++) {
			vars.put(x, new Variable(x));
		}

		// leaves site[0] empty will make it easier to reference failures
		this.sites.add(null);
		for (int x = 1; x < 11; x++) {
			sites.add(new Site(x));
		}
	}

	/**
	 * @return HashMap<Integer, Variable> vars
	 */
	public HashMap<Integer, Variable> getVars() {
		return this.vars;
	}

	/**
	 * @return ArrayList<Site> sites
	 */
	public ArrayList<Site> getSites() {
		return this.sites;
	}

	/**
	 * <strong>replicate</strong>
	 * <ul>
	 * 		<li> Potential Purposes:</li>
	 * 		<ul>
	 * 			<li>Replication</li>
	 * 			<ul>
	 * 				<li>Go into every site (associated with a Variable's sites) and update that Variable's value field</li>
	 * 				<li>If Site's LockTable is null (because it failed), then it skips that Site</li>
	 * 				<li>This should not address lock checking because lock checking should be done before this method is invoked in TransactionManager</li>
	 * 				<li>We do not update DataManager's vars because replication does not necessarily mean a commit took place</li>
	 *			</ul>
	 *		</ul>
	 * </ul>
	 * 
	 * @param varID - Variable's ID
	 * @param value - Variable's value
	 */
	public void replicate(int varID, int value) {
		for (Integer siteID : this.vars.get(varID).getSiteLocations()) {
			if (this.sites.get(siteID).getLT() != null) {
				this.sites.get(siteID).getLT().updateVar(varID, value);
			}
		}
	}

	/**
	 * 
	 * @param siteID - ID of the Site to read from.
	 * @param varID - ID of Variable which we are getting the value for from a Site
	 * @return If the site is not null (it is available), then return the value from that site. Otherwise, return the value provided in the vars (which is stale data)
	 * 
	 * NOT SURE IF THIS IS A GOOD IDEA FOR WHEN THE SITE IS DOWN... NOTE THIS RETURNS INTEGER
	 */
	
	public Integer read(int siteID, int varID) {
		if (this.sites.get(siteID).getLT() != null) {
			return this.sites.get(siteID).getLT().get(varID).getValue();
		}

		// if it is not available in the Site's LockTable, we can return stale data from DataManager's vars
		// TODO: Not sure if this is a good idea for when the site is not avaiable, it should really just check another site. Need to fix!
		return this.vars.get(varID).getValue();
	}

	/**
	 * @return Returns the list of variables in the vars list. Subject to change.
	 */
	public String toString() {
		return " vars:" + vars;
	}

}
