package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.*;

public interface INetHandlerPlayClient extends INetHandler
{
    void handleSpawnObject(S0EPacketSpawnObject packetIn);

    void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn);

    void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn);

    void handleSpawnMob(S0FPacketSpawnMob packetIn);

    void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn);

    void handleSpawnPainting(S10PacketSpawnPainting packetIn);

    void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn);

    void handleAnimation(S0BPacketAnimation packetIn);

    void handleStatistics(S37PacketStatistics packetIn);

    void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn);

    void handleSignEditorOpen(S36PacketSignEditorOpen packetIn);

    void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn);

    void handleBlockAction(S24PacketBlockAction packetIn);

    void handleBlockChange(S23PacketBlockChange packetIn);

    void handleChat(S02PacketChat packetIn);

    void handleTabComplete(S3APacketTabComplete packetIn);

    void handleMultiBlockChange(S22PacketMultiBlockChange packetIn);

    void handleMaps(S34PacketMaps packetIn);

    void handleConfirmTransaction(S32PacketConfirmTransaction packetIn);

    void handleCloseWindow(S2EPacketCloseWindow packetIn);

    void handleWindowItems(S30PacketWindowItems packetIn);

    void handleOpenWindow(S2DPacketOpenWindow packetIn);

    void handleWindowProperty(S31PacketWindowProperty packetIn);

    void handleSetSlot(S2FPacketSetSlot packetIn);

    void handleCustomPayload(S3FPacketCustomPayload packetIn);

    void handleDisconnect(S40PacketDisconnect packetIn);

    void handleUseBed(S0APacketUseBed packetIn);

    void handleEntityStatus(S19PacketEntityStatus packetIn);

    void handleEntityAttach(S1BPacketEntityAttach packetIn);

    void handleExplosion(S27PacketExplosion packetIn);

    void handleChangeGameState(S2BPacketChangeGameState packetIn);

    void handleKeepAlive(S00PacketKeepAlive packetIn);

    void handleChunkData(S21PacketChunkData packetIn);

    void handleMapChunkBulk(S26PacketMapChunkBulk packetIn);

    void handleEffect(S28PacketEffect packetIn);

    void handleJoinGame(S01PacketJoinGame packetIn);

    void handleEntityMovement(S14PacketEntity packetIn);

    void handlePlayerPosLook(S08PacketPlayerPosLook packetIn);

    void handleParticles(S2APacketParticles packetIn);

    void handlePlayerAbilities(S39PacketPlayerAbilities packetIn);

    void handlePlayerListItem(S38PacketPlayerListItem packetIn);

    void handleDestroyEntities(S13PacketDestroyEntities packetIn);

    void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn);

    void handleRespawn(S07PacketRespawn packetIn);

    void handleEntityHeadLook(S19PacketEntityHeadLook packetIn);

    void handleHeldItemChange(S09PacketHeldItemChange packetIn);

    void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn);

    void handleEntityMetadata(S1CPacketEntityMetadata packetIn);

    void handleEntityVelocity(S12PacketEntityVelocity packetIn);

    void handleEntityEquipment(S04PacketEntityEquipment packetIn);

    void handleSetExperience(S1FPacketSetExperience packetIn);

    void handleUpdateHealth(S06PacketUpdateHealth packetIn);

    void handleTeams(S3EPacketTeams packetIn);

    void handleUpdateScore(S3CPacketUpdateScore packetIn);

    void handleSpawnPosition(S05PacketSpawnPosition packetIn);

    void handleTimeUpdate(S03PacketTimeUpdate packetIn);

    void handleUpdateSign(S33PacketUpdateSign packetIn);

    void handleSoundEffect(S29PacketSoundEffect packetIn);

    void handleCollectItem(S0DPacketCollectItem packetIn);

    void handleEntityTeleport(S18PacketEntityTeleport packetIn);

    void handleEntityProperties(S20PacketEntityProperties packetIn);

    void handleEntityEffect(S1DPacketEntityEffect packetIn);

    void handleCombatEvent(S42PacketCombatEvent packetIn);

    void handleServerDifficulty(S41PacketServerDifficulty packetIn);

    void handleCamera(S43PacketCamera packetIn);

    void handleWorldBorder(S44PacketWorldBorder packetIn);

    void handleTitle(S45PacketTitle packetIn);

    void handleSetCompressionLevel(S46PacketSetCompressionLevel packetIn);

    void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn);

    void handleResourcePack(S48PacketResourcePackSend packetIn);

    void handleEntityNBT(S49PacketUpdateEntityNBT packetIn);
}
