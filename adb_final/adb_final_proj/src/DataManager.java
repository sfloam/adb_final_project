/**
 * 
 * @author pratikkarnik and scottfloam
 * 
 *         <h1>Data Manager</h1> The DataManager class creates 1 sites for data replication. The
 *         initialization of sites with variables is conducted in the Site class during the Site's
 *         instantiation.
 * 
 */
public class DataManager {

  /**
   * <strong>site:</strong> used to hold a site
   * 
   * <ul>
   * <li>Potential Purposes:</li>
   * <ul>
   * <li>Access a site</li>
   * </ul>
   * </ul>
   * 
   */
  private Site site;

  public DataManager(int id) {
    this.site = new Site(id);
  }

  /**
   * @return site
   */
  public Site getSite() {
    return this.site;
  }

  /**
   * <strong>replicate</strong>
   * <ul>
   * <li>Potential Purposes:</li>
   * <ul>
   * <li>Replication</li>
   * <ul>
   * <li>Go into the site and update that Variable's value field</li>
   * <li>If Site's LockTable is null (because it failed), then it skips that Site</li>
   * <li>This should not address lock checking because lock checking should be done before this
   * method is invoked in TransactionManager</li>
   * </ul>
   * </ul>
   * </ul>
   * 
   * @param varID - Variable's ID
   * @param value - Variable's value
   */
  public void replicate(Integer varID, Integer value) {
    if (this.site.getLT() != null) {
      // updateVar will also handle locks
      this.site.getDataTable().updateVar(varID, value);
    }
  }

  /**
   * 
   * @param siteID - ID of the Site to read from.
   * @param varID - ID of Variable which we are getting the value for from a Site
   * @return If the site is not null (it is available), then return the value from that site.
   *         Otherwise, return -1.
   * 
   *         NOT SURE IF THIS IS A GOOD IDEA FOR WHEN THE SITE IS DOWN... Still need to address
   *         logic on calling function for instances where this method returns -1
   */

  public Integer read(int siteID, int varID) {
    if (this.site.getLT() != null) {
      return this.site.getDataTable().getDT().get(varID).getValue();
    }

    // if it is not available in the Site's LockTable, return -1
    // TODO: Not sure if this is a good idea for when the site is not available
    return -1;
  }

  /**
   * @return Returns the list of variables in the vars list. Subject to change.
   */
  public String toString() {
    String res = "";
    res += "SiteID:" + site.getID() + " Variables: " + this.site.getLT().toString();
    return res;
    // TODO: make to string output dump for site
  }

}
