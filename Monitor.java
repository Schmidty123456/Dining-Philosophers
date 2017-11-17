import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Creates a monitor to ensure threads can't access shared
 * space without key
 *
 * @author Logan Schmidt
 * @version 4/28/17
 *
 */
public class Monitor {

	/**Lock to make sure data is secure*/
	ReentrantLock lock;
	/**The state of each philosopher*/
	State[] states;
	/**The conditions for each chopstick*/
	Condition[] conditions;


	/**
	 * Creates a monitor and establishes all condition
	 * variables for each philosopher
	 */
	public Monitor(){
		states = new State[Philosopher.DINERS];
		conditions = new Condition[Philosopher.DINERS];
		lock = new ReentrantLock();
		for(int i = 0; i < Philosopher.DINERS; i++){
			states[i] = State.HUNGRY;
			conditions[i] = lock.newCondition();
		}
	}

	/**
	 * Takes the chopsticks if they are ther if not
	 * wait
	 * @param id the philosopher id
	 * @throws InterruptedException
	 */
	public void takeChopsticks(int id) throws InterruptedException{

		try{
			lock.lock();
			states[id] = State.HUNGRY;
			if(states[(id+1)%Philosopher.DINERS] == State.EATING){
				conditions[(id+1)%Philosopher.DINERS].await();
			}
			if(states[(id+4)%Philosopher.DINERS] == State.EATING){
				conditions[(id+4)%Philosopher.DINERS].await();
			}
			states[id] = State.EATING;
		}finally {
			lock.unlock();
		}

	}

	/**
	 * Replaces chopsticks if philosopher can
	 * @param id the id of philosopher
	 * @throws InterruptedException
	 */
	public void replaceChopsticks(int id) throws InterruptedException{
		try{
			lock.lock();
			states[id] = State.THINKING;
			conditions[(id)%Philosopher.DINERS].signalAll();
			conditions[(id+1)%Philosopher.DINERS].signalAll();
		}finally {
			lock.unlock();
		}
	}
}
