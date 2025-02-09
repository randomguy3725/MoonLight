package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.utils.player.MovementUtils;
import wtf.moonlight.utils.player.PlayerUtils;

import java.util.Map;

@ModuleInfo(name = "NoWeb",category = ModuleCategory.Movement)
public class NoWeb extends Module {

    private final ModeValue mode = new ModeValue("Mode", new String[]{"Vanilla", "GrimAC", "Intave"}, "Vanilla",this);
    public final BoolValue noDown = new BoolValue("No Down",true,this,() -> mode.is("Intave"));
    public final BoolValue upAndDown = new BoolValue("Up And Down",true,this,() -> mode.is("Intave") && noDown.get());

    @EventTarget
    public void onMotion(MotionEvent event) {
        setTag(mode.get());
        if (!mc.thePlayer.isInWeb) {
            return;
        }

        switch (mode.get()) {
            case "Vanilla":
                mc.thePlayer.isInWeb = false;
                break;
            case "GrimAC":
                Map<BlockPos, Block> searchBlock = PlayerUtils.searchBlocks(2);
                for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
                    if (mc.theWorld.getBlockState(block.getKey()).getBlock() instanceof BlockWeb) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
                    }
                }
                mc.thePlayer.isInWeb = false;
                break;
            case "Intave":
                searchBlock = PlayerUtils.searchBlocks(2);
                for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
                    if (mc.theWorld.getBlockState(block.getKey()).getBlock() instanceof BlockWeb) {
                        if (noDown.get()) {
                            if (upAndDown.get())
                                if (mc.gameSettings.keyBindSneak.isKeyDown())
                                    mc.thePlayer.motionY = -0.2;
                                else if (mc.gameSettings.keyBindJump.isKeyDown())
                                    mc.thePlayer.motionY = mc.thePlayer.ticksExisted % 2 == 0 ? 0.2 : -0.01;
                                else
                                    mc.thePlayer.motionY = -0.01;
                            else
                                mc.thePlayer.motionY = -0.01;
                        }

                    }
                }
                break;
        }
    }
}
