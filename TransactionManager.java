import java.util.PriorityQueue;
public class TransactionManager {

	public PriorityQueue<Transaction> running;
	public PriorityQueue<Transaction> ready;
	public PriorityQueue<Transaction> blocked;

	public TransactionManager(){
		this.running = new PriorityQueue<Transaction>();
		this.ready = new PriorityQueue<Transaction>();
		this.blocked = new PriorityQueue<Transaction>();
	}

}