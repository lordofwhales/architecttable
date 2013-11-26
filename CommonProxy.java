package mods.architecttable;

import mods.architecttable.bag.ContainerArchitectBackpack;
import mods.architecttable.bag.ItemArchitectBag;
import mods.architecttable.client.ArchitectBackpackGUI;
import mods.architecttable.client.ArchitectTableGUI;
import mods.architecttable.client.ClipboardGUI;
import mods.architecttable.table.ContainerArchitectTable;
import mods.architecttable.table.TileEntityArchitectTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {
	public void registerRenders() {
		//server doesn't care
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x,y,z);
		switch(id) {
			case 0:
				if(te instanceof TileEntityArchitectTable)
					return new ContainerArchitectTable(player.inventory,(TileEntityArchitectTable)te);
				else return null;
			case 1: 
				return new ContainerWorkbench(player.inventory, world, x, y, z) {
					@Override public boolean canInteractWith(EntityPlayer player) {
						return true;
					}
				};
			case 2:
				return new ContainerArchitectBackpack(player.inventory, ItemArchitectBag.getInventory(player));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch(id) {
			case 0: 
				return new ArchitectTableGUI(player.inventory,(TileEntityArchitectTable)world.getBlockTileEntity(x,y,z));
			case 1:
				return new ClipboardGUI(player.inventory, world, x, y, z);
			case 2:
				if (player.getHeldItem().getItem() instanceof ItemArchitectBag)
					return new ArchitectBackpackGUI(player);
				else return null;
		} return null;
	}
}
