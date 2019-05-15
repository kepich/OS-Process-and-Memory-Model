import java.util.ArrayDeque;
import java.util.HashMap;
/*
 * PageSize					= 4096 kB 		/ 		12 bits
 * NumberOfPages			= 16			/ 		4 bits (virtual) / 3 bits(physical)
 * PhysicalMemoryVolume		= 32784 kB		/ 		8 pages
 * VirtualMemoryVolume		= 65568 kB		/ 		16 pages
 * TypeOfManagement			= Paging
 * 
 * System's files are stored in low addresses (1 Page, but, if you want, you can change it in configurations <boardOfSystems>)
 * 
 */

public class MemoryManagementUnit {
	private	final 	int						PageSize		= 4096;									// Size of one page
	private	final 	byte					NumberOfPages	= 16;									// Size of page table
	private			byte[]					TableOfPages	= new byte[this.NumberOfPages];			// Table of pages
	private			boolean[]				BitMap			= new boolean[this.NumberOfPages];		// Mapping pages into physical memory
	private			int[]					LastHandling	= new int[this.NumberOfPages];			// Delay of last handling to pages !!! IN RAM !!!
	
	private			HashMap<Byte, Byte[]> 	Storage			= new HashMap<Byte, Byte[]>();			// Main data storage
	private			HashMap<Byte, Byte[]> 	RAM				= new HashMap<Byte, Byte[]>();
	
	private			byte					boardOfSystem;
	
	private			int						lastVirtAdress 	= 0x00;
	private			int						lastPhysAdress 	= 0x00;
	private			byte					lastNeededPage 	= 0;
	
