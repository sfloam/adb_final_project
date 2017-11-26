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
    public HashMap<Integer, Variable> vars;
    public ArrayList<Site> sites;

    public DataManager(){
        this.vars = new HashMap<Integer, Variable>();
        this.sites = new ArrayList<Site>();

        for (int x = 1; x < 21; x++) {
            vars.put(x, new Variable(x));
            System.out.println(vars);
        }
        
        this.sites.add(null); //leaves site0 empty will make it easier later
        for (int x = 1; x < 11; x++) {
            sites.add(new Site(x));
            System.out.println(sites.get(x).lt);
        }
    }
    
    public HashMap<Integer, Variable> getVars() {
    	return this.vars;
    }
    
    public ArrayList<Site> getSites() {
    	return this.sites;
    }

}
