package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;

import java.awt.*;

@ModuleInfo(name = "ItemESP", category = ModuleCategory.Visual)
public class ItemESP extends Module {

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        for (final Entity entity : mc.theWorld.getLoadedEntityList()) {
            final RenderManager renderManager = mc.getRenderManager();
            final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - renderManager.renderPosX;
            final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - renderManager.renderPosY;
            final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - renderManager.renderPosZ;
            if (entity instanceof EntityItem) {
                String enhancement = "";

                if (EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, ((EntityItem) entity).getEntityItem()) != 0) {
                    enhancement = EnumChatFormatting.AQUA + " Protection:" + EnumChatFormatting.RED + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, ((EntityItem) entity).getEntityItem());
                }

                if (EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, ((EntityItem) entity).getEntityItem()) != 0) {
                    enhancement = EnumChatFormatting.AQUA + " Sharpness:" + EnumChatFormatting.RED + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, ((EntityItem) entity).getEntityItem());
                }

                if (((EntityItem) entity).getEntityItem().getItem() == Items.golden_apple) {
                    if (((EntityItem) entity).getEntityItem().getItem().hasEffect(((EntityItem) entity).getEntityItem())) {
                        enhancement = EnumChatFormatting.RED + " Enchanted";
                    }
                }

                final String var3 = (((EntityItem) entity).getEntityItem().stackSize > 1) ? (EnumChatFormatting.RESET + " x" + ((EntityItem) entity).getEntityItem().stackSize) : "";
                if (!this.checkItem(((EntityItem) entity).getEntityItem().getItem())) {
                    continue;
                }

                GL11.glEnable(32823);
                GL11.glPolygonOffset(1.0f, -1100000.0f);
                renderLivingLabel(entity, ((EntityItem) entity).getEntityItem().getDisplayName() + var3 + enhancement, posX, posY, posZ, 160);
                GL11.glDisable(32823);
                GL11.glPolygonOffset(1.0f, 1100000.0f);
            }
        }
    }

    public boolean checkItem(Item item) {
        return item instanceof ItemAppleGold ||
                item instanceof ItemSword ||
                item instanceof ItemBow ||
                item instanceof ItemBucketMilk ||
                item instanceof ItemPotion ||
                item == Items.diamond ||
                item == Items.gold_ingot ||
                item == Items.gold_nugget ||
                item == Items.iron_ingot ||
                item instanceof ItemEnchantedBook ||
                item == Items.apple ||
                item == Items.skull ||
                item == Items.diamond_sword ||
                item == Items.diamond_boots ||
                item == Items.diamond_helmet ||
                item == Items.diamond_leggings ||
                item == Items.iron_leggings;
    }

    protected void renderLivingLabel(final Entity entityIn, final String strin, final double x, final double y, final double z, final int maxDistance) {
        final double d0 = entityIn.getDistanceSqToEntity(mc.thePlayer);
        if (d0 <= maxDistance * maxDistance) {
            final FontRenderer fontrenderer = mc.fontRendererObj;
            float var12 = mc.thePlayer.getDistanceToEntity(entityIn) / 6.0f;
            if (var12 < 1.1f) {
                var12 = 1.1f;
            }
            float var13 = (float) (var12 * 1.1);
            var13 /= 100.0f;
            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.translate((float) x + 0.0f, (float) y + entityIn.height + 0.5f, (float) z);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            if (mc.gameSettings.thirdPersonView == 2) {
                GL11.glRotatef(mc.getRenderManager().playerViewX, -1.0f, 0.0f, 0.0f);
            } else {
                GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
            }
            GlStateManager.scale(-var13, -var13, var13);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            final Tessellator tessellator = Tessellator.getInstance();
            tessellator.getWorldRenderer();
            GlStateManager.disableTexture2D();
            GlStateManager.enableTexture2D();
            new ScaledResolution(mc);
            mc.fontRendererObj.drawOutlinedString(strin, (float) -fontrenderer.getStringWidth(strin) / 2, 0,0.5f, -1, Color.BLACK.getRGB());
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GL11.glScaled(0.6000000238418579, 0.6000000238418579, 0.6000000238418579);
            GL11.glScaled(1.0, 1.0, 1.0);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            RenderHelper.disableStandardItemLighting();
            GL11.glScaled(1.5, 1.5, 1.5);
            GlStateManager.popMatrix();
            GL11.glPolygonOffset(1.0f, 1000000.0f);
            GL11.glDisable(32823);
        }
    }
}
