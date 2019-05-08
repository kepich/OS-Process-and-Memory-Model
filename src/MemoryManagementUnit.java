import java.util.*;

/*
 * PageSize					= 4098 kB 		/ 		12 bits
 * NumberOfPages			= 16			/ 		4 bits (virt) / 3 bits(phys)
 * PhysicalMemoryVolume		= 32784 kB		/ 		8 pages
 * VirtualMemoryVolume		= 65568 kB		/ 		16 pages
 * TypeOfManagement			= Paging
 */

public class MemoryManagementUnit {
	private		final		int						PageSize		= 4098;								// Size of one page
	private		final		byte					NumberOfPages	= 16;								// Size of pages table
	private					byte[]					TableOfPages	= new byte[this.NumberOfPages];		// Table of pages
	private					boolean[]				BitMap			= new boolean[this.NumberOfPages];	// Mapping pages into physical memory
	private					int[]					LastHanling		= new int[this.NumberOfPages];		// Delay of last handling to pages
	
	private					HashMap<Byte, Byte[]> 	Storage			= new HashMap<Byte, Byte[]>();		// Main data storage
	private					HashMap<Byte, Byte[]> 	RAM				= new HashMap<Byte, Byte[]>();
	
	private					byte					boardOfSystem;
	
	public MemoryManagementUnit(byte boardOfSystem) {
		/*
		 * 
		 * 			Create initialisation of system (loading height and low adresses)
		 * 
		 */
		this.boardOfSystem = boardOfSystem;
		for(byte i = 0; i < this.NumberOfPages; i++) {
			this.TableOfPages[i] 	= 0x00;
			this.BitMap[i]			= false;
			this.LastHanling[i]		= 0;
		}
	}
	
	public int getPhysicalAdress(int virtualAdress) {
		while(true) {
			// Search needed page in table and check it for mapping
			byte row = (byte) (virtualAdress / this.PageSize);
			if (this.BitMap[row]) {
				int newAddress = virtualAdress % this.PageSize + this.TableOfPages[row] * this.PageSize;
				return newAddress;
			}
			else
				this.Paging(row);
		}
	}
	
	private void Paging(byte VirtualAdress) {
		//******************************************************************
		int usedMostSeldom = this.LastHanling[(byte) (this.boardOfSystem + 1)];
		int index = (byte) (this.boardOfSystem + 1);
		
		for (int i = this.boardOfSystem + 1; i < this.NumberOfPages; i++) {			// Searching most seldom used
			if(usedMostSeldom > this.LastHanling[i]) {
				usedMostSeldom = this.LastHanling[i];
				index = i;
			}
		}
		
		// Save on disk storage temp page
		
		byte[] storedPage = this.RAM.get(this.TableOfPages)
		this.Storage.put(index, this.RAM.get(this.TableOfPages))
		//this.Storage[index] = 
	}
}
