import java.util.ArrayDeque;

//- Priority planner with several(4) priority queue
public class Scheduler {																// Priority range:
	private ArrayDeque<Process> priority_4 				= new ArrayDeque<Process>();	// 31 - 24
	private ArrayDeque<Process> priority_3 				= new ArrayDeque<Process>();	// 23 - 16
	private ArrayDeque<Process> priority_2 				= new ArrayDeque<Process>();	// 15 - 8
	private ArrayDeque<Process> priority_1 				= new ArrayDeque<Process>();	// 7 - 0
	
	private ArrayDeque<Process> inputProcessStream 		= new ArrayDeque<Process>();	// All input processes
	
	public void AddNewProcess(Process newProcess) {							// Add new process into scheduler
		this.inputProcessStream.addLast(newProcess);
	}
	
	private void Distribute() {												// Get all processes from the input stream and put its into appropriate queue
		while (!this.inputProcessStream.isEmpty()) {
			Process temp = this.inputProcessStream.poll();
			byte tempPriority = temp.GetPriority();
			
			if (tempPriority >= 24)
				this.priority_4.addLast(temp);
			else if (tempPriority >= 16)
				this.priority_3.addLast(temp);
			else if (tempPriority >= 8)
				this.priority_2.addLast(temp);
			else
				this.priority_1.addLast(temp);
		}
	}
	
	private void AddProcessIntoQueue(Process p) {							// To imitate cycle list
		byte tempPriority = p.GetPriority();
		
		if (tempPriority >= 24)
			this.priority_4.addLast(p);
		else if (tempPriority >= 16)
			this.priority_3.addLast(p);
		else if (tempPriority >= 8)
			this.priority_2.addLast(p);
		else
			this.priority_1.addLast(p);
	}
	
	public Process getTempExecutedProcess() {
		this.Distribute();
		
	}
}
