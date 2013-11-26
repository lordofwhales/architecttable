package mods.architecttable.table;

import static mods.architecttable.table.TileEntityArchitectTable.FIRST_CRAFTING_SLOT;
import static mods.architecttable.table.TileEntityArchitectTable.FIRST_INTERNAL_SLOT;
import static mods.architecttable.table.TileEntityArchitectTable.FIRST_PLAYER_SLOT;
import static mods.architecttable.table.TileEntityArchitectTable.LAST_CRAFTING_SLOT;
import static mods.architecttable.table.TileEntityArchitectTable.LAST_HOTBAR_SLOT;
import static mods.architecttable.table.TileEntityArchitectTable.LAST_INTERNAL_SLOT;
import static mods.architecttable.table.TileEntityArchitectTable.OUTPUT_SLOT;
import mods.architecttable.ATUtil;
import mods.architecttable.SingleItemSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerArchitectTable extends Container {
	protected TileEntityArchitectTable tileEntity;

	public IInventory craftSupply;
	public IInventory craftResult;

	public ContainerArchitectTable(InventoryPlayer inventory, TileEntityArchitectTable te) {
		tileEntity = te;
		craftSupply = tileEntity.craftSupply;
		craftResult = tileEntity.craftResult;
		
		layoutContainer(inventory, tileEntity);
		SlotATCrafting foo = (new SlotATCrafting(this, te, inventory.player, tileEntity, craftResult,
				tileEntity, OUTPUT_SLOT, 124, 35));
		addSlotToContainer(foo);
		bindPlayerInventory(inventory);
		updateCraftingResults();
		
		te.slot = foo;		
	}

	private void layoutContainer(InventoryPlayer inventory, TileEntityArchitectTable te) {
		for(int r=0; r<3; r++)
			for(int c=0; c<3; c++)
				addSlotToContainer(new Slot(tileEntity, r*3+c, 30 + c * 18, 17 + r * 18)); //slots 0-8
		
		for(int r=0; r<2; r++)
			for(int c=0; c<9; c++)
				addSlotToContainer(new Slot(tileEntity, 9*(r+1)+c, 8+c*18, 77+r*19)); //slots 9 - 26
		
		addSlotToContainer(new SingleItemSlot(tileEntity,27,89,22) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return ATUtil.isBlueprint(stack);
			}
		});
	}
	
	private void bindPlayerInventory(InventoryPlayer invPlayer) {
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(invPlayer, 9+j+i*9, 8+j*18, 84+i*18+37));
		
		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(invPlayer, i, 8+i*18, 142+37));
	}

	private void updateCraftingResults() {
		craftResult.setInventorySlotContents(0, tileEntity.findRecipe());
	}
	
	@Override public ItemStack slotClick(int slot, int button, int par, EntityPlayer player) {
		ItemStack out = super.slotClick(slot, button, par, player);
		onCraftMatrixChanged(tileEntity);
		if (slot < FIRST_PLAYER_SLOT) 
			updateCraftingResults();
		return out;
	}

	@Override public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}

	@Override public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);
        ItemStack result = slot.getStack();
        while (slot != null && slot.getHasStack()) {
            ItemStack stack2 = slot.getStack();
            if(!stack2.isItemEqual(result)) break;
            stack = stack2.copy();
            
            if (!okayMerge(slotIndex, stack2))
            	return null;

            if (stack2.stackSize == 0) {
                slot.onSlotChange(stack2, stack);
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (stack2.stackSize == stack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, stack2);
            updateCraftingResults();
        }

        return stack;
    }
	private boolean okayMerge(int slot, ItemStack stack) {
		return	
			// Send result to supply (first) or player inventory (second) or hotbar (last)
	        this.mergeFrom(slot, stack, OUTPUT_SLOT, OUTPUT_SLOT, FIRST_INTERNAL_SLOT, LAST_INTERNAL_SLOT, false) ||
	        this.mergeFrom(slot, stack, OUTPUT_SLOT, OUTPUT_SLOT, FIRST_PLAYER_SLOT, LAST_HOTBAR_SLOT, true) ||
	     
	        // Send crafting slots to supply (first) or player inventory (second)
	        this.mergeFrom(slot, stack, FIRST_CRAFTING_SLOT, LAST_CRAFTING_SLOT,
	        		FIRST_INTERNAL_SLOT, LAST_INTERNAL_SLOT, false) ||
	        this.mergeFrom(slot, stack, FIRST_CRAFTING_SLOT, LAST_CRAFTING_SLOT,
	        		FIRST_PLAYER_SLOT, LAST_HOTBAR_SLOT, false) ||
	                    
	        // Send supply slots to player inventory
	        this.mergeFrom(slot, stack, FIRST_INTERNAL_SLOT, LAST_INTERNAL_SLOT,
	        		FIRST_PLAYER_SLOT, LAST_HOTBAR_SLOT, false) ||
	        
	        // Send player inventory to supply
	        this.mergeFrom(slot, stack, FIRST_PLAYER_SLOT, LAST_HOTBAR_SLOT,
	        		FIRST_INTERNAL_SLOT, LAST_INTERNAL_SLOT, false)
        ;
	}
	
    private boolean mergeFrom(int slotIndex, ItemStack stack, int firstFrom, int lastFrom, 
    		int firstTo, int lastTo, boolean backwards) {
    	if(slotIndex >= firstFrom && slotIndex <= lastFrom)
    		return this.mergeItemStack(stack, firstTo, lastTo + 1, backwards);
    	return false;
    }
}
