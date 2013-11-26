package mods.architecttable.table;

import java.util.Random;

import mods.architecttable.ArchitectTable;
import mods.architecttable.client.ClientProxy;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ArchitectTableBlock extends BlockContainer {
	private static Icon[] icons = new Icon[3];
	public ArchitectTableBlock(int id) {
		super(id, Material.wood);
		setHardness(3.0f);
		setUnlocalizedName("robertwanArchitectTable");
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	@Override public void registerIcons(IconRegister ir) {
		icons[0] = ir.registerIcon(ClientProxy.PREFIX + ":table_bottom");
		icons[1] = ir.registerIcon(ClientProxy.PREFIX + ":table_top");
		icons[2] = ir.registerIcon(ClientProxy.PREFIX + ":table_side");
	}
	
	@Override public Icon getIcon(int side, int meta) {
		switch(side) {
			case 0: case 1: return icons[side];
			default: return icons[2];
		}
	}
	
	@Override public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer player,
	int why, float are, float these, float here) {
		TileEntity te = w.getBlockTileEntity(x,y,z);
		if(te==null || player.isSneaking())
			return false;
        player.openGui(ArchitectTable.instance, 0, w, x, y, z);
		return true;
    }
	
	public void breakBlock(World w, int x, int y, int z, int useless, int variable) {
		dropItems(w, x, y, z);
		super.breakBlock(w, x, y, z, useless, variable);
	} private void dropItems(World world, int x, int y, int z) {
		Random r = new Random();
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(!(te instanceof IInventory))
			return;
		IInventory inv = (IInventory)te;
		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null && stack.stackSize > 0){
				float dx = r.nextFloat() * 0.8F + 0.1F;
				float dy = r.nextFloat() * 0.8F + 0.1F;
				float dz = r.nextFloat() * 0.8F + 0.1F;
				EntityItem ei = new EntityItem(world, x+dx, y+dy, z+dz,
						new ItemStack(stack.itemID, stack.stackSize, stack.getItemDamage()));
				if(stack.hasTagCompound())
					ei.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
				float factor = 0.05f;
				ei.motionX = r.nextGaussian() * factor;
				ei.motionY = r.nextGaussian() * factor + 0.2F;
				ei.motionZ = r.nextGaussian() * factor;
				if(!world.isRemote)
					world.spawnEntityInWorld(ei);
				stack.stackSize = 0;
			}
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityArchitectTable();
	}
	
	@Override public boolean renderAsNormalBlock() {
		return true;
	}

}
