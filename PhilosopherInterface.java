
/**
 * Interface for the philosophers
 *
 * @author Logan Schmidt
 * @version 4/28/17
 */
public interface PhilosopherInterface {

	/**Amount of philosophers*/
	public static final int DINERS = 5;

	/**
	 * Takes chopsticks if philosopher can
	 * @throws InterruptedException
	 */
	public void takeChopsticks() throws InterruptedException;

	/**
	 * Replaces chopsticks and notifies all other philosophers
	 * @throws InterruptedException
	 */
	public void replaceChopsticks() throws InterruptedException;
}