	public MemoryManagementUnit(byte boardOfSystem) {
		
		// Initialization of all tables	***********************************
		this.boardOfSystem = boardOfSystem;
		for(byte i = boardOfSystem; i < this.NumberOfPages; i++) {
			this.TableOfPages[i] 	= 0x00;
			this.BitMap[i]			= false;
			this.LastHandling[i]	= 0;
		}
		
		// OS data initialisation	***************************************
		for(byte i = 0; i < this.boardOfSystem; i++) {
			this.TableOfPages[i]	= i;
			this.BitMap[i]			= true;
			this.LastHandling[i]	= 0;
			Byte[] os = {(byte) 0x7f, (byte) 0x7f};
			this.RAM.put((byte) 0, os);
		}
	}
	public int getPhysicalAdress(int virtualAdress) {
		for (int i = boardOfSystem; i < LastHandling.length; i++)								// Last handling update
			LastHandling[i]++;
		
		while(true) {
			// Search needed page in table and check it for mapping
			byte row = (byte) (virtualAdress / this.PageSize);
			if (this.BitMap[row]) {
				int newAddress = virtualAdress % this.PageSize + this.TableOfPages[row] * this.PageSize;
				this.LastHandling[row] = 0;
				
				lastVirtAdress = virtualAdress;
				lastPhysAdress = newAddress;
				lastNeededPage = row;
				
				return newAddress;
			}
			else
				this.Paging(virtualAdress);	// Page fault situation
		}
	}
	private void Paging(int VirtualAdress) {
		//******************************************************************
		int usedMostSeldom = this.LastHandling[(byte) (this.boardOfSystem)];
		byte index = (byte) (this.boardOfSystem);
		// Checking free space in RAM
		boolean isFullRAM = (RAM.size() == NumberOfPages / 2);
		
		if(isFullRAM) {
			for (byte i = (byte) (this.boardOfSystem); i < this.NumberOfPages; i++) {			// Searching most seldom used
				if((usedMostSeldom >= this.LastHandling[i]) && this.BitMap[i]){
					usedMostSeldom = this.LastHandling[i];
					index = i;
				}
			}
			
			// Save on disk storage temp page and restore needed page
			Byte[] storedPage = this.RAM.get(this.TableOfPages[index]);
			byte temp = (byte) (VirtualAdress / this.PageSize);
			Byte[] restoredPage = this.Storage.get(temp);
			
			// Saving unused page and restoring needed page
			this.Storage.put(index, storedPage);
			this.RAM.put(this.TableOfPages[index], restoredPage);
			
			// Correct tables
			this.BitMap[index] = false;
			this.BitMap[VirtualAdress / this.PageSize] = true;
			this.TableOfPages[VirtualAdress / this.PageSize] = this.TableOfPages[index];
			
		}
		else {
			byte physAddr = 0x00;		// Free address in RAM							
			// Searching free space in RAM
			for(byte i = boardOfSystem; i < NumberOfPages / 2; i++) {
				if(RAM.get(i) == null) {
					physAddr = i;
					break;
				}
			}
			
			byte temp = (byte) (VirtualAdress / this.PageSize);
			Byte[] restoredPage = this.Storage.get(temp);
			
			this.RAM.put(physAddr, restoredPage);
			this.BitMap[VirtualAdress / this.PageSize] = true;
			this.TableOfPages[VirtualAdress / this.PageSize] = physAddr;
		}
		
	}
	public boolean AllocateMemory(Process proc) {
		if (proc.GetPID() != 0x00) {
			int memVol 		= proc.GetMemoryVolume();
			int neededPages = memVol / PageSize + ((memVol % PageSize > 0) ? 1 : 0);
			int searchingSpace = 0;
			byte segmentPosition = 0;
			for (byte i = 0; i < NumberOfPages; i++) {
				if ((TableOfPages[i] == 0x00) && !BitMap[i])							// Page is free
					searchingSpace++;
				else {
					searchingSpace = 0;
					segmentPosition = (byte) (i + 1);
				}
				
				if(searchingSpace == neededPages) {						// Space is founded
					ArrayDeque<Byte> memory = new ArrayDeque<Byte>();	// Allocating memory
					for (byte j = 0; j < neededPages; j++) {
						memory.push((byte) (segmentPosition + j));
						TableOfPages[segmentPosition + j] = 0x01;
						Byte[] procData = {proc.GetPID(), proc.GetPID()};
						Storage.put((byte)(segmentPosition + j), procData);
					}
					proc.AllocateMemory(memory);
					return true;
				}
			}
		}
		else {
			ArrayDeque<Byte> memory = new ArrayDeque<Byte>();			// Allocating memory
			memory.push((byte)(0x00));
			proc.AllocateMemory(memory);
			return true;
		}
		return false;
	}
	public void Display() {
		System.out.println("*****************************Virtual Memory Table*******************************");
		System.out.println("lastPhysAdress:\t" + lastPhysAdress + "\t\tlastVirtAdress:\t" + lastVirtAdress + "\t\tlastNeededPage:\t" + lastNeededPage);
		System.out.println("********************************************************************************");
		
		System.out.print("VirtAddr  |\t");
		for (int i = 0; i < this.NumberOfPages; i++) {
			System.out.print(Integer.toHexString(i));
		}
		System.out.println();
		System.out.print("PhysAddr  |\t");
		for (int i = 0; i < this.NumberOfPages; i++) {
			System.out.print(Integer.toHexString(TableOfPages[i]));
		}
		System.out.println();
		System.out.print("tMapping  |\t");
		for (int i = 0; i < this.NumberOfPages; i++) {
			System.out.print((BitMap[i]) ? 1 : 0);
		}
		System.out.println();
		
		System.out.print("Addr:\t");
		for(byte i = 0x00; i < (NumberOfPages / 2); i++)
			System.out.print(i);
		System.out.println();
		System.out.print("Page:\t");
		for(byte i = 0x00; i < (NumberOfPages / 2); i++) {
			boolean isMapped = false;
			byte tempPage = 0x00;
			for(tempPage = 0x00; tempPage < NumberOfPages; tempPage++)
				if((TableOfPages[tempPage] == i) && BitMap[tempPage]) {
					isMapped = true;
					break;
				}
			
			System.out.print(isMapped ? Integer.toHexString(tempPage) : 0);
		}
		System.out.println();
	}
	public void KillProcess(Process proc) {								// Cleaning All information about processes from RAM
		ArrayDeque<Byte> MemorySegments = proc.GetMemorySegments();
		
		if(MemorySegments != null) {
			for(Byte i: MemorySegments) {
				Storage.remove(i);
				if(BitMap[i])
					RAM.remove(TableOfPages[i]);
				TableOfPages[i] = 0x00;
				BitMap[i] = false;
			}
		}
	}
}
