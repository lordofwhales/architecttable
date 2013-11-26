package mods.architecttable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import mods.architecttable.table.TileEntityArchitectTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data));
		int x=0, y=0, z=0;
		try {
			x = in.readInt();
			y = in.readInt();
			z = in.readInt();
		} catch(Exception e) { e.printStackTrace(); }
		TileEntity foo = ((EntityPlayer)player).worldObj.getBlockTileEntity(x, y, z);
		if(foo==null || !(foo instanceof TileEntityArchitectTable)) return;
		((TileEntityArchitectTable)foo).write();
	}

}
