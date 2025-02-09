package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C0APacketAnimation implements Packet<INetHandlerPlayServer>
{
    public void readPacketData(PacketBuffer buf) throws IOException
    {
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.handleAnimation(this);
    }
}
