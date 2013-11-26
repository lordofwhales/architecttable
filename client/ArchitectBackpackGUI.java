package mods.architecttable.client;

import org.lwjgl.opengl.GL11;

import mods.architecttable.bag.ContainerArchitectBackpack;
import mods.architecttable.bag.InventoryArchitectBag;
import mods.architecttable.bag.ItemArchitectBag;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ArchitectBackpackGUI extends GuiContainer {
	InventoryPlayer playerInv;
	InventoryArchitectBag bag;
	public ArchitectBackpackGUI(EntityPlayer player) {
		super(new ContainerArchitectBackpack(player.inventory, ItemArchitectBag.getInventory(player)));
		bag = ItemArchitectBag.getInventory(player);
		playerInv = player.inventory;
		ySize += 56;
	}
	
	@Override protected void drawGuiContainerForegroundLayer(int useless,int vars) {
		fontRenderer.drawString("Architect's Backpack", 6, 6, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1f, 1f, 1f, 1f);
        this.mc.renderEngine.bindTexture(ClientProxy.AB_GUI);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
}
