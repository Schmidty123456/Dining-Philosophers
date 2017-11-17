import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Models a philosopher that is able to eat and think
 *
 * @author Logan Schmidt
 * @version 4/28/17
 *
 */
public class Philosopher implements PhilosopherInterface, Runnable{

	Monitor monitor;
	int id;
	String fd;

	/**
	 * Creates a philosopher with a unique id
	 * @param id unique id
	 * @param monitor monitors actions into shared memory
	 * @param fd if file is being displayed or logged
	 */
	Philosopher(int id, Monitor monitor,  String fd){
		this.id = id;
		this.monitor = monitor;
		this.fd = fd;
	}

	/* (non-Javadoc)
	 * @see PhilosopherInterface#takeChopsticks()
	 */
	@Override
	public synchronized void takeChopsticks() throws InterruptedException {
		monitor.takeChopsticks(id);
		eat();
		replaceChopsticks();
		think();
	}

	/* (non-Javadoc)
	 * @see PhilosopherInterface#replaceChopsticks()
	 */
	@Override
	public synchronized void replaceChopsticks() throws InterruptedException {
		monitor.replaceChopsticks(id);

	}

	/**
	 * Writes to display or log and waits
	 * @throws InterruptedException
	 */
	private void think() throws InterruptedException{
		write(fd, "Philosopher "+ id +" is thinking\n");
		wait(ThreadLocalRandom.current().nextLong(5000));
	}

	/**
	 * Writes to display or logs and waits
	 * @throws InterruptedException
	 */
	private void eat() throws InterruptedException{
		write(fd,"Philosopher "+ id +" is eating\n");
		wait(ThreadLocalRandom.current().nextLong(5000));
	}



	/**
	 * Starts the dining philosophers
	 * @param args arguments being passed in such as time and logging
	 */
	public static void main(String[] args){

		int time;
        if(!(args.length == 2)){
            System.out.println("java Philosophers <time> <write to file>");
            System.exit(0);
        }
		try{
			time = Integer.parseInt(args[0]);
		}catch(NumberFormatException e){
			time = 5;
		}
		Philosopher[] philosophers = new Philosopher[Philosopher.DINERS];
		Thread phil[] = new Thread[Philosopher.DINERS];
		ThreadPoolExecutor executor = (ThreadPoolExecutor)
                Executors.newFixedThreadPool(Philosopher.DINERS);
		Monitor monitor = new Monitor();
		for(int i = 0; i < Philosopher.DINERS; i++){
			phil[i] = new Thread(new Philosopher(i, monitor, args[1]));
			executor.submit(phil[i]);
		}
		
		Timer timer = new Timer();
		timer.schedule( new TimerTask() {

			@Override
			public void run() {
				for(int i = 0; i < Philosopher.DINERS; i++){
					phil[i] = null;
					executor.shutdownNow();
					while(!executor.isShutdown()){}
					System.exit(0);
				}
			}
		}, time*1000);

	}

	/**
	 * Writes to log or display
	 * @param fd if file is displaying or logging
	 * @param output the data being written
	 */
	public static synchronized void write(String fd, String output){
		FileWriter fw = null;
		BufferedWriter bw = null;
		if(fd.equals("F"))
			System.out.println(output);
		else{
			try {
				fw = new FileWriter("log.txt", true);
				bw = new BufferedWriter(fw);
				bw.write(output);
				bw.close();
				fw.close();
			} catch (IOException e) {
				System.out.println("File can not be accessed");
			}finally{
				try {
					bw.close();
					fw.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				if(id == 0)
					Thread.currentThread().sleep(1000);
				takeChopsticks();
			}
		}catch(InterruptedException e){
			//Thread.currentThread().interrupt();
			try {
				replaceChopsticks();
                think();
			} catch (InterruptedException e1) {
			}
		}
	}
}
