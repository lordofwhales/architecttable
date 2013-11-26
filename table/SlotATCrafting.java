package mods.architecttable.table;

import static mods.architecttable.table.TileEntityArchitectTable.BLUEPRINT_SLOT;

import java.util.ArrayList;
import java.util.HashMap;

import mods.architecttable.ATUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;

public class SlotATCrafting extends SlotCrafting {
	private EntityPlayer player;
	private final IInventory craftMatrix;
	private IInventory craftResult;
	
	private IInventory craftSupply;
	private TileEntityArchitectTable tileEntity;

	public SlotATCrafting(Container parent, TileEntityArchitectTable te, EntityPlayer player, IInventory craftingMatrix, 
	IInventory craftingResult, IInventory craftingSupply, int slotID, int x, int y) {
		super(player, craftingMatrix, craftingResult, slotID, x, y);
		this.player = player;
		craftMatrix = craftingMatrix;
		craftResult = craftingResult;
		craftSupply = craftingSupply;
		tileEntity = te;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        GameRegistry.onItemCrafted(player, stack, craftMatrix);
        this.onCrafting(stack);
        decreaseItems(tileEntity);
	}
	
	public void decreaseItems(TileEntityArchitectTable tile) {
		boolean found = false;
		boolean hasMetadata = false;
		boolean hasNBT = false;
        ArrayList<ItemStack> blueprintStacks = new ArrayList<ItemStack>();
        for (int i = 0; i < 9; i++)
        	blueprintStacks.add(null);
        
        ItemStack print = tile.getStackInSlot(BLUEPRINT_SLOT);
        if (tile.isValidBlueprint(print)) {
        	NBTTagCompound printTags = print.stackTagCompound;
        	NBTTagList items = (NBTTagList)printTags.getTag("blueprint");
        	for (int i = 0; i < printTags.getInteger("length"); i++) {
        		NBTTagCompound item = (NBTTagCompound)items.tagAt(i);
        		blueprintStacks.set(item.getByte("slot"), ItemStack.loadItemStackFromNBT(item));
        	}
        }
        
        ArrayList<ItemStack> components;
        for (int i = 0; i < 9; i++) {
        	found = false;
        	ItemStack craftStack = blueprintStacks.get(i);
        	if(craftStack==null)
        		craftStack = craftSupply.getStackInSlot(i);
        	components = new ArrayList<ItemStack>();
        	
        	if(ATUtil.isWritBlueprint(craftStack) && !ATUtil.isUnwritBlueprint(tile.findRecipe())) {
        		HashMap<ItemStack, Integer> req = TileEntityArchitectTable.getBlueprintRequirements(craftStack);
        		for (ItemStack key : req.keySet())
        			for (int j = 0; j < req.get(key); j++)
        				components.add(key);
        	} else components.add(craftStack);
        	
        	for(ItemStack craftComponent : components) {
        		found = hasMetadata = hasNBT = false;
	        	if(craftComponent == null)
	        		continue;
	        	
        		if(!craftComponent.isItemStackDamageable() && craftComponent.getMaxDamage() == 0
	        	&& OreDictionary.getOreID(craftComponent)==-1) {
					hasMetadata = true;
				}
        		
        		if (craftComponent.hasTagCompound())
        			hasNBT = true;
        		
    			for(int supplySlot = 9; supplySlot < 27; supplySlot++) {
	    			ItemStack supplyStack = craftSupply.getStackInSlot(supplySlot);
	    			if(supplyStack == null || supplyStack.itemID != craftComponent.itemID) 
	    				continue;
	    			
					if(hasMetadata && !craftComponent.isItemEqual(supplyStack)) {
						continue;
					} else if(hasNBT && !ATUtil.deepNBTEqual(supplyStack, craftComponent)) {
						continue;
					} else {
    					craftSupply.decrStackSize(supplySlot, 1);
    					found = true;
					}
					
					if (supplyStack.getItem().hasContainerItem()) {
	                    ItemStack container = supplyStack.getItem().getContainerItemStack(supplyStack);
	                    handleContainerItem(supplySlot, supplyStack, container);
	                }
					
					break;
	    		}
        		
        		if(!found) {
        			craftSupply.decrStackSize(i, 1);
        			if (craftComponent.getItem().hasContainerItem()) {
                        ItemStack conStack = craftComponent.getItem().getContainerItemStack(craftComponent);
                        handleContainerItem(i, craftComponent, conStack);
                    }
        		}
	        }
	    }
	}
	
	public void handleContainerItem(int slot, ItemStack stack, ItemStack container) {
		if (container.isItemStackDamageable() && container.getItemDamage() > container.getMaxDamage()) {
            MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, container));
            container = null;
        }

        if (container != null && (!stack.getItem().doesContainerItemLeaveCraftingGrid(stack) || !this.player.inventory.addItemStackToInventory(container))) {
            if (craftMatrix.getStackInSlot(slot) == null) {
                craftMatrix.setInventorySlotContents(slot, container);
            } else {
                player.dropPlayerItem(container);
            }
        }
	}
}