import java.util.HashMap;

public class Site{

	public LockTable lt;
	public int id;

	public Site (int id){
		this.id = id;
		lt = new LockTable(id);
		
		for (int i = 1; i < 21; i++){
			if (i%2 == 0){
				(this.lt).put(i, i*10);
			}
			
			else {
				if ((1+(i%10)) == this.id){
					(this.lt).put(i, i*10);
				}
			}
		}

	}

	public String toString(){
		return "Site_"+id;
	}

}