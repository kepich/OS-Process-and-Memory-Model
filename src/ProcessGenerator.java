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
	private static byte			prevPID = 0x00;
	
	public ProcessGenerator(ArrayList<Process> procTable, int maxVol, int maxProcTime) {
		processTable 		= procTable;
		maxMemoryVolume		= maxVol;
		maxProcessorTime	= maxProcTime;
	}

	public ArrayList<Process> GenerateProcessPopulation(int TEMP_TIME, int START_GENERATION) {
		Random 				rand 	= new Random();
		ArrayList<Process> 	result 	= new ArrayList<Process>();
		
		if(START_GENERATION != 0) {
			for (int i = 0; i < START_GENERATION; i++) {
				byte 	tempPID 			= (byte) (prevPID++ + 1);
				String 	tempName			= processesNames[Math.abs(rand.nextInt()) % processesNames.length];
				int		tempCreationTime	= TEMP_TIME;
				byte	tempPriority		= (byte) (Math.abs(rand.nextInt()) % 32);
				int		tempMemoryVolume	= Math.abs(rand.nextInt()) % (maxMemoryVolume * 4096);
				int		tempProcTime		= Math.abs(rand.nextInt()) % maxProcessorTime;
				
				result.add(new Process(tempPID, tempName, tempCreationTime, tempPriority, tempMemoryVolume, tempProcTime));
			}
		}
		else {
			result.add(new Process((byte) 0x01, "SYSTEM_IDLE", TEMP_TIME, (byte)0, 1, 1));
		}
		return result;
	}
}
