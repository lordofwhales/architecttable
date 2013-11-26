package mods.architecttable.bag;

import static mods.architecttable.bag.InventoryArchitectBag.FIRST_BAG_SLOT;
import static mods.architecttable.bag.InventoryArchitectBag.FIRST_BLUEPRINT_SLOT;
import static mods.architecttable.bag.InventoryArchitectBag.FIRST_PLAYER_SLOT;
import static mods.architecttable.bag.InventoryArchitectBag.LAST_BAG_SLOT;
import static mods.architecttable.bag.InventoryArchitectBag.LAST_BLUEPRINT_SLOT;
import static mods.architecttable.bag.InventoryArchitectBag.LAST_HOTBAR_SLOT;

import java.util.Collection;
import java.util.LinkedList;

import mods.architecttable.ATUtil;
import mods.architecttable.SingleItemSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerArchitectBackpack extends Container {
	private InventoryPlayer playerInv;
	private InventoryArchitectBag bagInv;
	public ContainerArchitectBackpack(InventoryPlayer playerInv, InventoryArchitectBag bagInv) {
		this.playerInv = playerInv;
		this.bagInv = bagInv;
		
		layoutContainer();
		layoutPlayerInventory();
	}
	
	private void layoutContainer() {
		for (int c = 0; c < 3; c++)
			addSlotToContainer(new SingleItemSlot(bagInv, c, 62 + 18 * c, 18));
		
		for (int r = 0; r < ItemArchitectBag.ROWS; r++)
			for (int c = 0; c < 9; c++)
				this.addSlotToContainer(new Slot(bagInv, 3 + r * 9 + c, 8 + 18 * c, 40 + 18 * r));
	}
	
	private void layoutPlayerInventory() {
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 9; c++)
				this.addSlotToContainer(new Slot(playerInv, 9 + r * 9 + c, 8 + 18 * c, 137 + 18 * r));
		
		for (int c = 0; c < 9; c++) {
			this.addSlotToContainer(new Slot(playerInv, c, 8 + 18 * c, 195) {
				@Override
				public boolean canTakeStack(EntityPlayer player) {
					return this.getSlotIndex() != player.inventory.currentItem;
				}
			});
		}
	}
	@Override
	public void onContainerClosed(EntityPlayer player) {
		this.bagInv.saveInventoryToItemStack(player.inventory.getCurrentItem());
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);
        ItemStack result = slot.getStack();
        
        while (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            if(!slotStack.isItemEqual(result)) break;
            stack = slotStack.copy();
            
            if (!okayMerge(slotIndex, slotStack))
            	return null;

            if (slotStack.stackSize == 0) {
                slot.onSlotChange(slotStack, stack);
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.stackSize == stack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, slotStack);
        }

        return stack;
    }
	
	private boolean okayMerge(int slot, ItemStack stack) {
		return	
			// Send blueprint slots to bag slots (first) or player slots (second)
	        this.mergeFrom(slot, stack, FIRST_BLUEPRINT_SLOT, LAST_BLUEPRINT_SLOT, 
	        		FIRST_BAG_SLOT, LAST_BAG_SLOT, false) ||
	        this.mergeFrom(slot, stack, FIRST_BLUEPRINT_SLOT, LAST_BLUEPRINT_SLOT,
	        		FIRST_PLAYER_SLOT, LAST_HOTBAR_SLOT, true) ||
	     
	        // Send bag slots to player slots
	        this.mergeFrom(slot, stack, FIRST_BAG_SLOT, LAST_BAG_SLOT,
	        		FIRST_PLAYER_SLOT, LAST_HOTBAR_SLOT, false) ||
	        
	        // Send player slots to bag slots
	        this.mergeFrom(slot, stack, FIRST_PLAYER_SLOT, LAST_HOTBAR_SLOT,
	        		FIRST_BAG_SLOT, LAST_BAG_SLOT, false)
        ;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}
	
	private boolean mergeFrom(int slotIndex, ItemStack stack, int firstFrom, int lastFrom, 
    		int firstTo, int lastTo, boolean backwards) {
    	if(slotIndex >= firstFrom && slotIndex <= lastFrom)
    		return this.mergeItemStack(stack, firstTo, lastTo + 1, backwards);
    	return false;
    }
}
