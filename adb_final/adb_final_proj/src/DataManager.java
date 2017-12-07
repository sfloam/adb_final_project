/**
 * @author pratikkarnik and scottfloam
 *         <h1>Data Manager</h1> 
 *         <span>The DataManager class creates a Site for data replication.</span>
 *         <span>The initialization of sites with variables is conducted in</span>
 *         <span>the Site class during the Site's instantiation.</span>       
 */
public class DataManager {

  
  private Site site;

  public DataManager(int id) {
    this.site = new Site(id);
  }
  /**
   * <strong>getSite:</strong>
   * <span>Get the site associated with this DataManger</span>
   * 
   */
  public Site getSite() {
    return this.site;
  }

  /**
   * @return String with the Site ID and the Site's toString().
   */
  public String toString() {
    String res = "";
    res += "SiteID:" + site.getID() + " " + this.site;
    return res;
  }

}
