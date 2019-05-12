import java.util.ArrayList;
import java.util.Random;
// TODO:
// - Empty process
// - Create processes opportunities to make waiting itself

public class main {
	private	static			ArrayList<Process>		processTable;			// Table of system processes
	private static			Scheduler				scheduler;				// Process scheduler
	private static			MemoryManagementUnit	mmu;					// Memory manager
	private static			int						systemTimer;
	private static			Process 				tempExecutedProcess;
	
	private static final	int						START_GENERATION = 10;
	private	static final	byte					AMOUNT_OF_ALLOWED_TICKS = 8;
	private static final	byte					SYSTEM_DATA_BOARD = 1;	// 1 page
	
	public static void Main(String[] args) {
		processTable			= new ArrayList<Process>();
		scheduler				= new Scheduler();
		mmu						= new MemoryManagementUnit(SYSTEM_DATA_BOARD);
		systemTimer				= 0;
		tempExecutedProcess		= null;
		/*
		 * Create random generation of processes
		 */
		
		// WORKING
		while(true) {
			tempExecutedProcess = scheduler.getTempExecutedProcess();	// Getting temp executed process from scheduler
			
			if(tempExecutedProcess.GetProcessStatus() == ProcessStatus.READINESS)
				tempExecutedProcess.SetExecuting();
			
			for (byte i = 0; i < AMOUNT_OF_ALLOWED_TICKS; i++) {
				systemTimer++;
				
				if (tempExecutedProcess.Execute() != ProcessStatus.EXECUTION)
					break;
			}
			
			if(tempExecutedProcess.GetProcessStatus() == ProcessStatus.EXECUTION)
				tempExecutedProcess.SetReady();
			
			systemTimer++;
			
			System.out.println("SystemTimer = " + Integer.toString(systemTimer));
			System.out.println("Temp Executed Process: " + Integer.toString(tempExecutedProcess.GetPID()));
			scheduler.Display();
			mmu.Display();
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
