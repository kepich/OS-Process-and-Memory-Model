import java.util.ArrayDeque;
import java.util.Random;

public class Process {
	private 		byte 				PID;				// Process ID				//	***
	private			int					CommandCounter;									// 	UPDATE: EXEC
	private			ProcessStatus		ProcessStatus;									//	UPDATE:	TASK_MANAGER
	private			ArrayDeque<Byte>	MemorySegments;									//	UPDATE:	MEM_MANAGER
	private			int					MemoryVolume;									//	***
	private			byte				Priority;			// Range: 0:31				//	UPDATE:	TASK_MANAGER / ***
	private			int					ProceesorTime;									//	UPDATE: EXEC
	
	private			int					ChanceOfWaiting 	= 5;						// Chance of blocking process
	private	final	int					ChanceOfWakeUp	 	= 20;						// Chance of wake up process
	private final	int					PageSize			= 4096;
	
	public 			String 				Name;				// Name of execution file	//	***
	public 			int 				CreationTime;		// Time of creation			//	***
	
	public Process(byte PID, String Name, int CreationTime, byte Priority, int MemoryVolume, int ProcessorTime) {
		
// Default initialization ****************************
		this.PID 				= PID;
		this.Name 				= Name;
		this.CreationTime 		= CreationTime;
		this.Priority 			= Priority;
		this.MemoryVolume 		= MemoryVolume;
		this.ProceesorTime		= ProcessorTime;
// ***************************************************
		
		this.CommandCounter 	= 0;
		this.ProcessStatus 		= ProcessStatus.CREATION;
		this.MemorySegments 	= new ArrayDeque<Byte>();									// Not allocated
	}
	
	public byte GetPID() {
		return PID;
	}
	
	public void Display() {
		System.out.println(Integer.toHexString(PID) + "\t" + Integer.toString(CreationTime) + "\t"+ Integer.toHexString(CommandCounter) + "\t" + ProcessStatus.toString() 
		+ "\t" + Integer.toString(MemoryVolume) + "\t" + Integer.toHexString(Priority) + "\t" + ((MemorySegments != null)? MemorySegments.toString() : "null"));
	}
	
	public void AllocateMemory(ArrayDeque<Byte> MemorySegment) {		// Initialization Memory Segments
		this.MemorySegments = MemorySegment;
		this.ProcessStatus = ProcessStatus.READINESS;
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
	
	public void SetSystemProcess() {
		ChanceOfWaiting = 60;
	}
	
	public int Execute() {												// Executing function
		Random rand = new Random(System.currentTimeMillis());
		if((ProcessStatus == ProcessStatus.EXECUTION) || (ProcessStatus == ProcessStatus.READINESS)) {
			this.ProcessStatus = ProcessStatus.EXECUTION;
			this.CommandCounter++;
			if(PID != 0x00)
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

		if(ProcessStatus == ProcessStatus.EXECUTION) 				// Creating request for needed data on this address
			return MemorySegments.getLast() * PageSize + Math.abs(rand.nextInt()) % MemoryVolume;
		else
			return 0x00;
	}
	
	
}
