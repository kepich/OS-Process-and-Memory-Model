
public class Process {
	private 	byte 			PID;				// Process identificator	//	***
	private		byte			CommandCounter;									// 	UPDATE: EXEC
	private		ProcessStatus	ProcessStatus;									//	UPDATE:	TASK_MANAGER
	private		int				MemorySegment;									//	UPDATE:	MEM_MANAGER
	private		int				MemoryVolume;									//	***
	private		byte			Priority;			// Range: 0:31				//	UPDATE:	TASK_MANAGER / ***
	private		int				Registers;										//	UPDATE:	EXEC
	private		int				ProceesorTime;									//	UPDATE: EXEC
	
	public 		String 			Name;				// Name of .exe file		//	***
	public 		int 			CreationTime;		// Time of creation			//	***
	
	public Process(byte PID, String Name, int CreationTime, byte Priority, int MemoryVolume, int ProcessorTime) {
		
// Default initialisation ****************************
		this.PID 			= PID;
		this.Name 			= Name;
		this.CreationTime 	= CreationTime;
		this.Priority 		= Priority;
		this.MemoryVolume 	= MemoryVolume;
		this.ProceesorTime	= ProcessorTime;
// ***************************************************
		
		this.CommandCounter 	= 0;
		this.ProcessStatus 		= ProcessStatus.CREATION;
		this.MemorySegment 		= -1;								// Not allocated
		this.Registers 			= 0x00;								// Default registers
	}
	
	public void AllocateMemory(int MemorySegment) {					// Initialisation Mem. Segm.
		this.MemorySegment = MemorySegment;
		this.ProcessStatus = ProcessStatus.READINESS;
	}
	
	public byte ReducePriority() {									// Decrease process priority
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
	
	public void SetExecuting() {									// Set process executable
		this.ProcessStatus = ProcessStatus.EXECUTION;
	}
	
	public ProcessStatus Execute() {								// Executing function
		this.CommandCounter++;
		this.Registers++;
		this.ProceesorTime--;
		
		if(this.ProceesorTime == 0) 
			return this.ProcessStatus = ProcessStatus.KILLING;
		else 
			return this.ProcessStatus = ProcessStatus.EXECUTION;
	}
	
	
}
