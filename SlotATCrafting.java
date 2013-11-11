package mods.ArchitectTable;

import java.util.ArrayList;
import java.util.HashMap;

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
	
	public void decreaseItems(TileEntityArchitectTable tileEntity) {
		boolean found = false;
		boolean hasMetadata = false;
        ArrayList<ItemStack> blueprintStacks = new ArrayList<ItemStack>();
        for(int i=0; i<9; i++) blueprintStacks.add(null);
        if(tileEntity.isValidBlueprint(tileEntity.getStackInSlot(27))) {
        	NBTTagList items = (NBTTagList)tileEntity.getStackInSlot(27).stackTagCompound.getTag("blueprint");
        	for(int i=0; i<tileEntity.getStackInSlot(27).stackTagCompound.getInteger("length"); i++) {
        		NBTTagCompound item = (NBTTagCompound)items.tagAt(i);
        		blueprintStacks.set(item.getByte("slot"), ItemStack.loadItemStackFromNBT(item));
        	}
        }
        
        ArrayList<ItemStack> components;
        for(int i = 0; i < 9; i++) {
        	found = false;
        	ItemStack craftStack = blueprintStacks.get(i);
        	if(craftStack==null) craftStack = craftSupply.getStackInSlot(i);
        	components = new ArrayList<ItemStack>();
        	
        	if(TileEntityArchitectTable.isWritBlueprint(craftStack) && !tileEntity.findRecipe().isItemEqual(new ItemStack(ArchitectTable.blueprint,1,0))) {
        		HashMap<ItemStack, Integer> req = TileEntityArchitectTable.getBlueprintRequirements(craftStack);
        		for(ItemStack key : req.keySet())
        			for(int j=0; j<req.get(key); j++) components.add(key);
        	} else components.add(craftStack);
        	
        	for(ItemStack craftComponentStack : components) {
        		found = false;
            	hasMetadata = false;
	        	if(craftComponentStack != null) {
	        		if(!craftComponentStack.isItemStackDamageable() && craftComponentStack.getMaxDamage() == 0
		        	&& OreDictionary.getOreID(craftComponentStack)==-1) {
						hasMetadata = true;
					}
	        		
	    			for(int supplyInv = 9; supplyInv < 27; supplyInv++) {
		    			ItemStack supplyMatrixStack = craftSupply.getStackInSlot(supplyInv);
		    			if(supplyMatrixStack != null) {
		    				if(supplyMatrixStack.itemID == craftComponentStack.itemID) {
		    					if(hasMetadata) {
		    						if(craftComponentStack.getItemDamage() != supplyMatrixStack.getItemDamage()) {
		    							continue;
		    						} else {
		    							craftSupply.decrStackSize(supplyInv, 1);
		            					found = true;
		    						}
		    					} else {
		        					craftSupply.decrStackSize(supplyInv, 1);
		        					found = true;
		    					}
		    					
		    					if (supplyMatrixStack.getItem().hasContainerItem()) {
				                    ItemStack contStack = supplyMatrixStack.getItem().getContainerItemStack(supplyMatrixStack);
				                    
				                    if (contStack.isItemStackDamageable() && contStack.getItemDamage() > contStack.getMaxDamage()) {
				                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(this.player, contStack));
				                        contStack = null;
				                    }
		
				                    if (contStack != null && (!supplyMatrixStack.getItem().doesContainerItemLeaveCraftingGrid(supplyMatrixStack) || !this.player.inventory.addItemStackToInventory(contStack))) {
				                        if (this.craftMatrix.getStackInSlot(supplyInv) == null) {
				                            this.craftMatrix.setInventorySlotContents(supplyInv, contStack);
				                        } else {
				                            this.player.dropPlayerItem(contStack);
				                        }
				                    }
				                }
		    					break;
		    				}
		    			}
		    		}
	        		
	        		if(!found) {
	        			craftSupply.decrStackSize(i, 1);
	        			if (craftComponentStack.getItem().hasContainerItem()) {
	                        ItemStack conStack = craftComponentStack.getItem().getContainerItemStack(craftComponentStack);
	                        
	                        if (conStack.isItemStackDamageable() && conStack.getItemDamage() > conStack.getMaxDamage()) {
	                            MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, conStack));
	                            conStack = null;
	                        }
	
	                        if (conStack != null && (!craftComponentStack.getItem().doesContainerItemLeaveCraftingGrid(craftComponentStack) || !this.player.inventory.addItemStackToInventory(conStack))) {
	                            if (craftMatrix.getStackInSlot(i) == null) {
	                                craftMatrix.setInventorySlotContents(i, conStack);
	                            } else {
	                                player.dropPlayerItem(conStack);
	                            }
	                        }
	                    }
	        		}
	        	}
	        }
	    }
	}
}
