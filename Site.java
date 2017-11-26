import java.util.HashMap;

public class Site {
	public int id;
	public LockTable lt;

	public Site (int id){
		this.id = id;
		this.lt = new LockTable(id);
		
		for (int i = 1; i < 21; i++){
			if (i%2 == 0){
				(this.lt).put(i, i*10);
			}
			
			else if ((1+(i%10)) == this.id){
				(this.lt).put(i, i*10);
			}
		}

	}

	public String toString(){
		return "Site_"+id;
	}

	public void fail(){
		this.lt = null;
	}

}