package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.*;

public interface INetHandlerPlayServer extends INetHandler
{
    void handleAnimation(C0APacketAnimation packetIn);

    void processChatMessage(C01PacketChatMessage packetIn);

    void processTabComplete(C14PacketTabComplete packetIn);

    void processClientStatus(C16PacketClientStatus packetIn);

    void processClientSettings(C15PacketClientSettings packetIn);

    void processConfirmTransaction(C0FPacketConfirmTransaction packetIn);

    void processEnchantItem(C11PacketEnchantItem packetIn);

    void processClickWindow(C0EPacketClickWindow packetIn);

    void processCloseWindow(C0DPacketCloseWindow packetIn);

    void processVanilla250Packet(C17PacketCustomPayload packetIn);

    void processUseEntity(C02PacketUseEntity packetIn);

    void processKeepAlive(C00PacketKeepAlive packetIn);

    void processPlayer(C03PacketPlayer packetIn);

    void processPlayerAbilities(C13PacketPlayerAbilities packetIn);

    void processPlayerDigging(C07PacketPlayerDigging packetIn);

    void processEntityAction(C0BPacketEntityAction packetIn);

    void processInput(C0CPacketInput packetIn);

    void processHeldItemChange(C09PacketHeldItemChange packetIn);

    void processCreativeInventoryAction(C10PacketCreativeInventoryAction packetIn);

    void processUpdateSign(C12PacketUpdateSign packetIn);

    void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packetIn);

    void handleSpectate(C18PacketSpectate packetIn);

    void handleResourcePackStatus(C19PacketResourcePackStatus packetIn);
}
