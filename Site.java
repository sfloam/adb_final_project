public class Site(){

	public LockTable lt;
	public id;

	public Site (int id){
		this.id = id;
		LockTable lt = new LockTable (id)
	}

	public String toString(){
		return "Site_"+id;
	}
}