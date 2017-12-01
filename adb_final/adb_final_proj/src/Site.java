import java.util.ArrayList;

/**
 * 
 * @author scottfloam and pratikkarnik
 * <h1>Site</h1> The Site class creates a Site and instantiates a lock
 *         table for that site. Of the 20 existing Variables, Variables 
 *         with even variable IDs are replicated across all sites. Variables 
 *         with odd variable IDs are replicated across sites with the site id 
 *         (1 + (varID % 10)). The initialization of sites with variables is 
 *         conducted in the Site class during the Site's instantiation. A list of
 *         Sites is provided in the DataManager's ArrayList<Site> sites.
 */
public class Site {
	private int id;
	private boolean isSiteUp;
	private ArrayList<Variable> variablesOnSite;
	private LockTable lt;
	
	public Site(int id) {
		this.id = id;
		this.lt = new LockTable(id);
		
		//even sites get all Variables, odd sites use algorithm to assign variables
		for (int i = 1; i < 21; i++) {
			if (i % 2 == 0) {
				(this.lt).put(i, new Variable(i));
			} else if ((1 + (i % 10)) == this.id) {
				(this.lt).put(i, new Variable(i));
			}
		}

	}

	/**
	 * toString
	 * @return site's id
	 */
	@Override
	public String toString() {
		StringBuilder siteInformation = new StringBuilder();
		siteInformation.append("Site ID: "+this.id);
		for(int i = 0; i < variablesOnSite.size(); i++) {
			String variableInformation = variablesOnSite.get(i).getID() + " = " + variablesOnSite.get(i).getValue();
			siteInformation.append(" - "+variableInformation+"\n");
		}
		return siteInformation.toString();
	}

	/**
	 * fail
	 * Sets LockTable (lt) to null
	 * @return null
	 */
	public void fail() {
		this.lt = null;
	}

	/**
	 * getLT
	 * @returns the LockTable associated with the Site
	 */
	public LockTable getLT() {
		return this.lt;
	}
	
	/**
	 * getID
	 * @return the numeric id value of this Site
	 */
	public int getID() {
		return this.id;
	}

	public ArrayList<Variable> getVariablesOnSite() {
		return this.variablesOnSite;
	}
}