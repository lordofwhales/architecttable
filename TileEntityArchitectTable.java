package mods.ArchitectTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityArchitectTable extends TileEntity implements IInventory, ISidedInventory {	
	private ItemStack[] inv;
	public IInventory craftResult;
	public IInventory craftSupply;
	public SlotATCrafting slot;
	
	public TileEntityArchitectTable() {
		inv = new ItemStack[28]; Arrays.fill(inv, null);
		craftSupply = new InventoryBasic("robertwan_atCraftSupply",false, 18);
		craftResult = new InventoryCraftResult();
	}
	
	@Override public int getSizeInventory() {return inv.length; }
	@Override public String getInvName() { return "Architect Table"; }
	@Override public int getInventoryStackLimit() { return 64; }
	

	@Override public ItemStack getStackInSlot(int slot) {  
		if(slot==28) return findRecipe();
		return inv[slot];
	}
	
	public static HashMap<ItemStack, Integer> getBlueprintRequirements(ItemStack print) {
		NBTTagList items = (NBTTagList)print.stackTagCompound.getTag("requirements");
		HashMap<ItemStack, Integer> requirements = new HashMap<ItemStack, Integer>();
		for(int i = 0; i<print.stackTagCompound.getInteger("numReq"); i++) {
			NBTTagCompound item = (NBTTagCompound)items.tagAt(i);
			requirements.put(ItemStack.loadItemStackFromNBT(item), item.getInteger("amount"));
		}
		return requirements;
	}
	public static boolean isWritBlueprint(ItemStack print) {
		return print!=null && print.itemID==ArchitectTable.blueprint.itemID && print.getItemDamage()==1;
	}
	public boolean isValidBlueprint(ItemStack print) {
		if(print==null || !isWritBlueprint(print)) return false;
		HashMap<ItemStack, Integer> req = getBlueprintRequirements(print);
		for(ItemStack r : req.keySet()) {
			int i=9;
			for(; i<27; i++) {
				if(getStackInSlot(i)==null) continue;
				ItemStack wildInSlot = getStackInSlot(i).copy();
				if(OreDictionary.getOreID(wildInSlot)>-1) wildInSlot.setItemDamage(OreDictionary.WILDCARD_VALUE);
				if((getStackInSlot(i).isItemEqual(r) || OreDictionary.itemMatches(wildInSlot, r, false)) && req.get(r)>0)
						req.put(r, req.get(r)-getStackInSlot(i).stackSize);
			}
		}
		for(ItemStack r : req.keySet()) if(req.get(r)>0) return false;
		return true;
	}
	public ItemStack findRecipe() {
		if(isValidBlueprint(getStackInSlot(27))) {
			boolean[] checked = new boolean[9];
			Arrays.fill(checked, false);
			NBTTagList list = (NBTTagList)getStackInSlot(27).stackTagCompound.getTag("blueprint");
			for(int i=0; i<getStackInSlot(27).stackTagCompound.getInteger("length"); i++) {
				ItemStack req = ItemStack.loadItemStackFromNBT((NBTTagCompound)list.tagAt(i));
				byte slot = ((NBTTagCompound)list.tagAt(i)).getByte("slot");
				checked[slot]=true;
				ItemStack in = getStackInSlot(slot);
				if(in!=null && !in.isItemEqual(req)) return null;
			}
			for(int i=0; i<9; i++) if(!checked[i] && getStackInSlot(i)!=null) return null;
			return ItemStack.loadItemStackFromNBT((NBTTagCompound)getStackInSlot(27).stackTagCompound.getTag("output"));
		}
		InventoryCrafting craftMatrix = new LocalCrafting();
		for(int i=0; i<9; i++) craftMatrix.setInventorySlotContents(i,getStackInSlot(i));		
		ItemStack preOutput = CraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj);
		if(preOutput!=null && preOutput.isItemEqual(new ItemStack(ArchitectTable.blueprint,1,0))) return preOutput;
		
		craftMatrix = new LocalCrafting();
		for(int i=0; i<craftMatrix.getSizeInventory(); i++) {
			ItemStack stack = getStackInSlot(i);
			if(stack!=null && isWritBlueprint(stack) && isValidBlueprint(stack)) {
				craftMatrix.setInventorySlotContents(i,
						ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag("output")));
			} else craftMatrix.setInventorySlotContents(i, stack);
		}

		return CraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj);
	}
	
	@Override public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if(stack==null) return stack;
		if(stack.stackSize<=amount)
			setInventorySlotContents(slot,null);
		else {
			stack = stack.splitStack(amount);
			if(stack.stackSize==0) setInventorySlotContents(slot,null);
		}
		return stack;
	}

	@Override public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if(stack!=null) setInventorySlotContents(slot,null);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(slot==28) {
			if(this.slot!=null) this.slot.decreaseItems(this);
			return;
		}
		inv[slot] = stack;
		if(stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord,yCoord,zCoord)==this &&
				player.getDistanceSq(xCoord+0.5,yCoord+0.5,zCoord+0.5) < 64;
	}

	@Override public void openChest() {	}
	@Override public void closeChest() { }

	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
				
		NBTTagList tagList = tagCompound.getTagList("Inventory");
		for(int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if(slot >= 0 && slot < inv.length) {
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}
	@Override public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		
		NBTTagList itemList = new NBTTagList();	
		for(int i = 0; i < inv.length; i++) {
			ItemStack stack = inv[i];
			if(stack != null) {
				NBTTagCompound tag = new NBTTagCompound();	
				tag.setByte("Slot", (byte)i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Inventory", itemList);
	}

	@Override public boolean isInvNameLocalized() { return false; }			
	
	public void write() {
		ItemStack s = getStackInSlot(27);
		if(worldObj.isRemote || isWritBlueprint(s)) return;
		if(findRecipe()==null) {
			System.out.println("yeh");
			int find = -1;
			for(int i=0; i<9; i++) {
				if(getStackInSlot(i)==null) continue;
				if(!isWritBlueprint(getStackInSlot(i))) return;
				else if(find>-1) return;
				else find = i;
			}
			if(find>-1) {
				setInventorySlotContents(27, getStackInSlot(find).copy());
				getStackInSlot(27).stackSize = 1;
			}
			return;
		}
		ItemStack print = s.copy();
		if(print.stackTagCompound==null) print.stackTagCompound = new NBTTagCompound();
		NBTTagList items = new NBTTagList();
		int len=0;
		HashMap<ItemStack, Integer> req = new HashMap<ItemStack, Integer>();
		for(int i=0; i<9; i++) {
			if(getStackInSlot(i)!=null) {
				len++;
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("slot", (byte)i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
		
				if(isWritBlueprint(getStackInSlot(i))) {
					HashMap<ItemStack, Integer> other = getBlueprintRequirements(getStackInSlot(i));
					for(ItemStack key : other.keySet()) {
						boolean found = false;
						for(ItemStack r : req.keySet()) {
							if(key.isItemEqual(r)) {
								int mult = ItemBlueprint.getOutput(getStackInSlot(i)).stackSize;
								if(other.get(key)%mult!=0) return;
								req.put(r, req.get(r)+other.get(key)/mult);
								found = true;
								break;
							}
						} if(!found) req.put(key, other.get(key));
					}
				} else {
					boolean found = false;
					for(ItemStack key : req.keySet()) {
						if(key.isItemEqual(getStackInSlot(i))) {
							req.put(key,req.get(key)+1);
							found = true;
							break;
						}
					} if(!found) req.put(getStackInSlot(i), 1);
				}
			}
		}
		NBTTagList requirements = new NBTTagList();
		for(ItemStack key : req.keySet()) {
			NBTTagCompound item = new NBTTagCompound();
			key.writeToNBT(item);
			item.setInteger("amount", req.get(key));
			requirements.appendTag(item);
		}
		print.setItemDamage(1);
		print.stackTagCompound.setInteger("length", len);
		print.stackTagCompound.setInteger("numReq", req.size());
		print.stackTagCompound.setTag("blueprint",items);
		print.stackTagCompound.setTag("requirements", requirements);
		NBTTagCompound out = new NBTTagCompound();
		findRecipe().writeToNBT(out);
		print.stackTagCompound.setCompoundTag("output", out);
		String name = findRecipe().getDisplayName();
		if(name==null || name.equals("null") || name.equals("")) name = findRecipe().getDisplayName();
		if(name==null || name.equals("null") || name.equals("")) name = "Something";
		print.stackTagCompound.setString("Tooltip", name);
		setInventorySlotContents(27, print.copy());
		onInventoryChanged();
	}

	@Override public boolean isItemValidForSlot(int i, ItemStack s) {
		return i>8 && i<27;
	}
	@Override public int[] getAccessibleSlotsFromSide(int side) {
		if(side==0) { //bottom
			return new int[] {28};
		}
		int[] out = new int[18];
		for(int i=0; i<out.length; i++) out[i]=i+9;
		return out;
	}

	@Override public boolean canInsertItem(int slot, ItemStack item, int side) {
		return isItemValidForSlot(slot, item);
	}
	@Override public boolean canExtractItem(int slot, ItemStack item, int side) {
		return slot==28 && findRecipe()!=null && side==0; //bottom
	}
	
	class LocalCrafting extends InventoryCrafting {
		public LocalCrafting() {
			super(new Container() {
				public boolean canInteractWith(EntityPlayer e) {
					return false;
				}
			},3,3);
		}
	};
}
