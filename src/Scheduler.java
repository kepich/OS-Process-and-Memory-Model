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
	
	public void Display() {
		
		ArrayDeque<Byte> inp = new ArrayDeque<Byte>();
		ArrayDeque<Byte> p1 = new ArrayDeque<Byte>();
		ArrayDeque<Byte> p2 = new ArrayDeque<Byte>();
		ArrayDeque<Byte> p3 = new ArrayDeque<Byte>();
		ArrayDeque<Byte> p4 = new ArrayDeque<Byte>();
		
		for(Process i: inputProcessStream)
			inp.push(i.GetPID());
		for(Process i: priority_1)
			p1.push(i.GetPID());
		for(Process i: priority_2)
			p2.push(i.GetPID());
		for(Process i: priority_3)
			p3.push(i.GetPID());
		for(Process i: priority_4)
			p4.push(i.GetPID());
		
		String buf = inp.toString();
		buf = p1.toString();
		buf = p2.toString();
		buf = p3.toString();
		buf = p4.toString();
		
		System.out.println("INPUT : \t" + inp.toString());
		System.out.println("0 - 7 : \t" + p1.toString());
		System.out.println("8 -15 : \t" + p2.toString());
		System.out.println("16-23 : \t" + p3.toString());
		System.out.println("24-31 : \t" + buf);
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
	
		if (!this.priority_4.isEmpty())
			resultProcess = this.priority_4.poll();
		else if (!this.priority_3.isEmpty())
			resultProcess = this.priority_3.poll();
		else if (!this.priority_2.isEmpty())
			resultProcess = this.priority_2.poll();
		else if (!this.priority_1.isEmpty())
			resultProcess = this.priority_1.poll();

		if(resultProcess != null)
			if(resultProcess.GetProcessStatus() != ProcessStatus.KILLING) {
				resultProcess.ReducePriority();
				this.AddProcessIntoQueue(resultProcess);
			}
		
		return resultProcess;
	}
}
