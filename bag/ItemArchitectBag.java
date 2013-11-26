package mods.architecttable.bag;

import mods.architecttable.ArchitectTable;
import mods.architecttable.client.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemArchitectBag extends Item {
	public static final int ROWS = 5;
	public ItemArchitectBag(int id) {
		super(id);
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setUnlocalizedName("Architect's Backpack");
	}
	
	@Override
	public void registerIcons(IconRegister ir) {
		this.itemIcon = ir.registerIcon(ClientProxy.PREFIX+"bag");
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack thisStack, World world, EntityPlayer player) {
		int x = (int) player.posX,
			y = (int) player.posY,
			z = (int) player.posZ;
		player.openGui(ArchitectTable.instance, 2, world, x, y, z);
		return thisStack;
	}
	
	@Override
	public void onUpdate(ItemStack thisStack, World world, Entity holder, int slot, boolean held) {
		if (!(holder instanceof EntityPlayer) || world.isRemote)
			return;
		
		EntityPlayer player = (EntityPlayer)holder;
		getInventory(thisStack).slurp(player.inventory, slot);
	}
	
	public static InventoryArchitectBag getInventory(EntityPlayer player) {
		ItemStack held = player.getHeldItem();
		return getInventory(held);
	}
	
	public static InventoryArchitectBag getInventory(ItemStack held) {
		InventoryArchitectBag out = null;
		if (held != null && held.getItem() instanceof ItemArchitectBag)
			out = new InventoryArchitectBag(ItemArchitectBag.ROWS, held);
		if (out == null) 
			System.err.println("Bag of holding is null!");
		return out;
	}
}
