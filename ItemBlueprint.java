package mods.architecttable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;

import mods.architecttable.client.ClientProxy;
import mods.architecttable.table.TileEntityArchitectTable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.oredict.OreDictionary;

public class ItemBlueprint extends Item {
	public Icon[] icons = new Icon[2];
	public ItemBlueprint(int id) {
		super(id);
		this.setCreativeTab(CreativeTabs.tabMisc);
		setHasSubtypes(true);
	}

	@Override public void registerIcons(IconRegister ir) {
		icons[0] = ir.registerIcon(ClientProxy.PREFIX + ":blueprint");
		icons[1] = ir.registerIcon(ClientProxy.PREFIX + ":blueprint_writ");
	}
	
	@Override public Icon getIconFromDamage(int dmg) {
		return icons[dmg];
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getItemDamage();
		switch(meta) {
			case 1:return "Blueprint";
			default:return "Blank Blueprint";
		}
	}

	public static ItemStack getOutput(ItemStack blueprint) {
		if(blueprint==null || blueprint.stackTagCompound==null) return null;
		if(!blueprint.stackTagCompound.hasKey("output")) return null;
		return ItemStack.loadItemStackFromNBT(blueprint.stackTagCompound.getCompoundTag("output"));
	}
	
	@Override
	public void addInformation(ItemStack thisStack, EntityPlayer player, List list, boolean boo) {
		if(thisStack.stackTagCompound==null) return;
		if(!thisStack.stackTagCompound.hasKey("Tooltip")) return;
		int size = getOutput(thisStack).stackSize;
		list.add(thisStack.stackTagCompound.getString("Tooltip")+(size>1?" x"+size:""));
		TreeMap<ItemStack, Integer> req = new TreeMap<ItemStack, Integer>(new Comparator<ItemStack>() {
			@Override public int compare(ItemStack a, ItemStack b) {
				int _ = a.itemID - b.itemID;
				if(_==0) return a.getItemDamage() - b.getItemDamage();
				return _;
			}
		});	req.putAll(TileEntityArchitectTable.getBlueprintRequirements(thisStack));
		for(ItemStack key : req.keySet()) {
			if(req.get(key)%size!=0) {
				list.add("* Cannot be used in crafting");
				list.add("  due to fractional requirements");
				break;
			}
		}
		for(ItemStack key : req.keySet()) {
			int id = OreDictionary.getOreID(key);
			list.add("- "+prettyNum(req.get(key))+" "+(id==-1 ? key.getDisplayName() : disp(OreDictionary.getOreName(id))));
		}
	} private String prettyNum(int n) {
		int row=0, stack=0;
		while(n>9*64) {
			row++;
			n-=9*64;
		} while(n>64) {
			stack++;
			n-=64;
		} String out = ""+n;
		if(stack>0) out=stack+"st+"+out;
		if(row>0) out=row+"ro+"+out;
		return out;
	} private String disp(String in) {
		if(repl.containsKey(in)) return repl.get(in);
		return in;
	}
	private static HashMap<String, String> repl = new HashMap<String, String>();
	static {
		repl.put("plankWood", 	"Wooden Planks");
		repl.put("logWood",		"Wooden Logs");
		repl.put("slabWood", 	"Wooden Slab");
		repl.put("stairWood",	"Wooden Stairs");
		repl.put("stickWood", 	"Stick");
        repl.put("treeSapling", "Tree Sapling");
        repl.put("treeLeaves",  "Leaves");
        repl.put("oreGold", 	"Gold Ore");
        repl.put("oreIron", 	"Iron Ore");
        repl.put("oreLapis", 	"Lapis Ore");
        repl.put("oreDiamond", 	"Diamond Ore");
        repl.put("oreRedstone",	"Redstone Ore");
        repl.put("oreEmerald", 	"Emerald Ore");
	}
}
