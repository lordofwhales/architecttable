package mods.ArchitectTable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ContainerArchitectTable extends Container {
	protected TileEntityArchitectTable tileEntity;

	public IInventory craftSupply;
	public IInventory craftResult;
	public int craftResultSlot =28;

	public ContainerArchitectTable(InventoryPlayer inventory, TileEntityArchitectTable te) {
		tileEntity = te;
		craftSupply = tileEntity.craftSupply;
		craftResult = tileEntity.craftResult;
		
		layoutContainer(inventory, tileEntity);
		SlotATCrafting foo = (new SlotATCrafting(this, te, inventory.player, tileEntity, craftResult,
				tileEntity, craftResultSlot, 124, 35));
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
		
		addSlotToContainer(new Slot(tileEntity,27,89,22));
	}
	
	protected void bindPlayerInventory(InventoryPlayer invPlayer) {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(invPlayer, 9+j+i*9, 8+j*18, 84+i*18+37));
		}
		for(int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(invPlayer, i, 8+i*18, 142+37));
		}
	}

	private void updateCraftingResults()	{
		craftResult.setInventorySlotContents(0, tileEntity.findRecipe());
	}
	
	@Override public ItemStack slotClick(int slot, int par2, int par3, EntityPlayer player) {
		ItemStack stack = null;
		if(slot==27) {
			ItemStack moused = player.inventory.getItemStack();
			if(moused!=null) {
				if(moused.itemID != ArchitectTable.blueprint.itemID) return null;
				if(moused.stackSize==1) {
					stack = moused.copy();
					player.inventory.setItemStack(this.getSlot(27).getStack());
					this.getSlot(27).putStack(stack.copy());
				}
				if(this.getSlot(27).getHasStack()) return null;
				if(moused.stackSize!=1) par2=1;		
			}
		}
		if(stack==null) stack = super.slotClick(slot, par2, par3, player);
		onCraftMatrixChanged(tileEntity);
		updateCraftingResults();
		return stack;
	}

	@Override public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}

	@Override public ItemStack transferStackInSlot(EntityPlayer player, int numSlot) {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(numSlot);
        ItemStack result = slot.getStack();
        while (slot != null && slot.getHasStack()) {
            ItemStack stack2 = slot.getStack();
            if(!stack2.isItemEqual(result)) break;
            stack = stack2.copy();
            
            //Merge result to supply matrix (first) or player inventory (second)
            if (numSlot == 28) {
                if (!this.mergeItemStack(stack2,29,56,false) && !this.mergeItemStack(stack2, 9, 27, true) ) {
                    return null;
                }
                slot.onSlotChange(stack2, stack);
            }
            //Merge crafting matrix item with supply matrix inventory
            else if(numSlot >= 0 && numSlot < 9) {
            	if(!this.mergeItemStack(stack2, 9, 28, false))
            	{
            		return null;
            	}
            }
            //Merge Supply matrix item with player inventory
            else if (numSlot >= 9 && numSlot < 28) {
                if (!this.mergeItemStack(stack2, 29, 65, false))
                {
                    return null;
                }
            }
            //Merge player inventory item with supply matrix
            else if (numSlot >= 29 && numSlot < 65) {
                if (!this.mergeItemStack(stack2, 9, 27, false))
                {
                    return null;
                }
            }

            if (stack2.stackSize == 0) {
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
}
