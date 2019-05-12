import java.util.HashMap;
import java.util.ArrayDeque;
/*
 * PageSize					= 4098 kB 		/ 		12 bits
 * NumberOfPages			= 16			/ 		4 bits (virt) / 3 bits(phys)
 * PhysicalMemoryVolume		= 32784 kB		/ 		8 pages
 * VirtualMemoryVolume		= 65568 kB		/ 		16 pages
 * TypeOfManagement			= Paging
 * 
 * System's files are stored in low adresses (1 Page, but, if you want, you can cange it in configs <boardOfSystems>)
 * 
 */

public class MemoryManagementUnit {
	private	final 	int						PageSize		= 4096;									// Size of one page
	private	final 	byte					NumberOfPages	= 16;									// Size of page table
	private			byte[]					TableOfPages	= new byte[this.NumberOfPages];			// Table of pages
	private			boolean[]				BitMap			= new boolean[this.NumberOfPages];		// Mapping pages into physical memory
	private			int[]					LastHandling	= new int[this.NumberOfPages / 2];		// Delay of last handling to pages !!! IN RAM !!!
	
	private			HashMap<Byte, Byte[]> 	Storage			= new HashMap<Byte, Byte[]>();			// Main data storage
	private			HashMap<Byte, Byte[]> 	RAM				= new HashMap<Byte, Byte[]>();
	
	private			byte					boardOfSystem;
	
	public MemoryManagementUnit(byte boardOfSystem) {
		
		// Initialisation of all tables	***********************************
		this.boardOfSystem = boardOfSystem;
		for(byte i = boardOfSystem; i < this.NumberOfPages; i++) {
			this.TableOfPages[i] 	= 0x00;
			this.BitMap[i]			= false;
			this.LastHandling[i]	= 0;
		}
		
		// OS data initialisation	***************************************
		for(byte i = 0; i < this.boardOfSystem; i++) {
			this.TableOfPages[i]	= (byte) (i + 1);
			this.BitMap[i]			= true;
			this.LastHandling[i]	= 0;
		}
	}
	public int getPhysicalAdress(int virtualAdress) {
		for (int i = 0; i < LastHandling.length; i++)			// Last handling update
			LastHandling[i]++;
		
		while(true) {
			// Search needed page in table and check it for mapping
			byte row = (byte) (virtualAdress / this.PageSize);
			if (this.BitMap[row]) {
				int newAddress = virtualAdress % this.PageSize + this.TableOfPages[row] * this.PageSize;
				this.LastHandling[row] = 0;
				return newAddress;
			}
			else
				this.Paging(row);	// Page fault situation
		}
	}
	private void Paging(byte VirtualAdress) {
		//******************************************************************
		int usedMostSeldom = this.LastHandling[(byte) (this.boardOfSystem + 1)];
		byte index = (byte) (this.boardOfSystem + 1);
		
		for (byte i = (byte) (this.boardOfSystem + 1); i < this.NumberOfPages; i++) {			// Searching most seldom used
			if((usedMostSeldom > this.LastHandling[i]) && this.BitMap[i]) {
				usedMostSeldom = this.LastHandling[i];
				index = i;
			}
		}
		
		// Save on disk storage temp page and restore needed page
		Byte[] storedPage = this.RAM.get(this.TableOfPages[index]);
		Byte[] restoredPage = this.Storage.get(VirtualAdress / this.PageSize);
		
		// Saving unused page and restoring needed page
		this.Storage.put(index, storedPage);
		this.RAM.put(this.TableOfPages[index], restoredPage);
		
		// Correct tables
		this.BitMap[index] = false;
		this.BitMap[VirtualAdress / this.PageSize] = true;
		this.TableOfPages[VirtualAdress / this.PageSize] = this.TableOfPages[index];
	}
	public boolean AllocateMemory(Process proc) {
		int memVol 		= proc.GetMemoryVolume();
		int neededPages = memVol / PageSize + ((memVol % PageSize > 0) ? 1 : 0);
		
		int searchingSpace = 0;
		byte segmentPosition = 0x00;
		for (byte i = boardOfSystem; i < NumberOfPages; i++) {
			if (TableOfPages[i] == 0x00)							// Page is free
				searchingSpace++;
			else {
				searchingSpace = 0;
				segmentPosition = i;
			}
			
			if(searchingSpace == neededPages) {						// Space is founded
				ArrayDeque<Byte> memory = new ArrayDeque<Byte>();	// Allocating memory
				for (byte j = 0; j < neededPages; j++) {
					memory.push((byte) (segmentPosition + j));
					TableOfPages[segmentPosition + j] = 0x01;
				}
				proc.AllocateMemory(memory);
				return true;
			}
		}
		
		return false;
	}
	public void Display() {
		System.out.println("Index\t\tPages\t\tMapping");
		for (int i = 0; i < this.NumberOfPages; i++) {
			System.out.println(Integer.toHexString(i) + "\t\t" + Integer.toHexString(TableOfPages[i]) + "\t\t" + Boolean.toString(BitMap[i]));
		}
	}
	public void KillProcess(Process proc) {								// Cleaning All information about processes from RAM
		ArrayDeque<Byte> MemorySegments = proc.GetMemorySegments();
		for(Byte i: MemorySegments) {
			Storage.remove(i);
			if(BitMap[i])
				RAM.remove(TableOfPages[i]);
			TableOfPages[i] = 0x00;
		}
		
	}
}
