package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.utils.player.PlayerUtils;

import java.util.Map;

@ModuleInfo(name = "NoFluid",category = ModuleCategory.Movement)
public class NoFluid extends Module {

    private final ModeValue mode = new ModeValue("Mode", new String[]{"Vanilla", "GrimAC"}, "Vanilla",this);

    public boolean shouldCancel;

    @Override
    public void onDisable(){
        shouldCancel = false;
    }

    @EventTarget
    public void onWorld(WorldEvent event){
        shouldCancel = false;
    }

    @EventTarget
    public void onMotion(MotionEvent event){
        setTag(mode.get());
        if (mc.thePlayer == null)
            return;

        if (event.isPost()) return;

        shouldCancel = false;

        Map<BlockPos, Block> searchBlock = PlayerUtils.searchBlocks(2);

        for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
            boolean checkBlock = mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.water
                    || mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.flowing_water
                    || mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.lava
                    || mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.flowing_lava;
            if (checkBlock) {
                shouldCancel = true;
                if (mode.is("GrimAC") && shouldCancel) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
                    mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
                }
            }
        }
    }
}
