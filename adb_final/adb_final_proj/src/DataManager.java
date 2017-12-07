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
   * @return Returns the list of variables in the vars list. Subject to change.
   */
  public String toString() {
    String res = "";
    res += "SiteID:" + site.getID() + " " + this.site;
    return res;
    // TODO: make to string output dump for site
  }

}
