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
	private			int					NextNeededAdress;	// Adress exec. command		//	UPDATE: EXEC
	
	private	final	int					ChanceOfWaiting 	= 5;						// Chanse of blocking process
	private	final	int					ChanceOfWakeUp	 	= 5;						// Chanse of blocking process
	private final	int					PageSize			= 4096;
	
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
		+ "\t" + Integer.toString(MemoryVolume) + "\t" + Integer.toHexString(Priority) + "\t" + ((MemorySegments != null)? MemorySegments.toString() : "null"));
	}
	
	public void AllocateMemory(ArrayDeque<Byte> MemorySegment) {		// Initialisation Mem. Segm.
		this.MemorySegments = MemorySegment;
		this.ProcessStatus = ProcessStatus.READINESS;
		NextNeededAdress = MemorySegment.getFirst() * PageSize;
	}
	
	public ArrayDeque<Byte>	GetMemorySegments(){
		return MemorySegments;
	}
	
	public String GetName(){
		return Name;
	}
	
	public ProcessStatus GetProcessStatus() {
		return this.ProcessStatus;
	}
	
	public int GetMemoryVolume() {
		return this.MemoryVolume;
	}
	
	public void ReducePriority() {										// Decrease process priority
		this.Priority = (byte) ((this.Priority - 1) == 0xffffffff ? this.Priority = 0x00 : (this.Priority - 1));
	}
	
	public byte GetPriority() {
		return this.Priority;
	}
	
	public void SetReady() {
		this.ProcessStatus = ProcessStatus.READINESS;
	}
	
	public void SetWaiting() {
		this.ProcessStatus = ProcessStatus.BLOCKING;
	}
	
	public void SetExecuting() {										// Set process executable
		this.ProcessStatus = ProcessStatus.EXECUTION;
	}
	
	public int Execute() {												// Executing function
		Random rand = new Random(System.currentTimeMillis());
		if((ProcessStatus == ProcessStatus.EXECUTION) || (ProcessStatus == ProcessStatus.READINESS)) {
			this.ProcessStatus = ProcessStatus.EXECUTION;
			this.CommandCounter++;
			this.Registers++;
			this.ProceesorTime--;
			
			if(this.ProceesorTime <= 0) 
				this.ProcessStatus = ProcessStatus.ISKILLING;
			
			// Imitation process behavior
			if ((Math.abs(rand.nextInt() % 100)) < ChanceOfWaiting)
				this.ProcessStatus = ProcessStatus.BLOCKING;
		}
		else {
			if((Math.abs(rand.nextInt() % 100)) < ChanceOfWakeUp) {
				this.ProcessStatus = ProcessStatus.EXECUTION;
			}
		}

		if(ProcessStatus == ProcessStatus.EXECUTION) 				// Creating reauest for needed data on this address
			return MemorySegments.getLast() * PageSize + Math.abs(rand.nextInt()) % MemoryVolume;
		else
			return 0x00;
	}
	
	
}
