import java.util.ArrayDeque;

//- Priority planner with several(4) priority queue
public class Scheduler {																// Priority range:
	private ArrayDeque<Process> priority_4 				= new ArrayDeque<Process>();	// 31 - 24
	private ArrayDeque<Process> priority_3 				= new ArrayDeque<Process>();	// 23 - 16
	private ArrayDeque<Process> priority_2 				= new ArrayDeque<Process>();	// 15 - 8
	private ArrayDeque<Process> priority_1 				= new ArrayDeque<Process>();	// 7 - 0
	
	private ArrayDeque<Process> inputProcessStream 		= new ArrayDeque<Process>();	// All input processes
	
	public void AddNewProcess(Process newProcess) {										// Add new process into scheduler
		this.inputProcessStream.offer(newProcess);
	}
	
	private void Distribute() {															// Get all processes from the input stream and put its into appropriate queue
		while (!this.inputProcessStream.isEmpty()) {
			Process temp = this.inputProcessStream.poll();
			byte tempPriority = temp.GetPriority();
			
			if (tempPriority >= 24)
				this.priority_4.offer(temp);
			else if (tempPriority >= 16)
				this.priority_3.offer(temp);
			else if (tempPriority >= 8)
				this.priority_2.offer(temp);
			else
				this.priority_1.offer(temp);
		}
	}
	
	private void AddProcessIntoQueue(Process p) {										// To imitate cycle list
		byte tempPriority = p.GetPriority();
		
		if (tempPriority >= 24)
			this.priority_4.offer(p);
		else if (tempPriority >= 16)
			this.priority_3.offer(p);
		else if (tempPriority >= 8)
			this.priority_2.offer(p);
		else
			this.priority_1.offer(p);
	}
	
	public Process getTempExecutedProcess() {											// Planning processes 
		this.Distribute();
		
		Process resultProcess = null;
		
		while (true) {
			if (!this.priority_4.isEmpty()) {
				resultProcess = this.priority_4.poll();
				resultProcess.ReducePriority();
				this.AddProcessIntoQueue(resultProcess);
			}
			else if (!this.priority_3.isEmpty()) {
				resultProcess = this.priority_3.poll();
				resultProcess.ReducePriority();
				this.AddProcessIntoQueue(resultProcess);
			}
			else if (!this.priority_2.isEmpty()) {
				resultProcess = this.priority_2.poll();
				resultProcess.ReducePriority();
				this.AddProcessIntoQueue(resultProcess);
			}
			else if (!this.priority_1.isEmpty()) {
				resultProcess = this.priority_1.poll();
				resultProcess.ReducePriority();
				this.AddProcessIntoQueue(resultProcess);
			}
			else {																			// Create idle process
				resultProcess = new Process((byte)0x7F, "System_idle", 0, (byte) 0x00, 0, 1);
				/*
				 * INSERT MEMORY ALLOCATING SYSTEM
				 */
			}


			if(resultProcess.GetProcessStatus() == ProcessStatus.KILLING)
				continue;
			else
				break;
		}
		
		return resultProcess;
	}
}
