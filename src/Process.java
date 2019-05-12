import java.util.ArrayDeque;
import java.util.Random;

public class Process {
	private 		byte 				PID;				// Process identificator	//	***
	private			int					CommandCounter;									// 	UPDATE: EXEC
	private			ProcessStatus		ProcessStatus;									//	UPDATE:	TASK_MANAGER
	private			ArrayDeque<Byte>	MemorySegments;									//	UPDATE:	MEM_MANAGER
	private			int					MemoryVolume;									//	***
	private			byte				Priority;			// Range: 0:31				//	UPDATE:	TASK_MANAGER / ***
	private			int					Registers;										//	UPDATE:	EXEC
	private			int					ProceesorTime;									//	UPDATE: EXEC
	private			byte				NextNeededAdress;	// Adress exec. command		//	UPDATE: EXEC
	
	private	final	int					ChanceOfWaiting 	= 5;						// Chanse of blocking process
	
	public 			String 				Name;				// Name of .exe file		//	***
	public 			int 				CreationTime;		// Time of creation			//	***
	
	public Process(byte PID, String Name, int CreationTime, byte Priority, int MemoryVolume, int ProcessorTime) {
		
// Default initialisation ****************************
		this.PID 				= PID;
		this.Name 				= Name;
		this.CreationTime 		= CreationTime;
		this.Priority 			= Priority;
		this.MemoryVolume 		= MemoryVolume;
		this.ProceesorTime		= ProcessorTime;
		this.NextNeededAdress	= 0x00;
// ***************************************************
		
		this.CommandCounter 	= 0;
		this.ProcessStatus 		= ProcessStatus.CREATION;
		this.MemorySegments 	= null;									// Not allocated
		this.Registers 			= 0x00;									// Default registers
	}
	
	public byte GetPID() {
		return PID;
	}
	
	public void Display() {
		System.out.println(Integer.toHexString(PID) + "\t" + Integer.toString(CreationTime) + "\t"+ Integer.toHexString(CommandCounter) + "\t" + ProcessStatus.toString() 
		+ "\t" + Integer.toString(MemoryVolume) + "\t" + Integer.toHexString(Priority) + "\t" + MemorySegments.toString());
	}
	
	public void AllocateMemory(ArrayDeque<Byte> MemorySegment) {		// Initialisation Mem. Segm.
		this.MemorySegments = MemorySegment;
		this.ProcessStatus = ProcessStatus.READINESS;
	}
	
	public ArrayDeque<Byte>	GetMemorySegments(){
		return MemorySegments;
	}
	
	public ProcessStatus GetProcessStatus() {
		return this.ProcessStatus;
	}
	
	public int GetMemoryVolume() {
		return this.MemoryVolume;
	}
	
	public byte ReducePriority() {										// Decrease process priority
		return (this.Priority - 1) == 0x7f ? this.Priority = 0x00 : --this.Priority;
	}
	
	public byte GetPriority() {
		return this.Priority;
	}
	
	public void SetReady() {
		this.ProcessStatus = ProcessStatus.READINESS;
	}
	
	public void SetWaiting() {
		this.ProcessStatus = ProcessStatus.WAITING;
	}
	
	public void SetExecuting() {										// Set process executable
		this.ProcessStatus = ProcessStatus.EXECUTION;
	}
	
	public byte Execute() {												// Executing function
		if((ProcessStatus == ProcessStatus.EXECUTION) || (ProcessStatus == ProcessStatus.READINESS)) {
			this.ProcessStatus = ProcessStatus.EXECUTION;
			this.CommandCounter++;
			this.Registers++;
			this.ProceesorTime--;
			
			if(this.ProceesorTime <= 0) 
				this.ProcessStatus = ProcessStatus.KILLING;
			
			// Imitation process behavior
			Random rand = new Random(System.currentTimeMillis());
			if ((rand.nextInt() % 100) < ChanceOfWaiting)
				this.ProcessStatus = ProcessStatus.WAITING;
		}
		

			
		return this.ProcessStatus;
	}
	
	
}
