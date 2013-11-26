package mods.architecttable;

import mods.architecttable.client.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemClipboard extends Item {

	public ItemClipboard(int id) {
		super(id);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override public void registerIcons(IconRegister ir) {
		itemIcon = ir.registerIcon(ClientProxy.PREFIX + ":clipboard");
	}
	
	@Override public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		player.openGui(ArchitectTable.instance, 1, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		return stack;		
	}

}
