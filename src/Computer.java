import java.util.ArrayList;
import java.util.Scanner;
// TODO:
// - Empty process
// - Create processes opportunities to make waiting itself

public class Computer {
	private	static			ArrayList<Process>		processTable;						// Table of system processes
	private static			Scheduler				scheduler;							// Process scheduler
	private static			MemoryManagementUnit	mmu;								// Memory manager
	private static			int						systemTimer;
	private static			Process 				tempExecutedProcess;
	private static			ProcessGenerator		procGenerator;
	
	private static final	int						START_GENERATION 		= 3;
	private	static final	byte					AMOUNT_OF_ALLOWED_TICKS = 8;
	private static final	byte					SYSTEM_DATA_BOARD 		= 1;		// 1 page
	private static final	int						MAX_MEMORY_VOLUME		= 10;
	private static final	int						MAX_PROC_TIME			= 60;
	
	public static void main(String[] args) {
		processTable			= new ArrayList<Process>();
		mmu						= new MemoryManagementUnit(SYSTEM_DATA_BOARD);
		systemTimer				= 0;
		tempExecutedProcess		= null;
		procGenerator			= new ProcessGenerator(processTable, MAX_MEMORY_VOLUME, MAX_PROC_TIME);
		processTable			= procGenerator.GenerateProcessPopulation(systemTimer, START_GENERATION);
		
		scheduler				= new Scheduler();
		for (Process i : processTable)
			scheduler.AddNewProcess(i);
		
		// WORKING
		while(true) {
			tempExecutedProcess = scheduler.getTempExecutedProcess();					// Getting temp executed process from scheduler
			
			if(tempExecutedProcess == null) {											// Creating idle process
				processTable = procGenerator.GenerateProcessPopulation(systemTimer, 0);
				continue;
			}
			
			if(tempExecutedProcess.GetProcessStatus() == ProcessStatus.ISKILLING) {
				processTable.remove(tempExecutedProcess);
				mmu.KillProcess(tempExecutedProcess);
				continue;
			}
			
			if(tempExecutedProcess.GetProcessStatus() == ProcessStatus.CREATION) {		// Allocating memory
				if(mmu.AllocateMemory(tempExecutedProcess)) {
					mmu.getPhysicalAdress(tempExecutedProcess.GetMemorySegments().getLast() * 4096 + 1);	// Loading process into the memory
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
				int nextAdress = tempExecutedProcess.Execute();						// Process executing
				if ((tempExecutedProcess.GetProcessStatus() == ProcessStatus.ISKILLING) || (tempExecutedProcess.GetProcessStatus() == ProcessStatus.BLOCKING))
					break;
				else
					mmu.getPhysicalAdress(nextAdress);									// Execution imitation
			}
			
			if(tempExecutedProcess.GetProcessStatus() == ProcessStatus.EXECUTION)
				tempExecutedProcess.SetReady();
			
			systemTimer++;
			
			InfoDelay();
		}
	}
	
	private static void InfoDelay() {
		Scanner in = new Scanner(System.in);
		System.out.println("\n\n\n********************************************************************************");
		System.out.println("********************************************************************************");
		System.out.println("********************************************************************************");
		System.out.println("********************************************************************************\n");
		System.out.println("----------------------------------------");
		System.out.println("SystemTimer:\t\t" + Integer.toString(systemTimer));
		System.out.println("Temp Executed Process:\t" + Integer.toString(tempExecutedProcess.GetPID()));
		System.out.println("----------------------------------------");
		scheduler.Display();
		mmu.Display();
		DisplayTable();
		in.nextLine();
	}
	
	private static void DisplayTable() {
		System.out.println("\n\t Process Table");
		System.out.println("********************************************************************************");
		System.out.println("PID\tCrTime\tComCntr\tStat\t\tMemVol\tPrior\tMemSeg");
		System.out.println("********************************************************************************");
		for(Process i: processTable) {
			i.Display();
		}
		System.out.println("********************************************************************************");	
	}

}
