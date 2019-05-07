/*
 * PageSize					= 4098 kB 		/ 		12 bits
 * NumberOfPages			= 16			/ 		4 bits (virt) / 3 bits(phys)
 * PhysicalMemoryVolume		= 32784 kB		/ 		8 pages
 * VirtualMemoryVolume		= 65568 kB		/ 		16 pages
 * TypeOfManagement			= Paging
 */

public class MemoryManagementUnit {
	private		final		int			PageSize		= 4098;								// Size of one page
	private		final		byte		NumberOfPages	= 16;								// Size of pages table
	private					byte[]		TableOfPages	= new byte[this.NumberOfPages];		// Table of pages
	private					boolean[]	BitMap			= new boolean[this.NumberOfPages];	// Mapping pges into physical memory
	
	public MemoryManagementUnit() {
		for(byte i = 0; i < this.NumberOfPages; i++) {
			this.TableOfPages[i] 	= 0x00;
			this.BitMap[i]			= false;
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
				this.Paging();
		}
	}
	
	private void Paging() {
		
	}
}
