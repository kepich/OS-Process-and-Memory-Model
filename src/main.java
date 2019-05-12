import java.util.ArrayList;
import java.util.Scanner;
// TODO:
// - Empty process
// - Create processes opportunities to make waiting itself

public class main {
	private	static			ArrayList<Process>		processTable;						// Table of system processes
	private static			Scheduler				scheduler;							// Process scheduler
	private static			MemoryManagementUnit	mmu;								// Memory manager
	private static			int						systemTimer;
	private static			Process 				tempExecutedProcess;
	private static			ProcessGenerator		procGenerator;
	
	private static final	int						START_GENERATION 		= 10;
	private	static final	byte					AMOUNT_OF_ALLOWED_TICKS = 8;
	private static final	byte					SYSTEM_DATA_BOARD 		= 1;		// 1 page
	private static final	int						MAX_MEMORY_VOLUME		= 10;
	private static final	int						MAX_PROC_TIME			= 60;
	
	public static void Main(String[] args) {
		processTable			= new ArrayList<Process>();
		mmu						= new MemoryManagementUnit(SYSTEM_DATA_BOARD);
		systemTimer				= 0;
		tempExecutedProcess		= null;
		procGenerator			= new ProcessGenerator(processTable, MAX_MEMORY_VOLUME, MAX_PROC_TIME);
		processTable			= procGenerator.GenerateProcessPopulation(systemTimer, START_GENERATION);
		
		scheduler				= new Scheduler();
		for (Process i : processTable)
			scheduler.AddNewProcess(i);
		
		Scanner in = new Scanner(System.in);
		
		
		// WORKING
		while(true) {
			tempExecutedProcess = scheduler.getTempExecutedProcess();					// Getting temp executed process from scheduler
			
			if(tempExecutedProcess.GetProcessStatus() == ProcessStatus.KILLING) {
				processTable.remove(tempExecutedProcess);
				mmu.KillProcess(tempExecutedProcess);
				continue;
			}
			
			if(tempExecutedProcess.GetProcessStatus() == ProcessStatus.CREATION) {		// Allocating memory
				if(mmu.AllocateMemory(tempExecutedProcess)) {
					/*
					 * Memory allocated successfully
					 * 
					 */
				}
				else {
					/*
					 * Not enough memory to initialise this process
					 * 
					 */
				}
			}
			
			for (byte i = 0; i < AMOUNT_OF_ALLOWED_TICKS; i++) {
				systemTimer++;
				Byte nextAdress = tempExecutedProcess.Execute();						// Process executing
				if ((tempExecutedProcess.GetProcessStatus() == ProcessStatus.KILLING) || (tempExecutedProcess.GetProcessStatus() == ProcessStatus.WAITING))
					break;
				else
					mmu.getPhysicalAdress(nextAdress);									// Execution imitation
			}
			
			if(tempExecutedProcess.GetProcessStatus() == ProcessStatus.EXECUTION)
				tempExecutedProcess.SetReady();
			
			systemTimer++;
			
			System.out.println("SystemTimer = " + Integer.toString(systemTimer));
			System.out.println("Temp Executed Process: " + Integer.toString(tempExecutedProcess.GetPID()));
			scheduler.Display();
			mmu.Display();
			/*
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
			in.nextLine();
		}
	}

}
