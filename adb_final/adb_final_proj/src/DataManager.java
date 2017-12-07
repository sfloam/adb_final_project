/**
 * 
 * @author pratikkarnik and scottfloam
 * 
 *         <h1>Data Manager</h1> 
 *         <span>The DataManager class creates one Site for data replication. </span> 
 *         <span>The initialization of variables at a Site is conducted in the </span> 
 *         <span>Site class during the Site's instantiation.</span>
 * 
 */
public class DataManager {

  /**
   * <strong>site:</strong> used to hold a {@link Site}.
   */
  private Site site;

  public DataManager(int id) {
    this.site = new Site(id);
  }

  /**
   * @return the {@link Site} associated with this {@link DataManager}
   */
  public Site getSite() {
    return this.site;
  }

  /**
   * @return Returns a {@link Site} with the {@link Site} information.
   */
  public String toString() {
    String res = "";
    res += "SiteID:" + site.getID() + " " + this.site;
    return res;
  }

}
