package mods.architecttable.client;

import mods.architecttable.CommonProxy;
import net.minecraft.util.ResourceLocation;

public class ClientProxy extends CommonProxy {
	public static final String PREFIX = "architect";
	public static final ResourceLocation 
			AT_GUI = new ResourceLocation(PREFIX, "textures/gui/at_gui.png"),
			CLIPBOARD_GUI = new ResourceLocation("textures/gui/container/crafting_table.png"),
			AB_GUI = new ResourceLocation(PREFIX, "textures/gui/ab_gui.png");

}
