import java.util.ArrayList;
import java.util.Random;
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
	
	private	static final	byte					AMOUNT_OF_ALLOWED_TICKS = 8;
	private static final	byte					SYSTEM_DATA_BOARD 		= 1;		// 1 page
	private static final	int						MAX_MEMORY_VOLUME		= 10;
	private static final	int						MAX_PROC_TIME			= 60;
	private static final	int						PROCESS_SPAWNING_CHANCE	= 10;
	private static			Random					RAND					= new Random();
	
	public static void main(String[] args) {
		processTable			= new ArrayList<Process>();
		mmu						= new MemoryManagementUnit(SYSTEM_DATA_BOARD);
		systemTimer				= 0;
		tempExecutedProcess		= null;
		procGenerator			= new ProcessGenerator(MAX_MEMORY_VOLUME, MAX_PROC_TIME);
		processTable			= procGenerator.GenerateProcessPopulation(systemTimer, -1);
		
		scheduler				= new Scheduler();
		for (Process i : processTable) {
			i.SetSystemProcess();
			scheduler.AddNewProcess(i);
		}
		
		// WORKING
		while(true) {
			CreateProcess();															// Creating process
			tempExecutedProcess = scheduler.getTempExecutedProcess();					// Getting temp executed process from scheduler
			
			if(tempExecutedProcess.GetProcessStatus() == ProcessStatus.ISKILLING) {
				KillProcess(tempExecutedProcess);
				InfoDelay();
				continue;
			}
			
			if(tempExecutedProcess.GetProcessStatus() == ProcessStatus.CREATION) {							// Allocating memory
				if(mmu.AllocateMemory(tempExecutedProcess)) {
					mmu.getPhysicalAdress(tempExecutedProcess.GetMemorySegments().getLast() * 4096 + 1);	// Loading process into the memory
					/*
					 * Memory allocated successfully
					 * 
					 */
				}
				else {
					KillProcess(tempExecutedProcess);
					System.out.println("ERROR!!! NOT ENOUGHT MEMORY FOR: ");
					System.out.println("Name\tPID\tCrTime\tComCntr\tStat\t\tMemVol\tPrior\tMemSeg");
					System.out.println("********************************************************************************");
					System.out.print(tempExecutedProcess.GetName());
					tempExecutedProcess.Display();
					InfoDelay();
					continue;
				}
			}
			
			for (byte i = 0; i < AMOUNT_OF_ALLOWED_TICKS; i++) {
				systemTimer++;
				int nextAdress = tempExecutedProcess.Execute();							// Process executing
				InfoDelay();
				if ((tempExecutedProcess.GetProcessStatus() == ProcessStatus.ISKILLING) || (tempExecutedProcess.GetProcessStatus() == ProcessStatus.BLOCKING))
					break;
				else
					mmu.getPhysicalAdress(nextAdress);									// Execution imitation
			}
			
			if(tempExecutedProcess.GetProcessStatus() == ProcessStatus.EXECUTION)
				tempExecutedProcess.SetReady();
			
			InfoDelay();
		}
	}
	
	private static void InfoDelay() {
		Scanner in = new Scanner(System.in);
		System.out.println("********************************************************************************\n");
		scheduler.Display();
		mmu.Display();
		DisplayTable();
		System.out.println("--------------------------------------------------------------------------------");
		System.out.println("SystemTimer:\t\t" + Integer.toString(systemTimer));
		System.out.println("Temp Executed Process (PID, Name, MemSeg):\t" + Integer.toString(tempExecutedProcess.GetPID()) + "\t"  + tempExecutedProcess.GetName() + 
				"\t" + tempExecutedProcess.GetMemorySegments().toString());
		System.out.println("--------------------------------------------------------------------------------\n\n\n\n");
		in.nextLine();
	}
	
	private static void DisplayTable() {
		System.out.println("******************************Process Table************************************");
		System.out.println("PID\tCrTime\tComCntr\tStat\t\tMemVol\tPrior\tMemSeg");
		System.out.println("********************************************************************************");
		for(Process i: processTable) {
			i.Display();
		}
	}
	
	private static void CreateProcess() {
		Process processTable_BUFFER = null;
		
		if((Math.abs(RAND.nextInt()) % 100) < PROCESS_SPAWNING_CHANCE) {
			System.out.println("Creating process");
			processTable_BUFFER = procGenerator.GenerateProcessPopulation(systemTimer, 1).get(0);
			processTable.add(processTable_BUFFER);
			scheduler.AddNewProcess(processTable_BUFFER);
		}
	}
	
	private static void KillProcess(Process p) {
		System.out.println("Killing process");
		mmu.KillProcess(p);
		processTable.remove(p);
		scheduler.KillProcess(p);
	}
}
