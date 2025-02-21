package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
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
        final RenderManager renderManager = mc.getRenderManager();
        for (final Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (!(entity instanceof EntityItem entityItem))
                continue;

            String enhancement = "";

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, entityItem.getEntityItem()) != 0) {
                enhancement = EnumChatFormatting.AQUA + " Protection:" + EnumChatFormatting.RED + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, entityItem.getEntityItem());
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, entityItem.getEntityItem()) != 0) {
                enhancement = EnumChatFormatting.AQUA + " Sharpness:" + EnumChatFormatting.RED + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, entityItem.getEntityItem());
            }

            if (entityItem.getEntityItem().getItem() == Items.golden_apple) {
                if (entityItem.getEntityItem().getItem().hasEffect(entityItem.getEntityItem())) {
                    enhancement = EnumChatFormatting.RED + " Enchanted";
                }
            }

            final String var3 = (entityItem.getEntityItem().stackSize > 1) ? (EnumChatFormatting.RESET + " x" + entityItem.getEntityItem().stackSize) : "";
            if (!this.checkItem(entityItem.getEntityItem().getItem())) {
                continue;
            }

            double interpolatedX = entityItem.lastTickPosX + (entityItem.posX - entityItem.lastTickPosX) * event.partialTicks();
            double interpolatedY = entityItem.lastTickPosY + (entityItem.posY - entityItem.lastTickPosY) * event.partialTicks();
            double interpolatedZ = entityItem.lastTickPosZ + (entityItem.posZ - entityItem.lastTickPosZ) * event.partialTicks();
            double diffX = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.partialTicks() - interpolatedX;
            double diffY = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.partialTicks() - interpolatedY;
            double diffZ = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.partialTicks() - interpolatedZ;

            double dist = MathHelper.sqrt_double(diffX * diffX + diffY * diffY + diffZ * diffZ);

            GlStateManager.pushMatrix();
            drawText(entityItem.getEntityItem().getDisplayName() + var3 + enhancement, -1, interpolatedX, interpolatedY, interpolatedZ, dist);
            GlStateManager.popMatrix();
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

    public void drawText(String value,int textColor, double posY, double posX, double posZ, double dist) {
        posY -= mc.getRenderManager().viewerPosX;
        posX -= mc.getRenderManager().viewerPosY;
        posZ -= mc.getRenderManager().viewerPosZ;
        GL11.glPushMatrix();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posY, (float) posX + 0.3, (float) posZ);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((mc.gameSettings.thirdPersonView == 2 ? -1 : 1) * mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
        float scale = Math.min(Math.max(0.02266667f, (float) (0.001500000013038516 * dist)), 0.07f);
        GlStateManager.scale(-scale, -scale, -scale);
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        mc.fontRendererObj.drawOutlinedString(value, -((float) mc.fontRendererObj.getStringWidth(value) / 2) + scale * 3.5f, -(123.805f * scale - 2.47494f),0.5f, textColor,Color.BLACK.getRGB());
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
}
