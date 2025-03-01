/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockGlass;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.AxisAlignedBB;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.BlockAABBEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.packet.PingSpoofComponent;
import wtf.moonlight.utils.player.PlayerUtils;

@ModuleInfo(name = "Phase", category = ModuleCategory.Movement)
public class Phase extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"Vanilla","Watchdog Auto","Watchdog","Intave"}, "Watchdog Auto", this);
    public boolean phase;
    private final TimerUtils timerUtils = new TimerUtils();
    private boolean phasing;
    private boolean canClip = false;

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());
        if (mode.get().equals("Watchdog Auto")) {
            if (phase && !timerUtils.hasTimeElapsed(4000)) PingSpoofComponent.blink();
        }
        if (mode.get().equals("Vanilla")) {
            this.phasing = false;

            final double rotation = Math.toRadians(mc.thePlayer.rotationYaw);

            final double x = Math.sin(rotation);
            final double z = Math.cos(rotation);

            if (mc.thePlayer.isCollidedHorizontally) {
                mc.thePlayer.setPosition(mc.thePlayer.posX - x * 0.005, mc.thePlayer.posY, mc.thePlayer.posZ + z * 0.005);
                this.phasing = true;
            } else if (PlayerUtils.insideBlock()) {
                sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX - x * 1.5, mc.thePlayer.posY, mc.thePlayer.posZ + z * 1.5, false));

                mc.thePlayer.motionX *= 0.3D;
                mc.thePlayer.motionZ *= 0.3D;

                this.phasing = true;
            }
        }
            if (mode.is("Intave")) {
                mc.thePlayer.capabilities.allowEdit = true;

                if (canClip) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0052, mc.thePlayer.posZ);
            }
            if (mc.thePlayer.isSneaking()) {
                final double wdist = 0.00001D;
                final double sdist = -0.00001D;

                final double rotation = Math.toRadians(mc.thePlayer.rotationYaw);

                if (mc.gameSettings.keyBindForward.isKeyDown()) {

                    final double x = Math.sin(rotation) * wdist;
                    final double z = Math.cos(rotation) * wdist;

                    mc.thePlayer.setPosition(mc.thePlayer.posX - x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                }
                if (mc.gameSettings.keyBindBack.isKeyDown()) {

                    final double x = Math.sin(rotation) * sdist;
                    final double z = Math.cos(rotation) * sdist;

                    mc.thePlayer.setPosition(mc.thePlayer.posX - x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                }
                if (mc.gameSettings.keyBindLeft.isKeyDown()) {

                    final double x = Math.sin(rotation) * wdist;

                    mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ);
                }
                if (mc.gameSettings.keyBindLeft.isKeyDown()) {

                    final double x = Math.sin(rotation) * sdist;

                    mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ);
                }
            }
        }
    }

    @EventTarget
    public void onRender3D (Render3DEvent e){
        if (mode.is("Intave")) {
            canClip = mc.playerController.curBlockDamageMP > 0.75;
        }
    }

    @EventTarget
    public void onBlockAABB(BlockAABBEvent event) {
        if (mode.get().equals("Watchdog Auto")) {
            if (phase && PingSpoofComponent.enabled && event.getBlock() instanceof BlockGlass)
                event.setCancelled(true);
        }
        if (mode.get().equals("Vanilla")) {
            if (event.getBlock() instanceof BlockAir && phasing) {
                final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

                if (y < mc.thePlayer.posY) {
                    event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
                }
            }
        };
    }

    @EventTarget
    public void onPacket(PacketEvent event) {

        Packet<?> packet = event.getPacket();
        if (mode.is("Watchdog Auto")) {
            if (event.getState() == PacketEvent.State.INCOMING) {
                if (packet instanceof S02PacketChat s02PacketChat) {
                    String chat = s02PacketChat.getChatComponent().getUnformattedText();

                    switch (chat) {
                        case "Cages opened! FIGHT!":
                        case "§r§r§r                               §r§f§lSkyWars Duel§r":
                        case "§r§eCages opened! §r§cFIGHT!§r":
                            phase = false;
                            break;

                        case "The game starts in 3 seconds!":
                        case "§r§e§r§eThe game starts in §r§a§r§c3§r§e seconds!§r§e§r":
                        case "§r§eCages open in: §r§c3 §r§eseconds!§r":
                            phase = true;
                            timerUtils.reset();
                            break;
                    }
                }
            }
        }
    }
}
