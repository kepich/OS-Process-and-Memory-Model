import java.util.ArrayList;
import java.util.Random;

public class ProcessGenerator {
	private static final String[] processesNames = {
			"Stalker",
			"svchost",
			"windefender" };
	private	ArrayList<Process> 	processTable;
	private	int					maxMemoryVolume;
	private	int					maxProcessorTime;		
	
	public ProcessGenerator(ArrayList<Process> procTable, int maxVol, int maxProcTime) {
		processTable 		= procTable;
		maxMemoryVolume		= maxVol;
		maxProcessorTime	= maxProcTime;
	}

	public ArrayList<Process> GenerateProcessPopulation(int TEMP_TIME, int START_GENERATION) {
		Random 				rand 	= new Random();
		ArrayList<Process> 	result 	= new ArrayList<Process>();
		
		for (int i = 0; i < START_GENERATION; i++) {
			byte 	tempPID 			= 0x00;
			String 	tempName			= processesNames[rand.nextInt() % processesNames.length];
			int		tempCreationTime	= TEMP_TIME;
			byte	tempPriority		= (byte) (rand.nextInt() % 32);
			int		tempMemoryVolume	= rand.nextInt() % maxMemoryVolume;
			int		tempProcTime		= rand.nextInt() % maxProcessorTime;
			
			for (byte k = 0x00; ; k++) {
				boolean cool = true;
				for(Process j: processTable) {
					if(j.GetPID() == k)
						cool = false;
					break;
				}
				if(cool) {
					tempPID = k;
					break;
				}
			}
			
			result.add(new Process(tempPID, tempName, tempCreationTime, tempPriority, tempMemoryVolume, tempProcTime));
		}
		
		return result;
	}
}
