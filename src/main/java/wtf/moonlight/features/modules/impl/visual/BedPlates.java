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
package wtf.moonlight.features.modules.impl.visual;

import kotlin.collections.ArraysKt;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.render.GLUtils;

import java.util.HashSet;
import java.util.Set;

@ModuleInfo(name = "BedPlates",category = ModuleCategory.Visual)
public class BedPlates extends Module {

    private static final int MAX_SIZE = 8;

    public final SliderValue distance = new SliderValue("Distance",50,10,75,1,this);
    public final SliderValue updateRate = new SliderValue("Update Rate",1000, 250, 5000, 250,this);
    public final SliderValue layers = new SliderValue("Layers",5,1,10,1,this);
    private final BlockPos[] beds = new BlockPos[MAX_SIZE];
    private final Set<Block>[] bedBlocks = new Set[MAX_SIZE];
    private final Set<BlockPos> retardedList = new HashSet<>();
    private final TimerUtils timer = new TimerUtils();

    private void clearBeds() {
        for (int i = 0; i < MAX_SIZE; i++) {
            beds[i] = null;
            bedBlocks[i] = new HashSet<>();
        }
    }

    @Override
    public void onEnable() {
        clearBeds();
        retardedList.clear();
    }

    private final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (!timer.hasTimeElapsed(updateRate.get()))
            return;

        clearBeds();

        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        int radius = (int) distance.get();
        int ind = 0;

        var center = new BlockPos(mc.thePlayer);
        for (int y = radius; y >= -radius; --y) {
            for (int x = -radius; x <= radius; ++x) {
                for (int z = -radius; z <= radius; ++z) {
                    mutable.set(center);
                    BlockPos pos = mutable.move(x, y, z);
                    Block bl = mc.theWorld.getBlockState(pos).getBlock();
                    if (retardedList.contains(pos))
                        continue;

                    if (ind < 8) {
                        if (bl.equals(Blocks.bed)) {
                            boolean found = find(pos.getX(), pos.getY(), pos.getZ(), ind);
                            if (found) {
                                retardedList.add(pos.north());
                                retardedList.add(pos.south());
                                retardedList.add(pos.east());
                                retardedList.add(pos.west());
                                ind++;
                            }
                        }
                    }
                }
            }
        }

        timer.reset();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        int index = 0;
        for (BlockPos blockPos : this.beds) {
            if (blockPos == null || beds[index] == null)
                continue;

            mc.entityRenderer.setupCameraTransform(event.partialTicks(), 0);

            final double x = blockPos.getX() - mc.getRenderManager().viewerPosX;
            final double y = blockPos.getY() - mc.getRenderManager().viewerPosY;
            final double z = blockPos.getZ() - mc.getRenderManager().viewerPosZ;

            final AxisAlignedBB bb = new AxisAlignedBB(x, y - 1, z, x, y + 1, z);

            final double[][] vectors = {{bb.minX, bb.minY, bb.minZ},
                    {bb.minX, bb.maxY, bb.minZ},
                    {bb.minX, bb.maxY, bb.maxZ},
                    {bb.minX, bb.minY, bb.maxZ},
                    {bb.maxX, bb.minY, bb.minZ},
                    {bb.maxX, bb.maxY, bb.minZ},
                    {bb.maxX, bb.maxY, bb.maxZ},
                    {bb.maxX, bb.minY, bb.maxZ}};

            float[] projection;
            final float[] position = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F};

            for (final double[] vec : vectors) {
                projection = GLUtils.project2D((float) vec[0], (float) vec[1], (float) vec[2], event.scaledResolution().getScaleFactor());
                if (projection != null && projection[2] >= 0.0F && projection[2] < 1.0F) {
                    final float pX = projection[0];
                    final float pY = projection[1];
                    position[0] = Math.min(position[0], pX);
                    position[1] = Math.min(position[1], pY);
                    position[2] = Math.max(position[2], pX);
                    position[3] = Math.max(position[3], pY);
                }
            }

            mc.entityRenderer.setupOverlayRendering();
            float width = bedBlocks[index].size() * 20 + 4;
            final float posX = position[0] - width / 2f;
            final float posY = position[1];
            Fonts.interMedium.get(15).drawCenteredString(((int) mc.thePlayer.getDistance(blockPos)) + "m", posX + width / 2, posY + 4, -1);
            float curX = posX + 4;
            for (Block block : bedBlocks[index]) {
                ItemStack stack = new ItemStack(block);
                GlStateManager.pushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.disableAlpha();
                GlStateManager.clear(256);
                mc.getRenderItem().zLevel = -150.0F;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                mc.getRenderItem().renderItemIntoGUI(stack, (int) curX, (int) (posY + Fonts.interMedium.get(15).getHeight() + 8));
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, (int) curX, (int) (posY + Fonts.interMedium.get(15).getHeight() + 8), null);
                mc.getRenderItem().zLevel = 0.0F;
                GlStateManager.enableAlpha();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();
                curX += 20;
            }
            mc.entityRenderer.setupOverlayRendering();
            index++;
        }
    }

    private static final Set<Block> targetBlocks = Set.of(
            Blocks.wool, Blocks.stained_hardened_clay, Blocks.stained_glass, Blocks.planks, Blocks.log, Blocks.log2, Blocks.end_stone, Blocks.obsidian,
            Blocks.bedrock
    ); // BW normal def blocks list ^ ^

    private boolean find(double x, double y, double z, int index) {
        BlockPos bedPos = new BlockPos(x, y, z);
        Block bed = mc.theWorld.getBlockState(bedPos).getBlock();
        bedBlocks[index].clear();
        beds[index] = null;

        if (ArraysKt.contains(beds, bedPos)) {
            return false;
        }

        final var pos = new BlockPos.MutableBlockPos();
        final int layer = (int) layers.get();
        for (int yOffset = 0; yOffset <= layer; ++yOffset) {
            for (int xOffset = -layer; xOffset <= layer; ++xOffset) {
                for (int zOffset = -layer; zOffset <= layer; ++zOffset) {
                    pos.set(bedPos);
                    pos.move(xOffset, yOffset, zOffset);
                    Block blockAtOffset = mc.theWorld.getBlockState(pos).getBlock();
                    if (targetBlocks.contains(blockAtOffset)) {
                        bedBlocks[index].add(blockAtOffset);
                    }
                }
            }
        }

        if (bed.equals(Blocks.bed)) {
            beds[index] = bedPos;
            return true;
        }

        return false;
    }
}
