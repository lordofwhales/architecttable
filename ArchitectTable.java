package mods.ArchitectTable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid="robertwan_architecttable", name="Architect Table", version="1.0.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class ArchitectTable {
	@Instance
	public static ArchitectTable instance;
	
	@SidedProxy(clientSide="mods.ArchitectTable.client.ClientProxy", serverSide="mods.ArchitectTable.CommonProxy")
	public static CommonProxy proxy;
	
	public int renderID;
	public static Block table;
	public static Item blueprint, clipboard;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent e) {
		Configuration c = new Configuration(e.getSuggestedConfigurationFile());
		int idTable		= c.getBlock("architectTable", 	700).getInt(),
			idBlueprint	= c.getItem("blueprint", 9335).getInt(),
			idClipboard	= c.getItem("clipboard", 9336).getInt();
		table = new ArchitectTableBlock(idTable);
		blueprint = new ItemBlueprint(idBlueprint);
		clipboard = new ItemClipboard(idClipboard);
		
		renderID = RenderingRegistry.getNextAvailableRenderId();
		proxy.registerRenders();
	}
	
	@Init
	public void init(FMLInitializationEvent e) {
		GameRegistry.registerBlock(table, "Architect Table");
		GameRegistry.registerTileEntity(TileEntityArchitectTable.class, "robertwanATTileEntity");
		
		LanguageRegistry.addName(table,"Architect Table");
		LanguageRegistry.addName(new ItemStack(blueprint,1,0),"Blank Blueprint");
		LanguageRegistry.addName(new ItemStack(blueprint,1,1),"Blueprint");
		LanguageRegistry.addName(clipboard,"Clipboard");
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		NetworkRegistry.instance().registerChannel(new PacketHandler(), "ATBlueprint");
		
		GameRegistry.addRecipe(new ItemStack(table), "ccc","wtw","whw",
				'c',new ItemStack(Block.cobblestone),
				'w',new ItemStack(Block.wood.blockID,1,-1),
				't',new ItemStack(Block.workbench),
				'h',new ItemStack(Block.chest));
		GameRegistry.addRecipe(new ItemStack(blueprint,8,0), "ppp","plp","ppp",
				'p',new ItemStack(Item.paper),
				'l',new ItemStack(Item.dyePowder,1,4));
		GameRegistry.addRecipe(new ItemStack(clipboard,1,0), " g ","ipi","ici",
				'g',new ItemStack(Item.ingotGold),
				'i',new ItemStack(Item.ingotIron),
				'p',new ItemStack(Item.paper),
				'c',new ItemStack(Block.workbench));
		GameRegistry.addShapelessRecipe(new ItemStack(blueprint,1,0), new ItemStack(blueprint,1,1));
	}
}
