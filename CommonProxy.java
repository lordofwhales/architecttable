package mods.ArchitectTable;

import mods.ArchitectTable.client.ArchitectTableGUI;
import mods.ArchitectTable.client.ClipboardGUI;
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
			case 1: 
				return new ContainerWorkbench(player.inventory, world, x, y, z) {
					@Override public boolean canInteractWith(EntityPlayer player) {
						return true;
					}
				};
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
		} return null;
	}
}
