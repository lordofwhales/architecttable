package mods.architecttable.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import mods.architecttable.IntPacket;
import mods.architecttable.table.ContainerArchitectTable;
import mods.architecttable.table.TileEntityArchitectTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ArchitectTableGUI extends GuiContainer {
	private TileEntityArchitectTable tileEntity;
	public ArchitectTableGUI(InventoryPlayer inventory,	TileEntityArchitectTable blockTileEntity) {
		super(new ContainerArchitectTable(inventory,blockTileEntity));
		this.tileEntity = blockTileEntity;
		ySize+=40;
	}
	
	@Override protected void drawGuiContainerForegroundLayer(int useless,int vars) {
		fontRenderer.drawString("Architect Table", 6, 6, 0x404040);
	}
	
	@Override protected void drawGuiContainerBackgroundLayer(float more, int useless, int vars) {
		GL11.glColor4f(1f, 1f, 1f, 1f);
        this.mc.renderEngine.bindTexture(ClientProxy.AT_GUI);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	@Override protected void mouseClicked(int x, int y, int button) {
		int nx = x - (width-xSize) / 2,
			ny = y - (height-ySize) / 2;
		if(108<=nx && nx<=117 && 21<=ny && ny<=30) {
			this.drawTexturedModalRect((width-xSize)/2+108, (height-ySize)/2+21, 176, 0, 10, 10);
			int tx = tileEntity.xCoord, ty = tileEntity.yCoord, tz = tileEntity.zCoord;
			IntPacket.createAndSendWithData("ATBlueprint", tx, ty, tz);
		}
		
		super.mouseClicked(x, y, button);
    }
}
