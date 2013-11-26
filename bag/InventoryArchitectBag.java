package mods.architecttable.bag;

import static mods.architecttable.bag.InventoryArchitectBag.FIRST_BAG_SLOT;
import static mods.architecttable.bag.InventoryArchitectBag.FIRST_BLUEPRINT_SLOT;
import static mods.architecttable.bag.InventoryArchitectBag.LAST_BAG_SLOT;
import static mods.architecttable.bag.InventoryArchitectBag.LAST_BLUEPRINT_SLOT;

import java.util.Collection;
import java.util.LinkedList;

import mods.architecttable.ATUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryArchitectBag implements IInventory {
	private ItemStack[] inventory;
	private ItemStack bag;
	private int rows;
	public static int
		FIRST_BLUEPRINT_SLOT = 0,
		LAST_BLUEPRINT_SLOT = 2,
		FIRST_BAG_SLOT = 3,
		LAST_BAG_SLOT = 47,
		FIRST_PLAYER_SLOT = 48,
		LAST_PLAYER_SLOT = 74,
		FIRST_HOTBAR_SLOT = 75,
		LAST_HOTBAR_SLOT = 83;
	
	public InventoryArchitectBag(int rows, ItemStack bag) {
		this.rows = rows;
		this.inventory = new ItemStack[getSizeInventory()];
		if (bag.stackTagCompound == null)
			bag.stackTagCompound = new NBTTagCompound();
		if (!bag.stackTagCompound.hasKey("archbagItems"))
			saveInventoryToItemStack(bag);
		
		readItemsFromNBT(bag.stackTagCompound.getTagList("archbagItems"));
	}
	
	private void readItemsFromNBT(NBTTagList itemList) {
		inventory = new ItemStack[getSizeInventory()];
		NBTTagCompound item;
		for (int i = 0; i < itemList.tagCount(); i++) {
			item = (NBTTagCompound)(itemList.tagAt(i));
			byte slot = item.getByte("slot");
			
			if (slot >= 0 && slot < getSizeInventory())
				this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
		}
	}
	
	protected void saveInventoryToItemStack(ItemStack heldStack) {
		if (heldStack == null) 
			return;
		
		NBTTagList items = new NBTTagList();
		NBTTagCompound item;
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null) {
				item = new NBTTagCompound();
				item.setByte("slot", (byte)(i));
				inventory[i].writeToNBT(item);
				items.appendTag(item);
			}
		}
		
		if (heldStack.stackTagCompound == null)
			heldStack.stackTagCompound = new NBTTagCompound();
		
		heldStack.stackTagCompound.setTag("archbagItems", items);
	}
	
	public Collection<ItemStack> getSlurpStacks() {
		Collection<ItemStack> out = new LinkedList<ItemStack>();
		for (int i = FIRST_BLUEPRINT_SLOT; i <= LAST_BLUEPRINT_SLOT; i++) {
			ItemStack stack = this.getStackInSlot(i);
			if (stack != null)
				out.add(stack);
		}
		return out;
	}
	
	public void slurp(IInventory inventory, int slot) {
		Collection<ItemStack> slurpStacks = getSlurpStacks();
		if (slurpStacks.isEmpty())
			return;
		
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (i == slot)
				continue;
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null)
				continue;
			for (ItemStack slurp : this.getSlurpStacks()) {
				if (slurp.isItemEqual(stack) && ATUtil.deepNBTEqual(slurp, stack)) {
					if (mergeToHere(stack)) {
						if (inventory.getStackInSlot(i).stackSize < 1)
							inventory.setInventorySlotContents(i, null);
						inventory.onInventoryChanged();
						this.onInventoryChanged();
						this.saveInventoryToItemStack(inventory.getStackInSlot(slot));
					}
					return;
				}
			}
		}
	}
	
	public boolean mergeToHere(ItemStack stack) {
		for (int i = FIRST_BAG_SLOT; i <= LAST_BAG_SLOT; i++) {
			ItemStack cur = this.getStackInSlot(i);
			if (cur == null) {
				this.setInventorySlotContents(i, stack);
				return true;
			}
			
			int diff = cur.getMaxStackSize() - cur.stackSize;
			if (cur.isItemEqual(stack) && diff > 0) {
				diff = Math.min(diff, stack.stackSize);
				stack.stackSize -= diff;
				cur.stackSize += diff;
				return true;
			}
		}
		return false;
	}
	

	@Override
	public int getSizeInventory() {
		return ItemArchitectBag.ROWS * 9 + 3;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (i < 0 || i >= getSizeInventory()) {
			System.err.println("Invalid argument to InventoryArchitectBag.getStackInSlot(int).  Received: "+i);
			return null;
		}
		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		if (inventory[slot] == null) 
			return null;
		ItemStack out;
		if (inventory[slot].stackSize <= amt)
			out = inventory[slot];
		else
			out = inventory[slot].splitStack(amt);
		
		if (inventory[slot].stackSize <= amt)
			inventory[slot] = null;
		
		return out;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack out = inventory[slot];
		inventory[slot] = null;
		return out;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;
		
		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
			stack.stackSize = this.getInventoryStackLimit();
	}

	@Override
	public String getInvName() {
		return "robertwan.architectbackpack";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override public void onInventoryChanged() {}
	@Override public void openChest() {}
	@Override public void closeChest() {}
}
