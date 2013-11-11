package mods.ArchitectTable.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import mods.ArchitectTable.ContainerArchitectTable;
import mods.ArchitectTable.TileEntityArchitectTable;
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
	
	/*@Override public void initGui() {
		super.initGui();
		buttonList.add(new GuiButton(0, 108, 20, 11, 11, " W "));
	}
	@Override protected void actionPerformed(GuiButton button) {
		System.out.println("Button");
		tileEntity.write = true;
	}*/
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
		if(button==0) {
			int nx = x - (width-xSize)/2,
				ny = y - (height-ySize)/2;
			if(108<=nx && nx<=117 && 21<=ny && ny<=30) {
				this.drawTexturedModalRect((width-xSize)/2+108, (height-ySize)/2+21, 176, 0, 10, 10);
				ByteArrayOutputStream bos = new ByteArrayOutputStream(12);
				DataOutputStream dos = new DataOutputStream(bos);
				try {
					dos.writeInt(tileEntity.xCoord);
					dos.writeInt(tileEntity.yCoord);
					dos.writeInt(tileEntity.zCoord);
				} catch(Exception e) { e.printStackTrace(); }
				Packet250CustomPayload packet = new Packet250CustomPayload();
				packet.channel = "ATBlueprint";
				packet.data = bos.toByteArray();
				packet.length = bos.size();
				PacketDispatcher.sendPacketToServer(packet);
			}
		}
		super.mouseClicked(x, y, button);
    }
	public void drawOkayItemStack(ItemStack stack, int slot) {
		
	}
	private void drawOkayItemStack(ItemStack stack, int x, int y, float clear) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, clear);
        this.zLevel = 200.0F;
        itemRenderer.zLevel = 200.0F;
        itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, stack, x, y);
        itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, stack, x, y);
        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
    }

}
