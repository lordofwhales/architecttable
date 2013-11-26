package mods.architecttable;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class ATUtil {
	public static boolean deepNBTEqual(ItemStack a, ItemStack b) {
		return deepNBTEqual(a.stackTagCompound, b.stackTagCompound);
	}
	public static boolean deepNBTEqual(NBTBase a, NBTBase b) {
		if (a==null && b==null)
			return true;
		if (a==null || b==null || !a.equals(b))
			return false;
		if (a instanceof NBTTagCompound && b instanceof NBTTagCompound) {
			NBTTagCompound ac = (NBTTagCompound)a, bc = (NBTTagCompound)b;
			if(!ac.equals(bc))
				return false;
			for(NBTBase tag : (Collection<NBTBase>)ac.getTags()) {
				String name = tag.getName();
				if (!deepNBTEqual(ac.getTag(name), bc.getTag(name)))
					return false;
			}
		}
		return true;
	}

	public static boolean isUnwritBlueprint(ItemStack print) {
		return isBlueprint(print) && print.getItemDamage()==0;
	}

	public static boolean isWritBlueprint(ItemStack print) {
		return isBlueprint(print) && print.getItemDamage()==1;
	}
	
	public static boolean isBlueprint(ItemStack print) {
		return print != null && print.itemID == ArchitectTable.blueprint.itemID;
	}
}
