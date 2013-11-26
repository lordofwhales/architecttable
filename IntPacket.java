package mods.architecttable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.network.packet.Packet250CustomPayload;

public class IntPacket {
	private Packet250CustomPayload packet;
	private ByteArrayOutputStream baos;
	private DataOutputStream dos;
	
	public IntPacket() {};
	public IntPacket(int... ints) {
		setData(ints);
	}
	
	public void setData(int... ints) {
		baos = new ByteArrayOutputStream(4*ints.length);
		dos = new DataOutputStream(baos);
		try {
			for(int i : ints) 
				dos.writeInt(i);
		} catch(Exception e) {
			e.printStackTrace();
		}
		packet = new Packet250CustomPayload();
		packet.data = baos.toByteArray();
		packet.length = baos.size();
	}
	
	public void sendOverChannel(String channel) {
		if (packet == null)
			throw new IllegalStateException("Data must be set before sending!");
		packet.channel = channel;
		PacketDispatcher.sendPacketToServer(packet);
	}
	
	public static void createAndSendWithData(String channel, int... data) {
		IntPacket send = new IntPacket();
		send.setData(data);
		send.sendOverChannel(channel);
	}
}
