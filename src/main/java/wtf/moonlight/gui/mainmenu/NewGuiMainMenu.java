package wtf.moonlight.gui.mainmenu;


import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.Moonlight;
import wtf.moonlight.gui.button.MenuButton;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;
import wtf.moonlight.utils.render.TextureLoader;
import wtf.moonlight.utils.render.shader.impl.Blur;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//by Brave233awa  qq:3089576561 使用请标明出处 尊重原作者产权
public class NewGuiMainMenu extends GuiScreen {
    private final List<NewGuiMainMenu.ChangeLog> logs = new ArrayList<>();
    private final List<Drop> drops = new ArrayList<>();
    private float hoverAlpha = 0f; // 初始透明度为0
    private float scale = 1f; // 初始缩放为1
    private static final float HOVER_SPEED = 0.01f; // 透明度变化速度
    private static final float SCALE_SPEED = 0.005f; // 缩放变化速度
    //图片资源
    private final ResourceLocation backgroundResource , s , m , o , a ;
    public NewGuiMainMenu() {
        //加载图片
        backgroundResource = TextureLoader.loadTexture(Minecraft.getMinecraft().getTextureManager(), "/assets/minecraft/moonlight/img/mainmenu/bg.png");
        s = TextureLoader.loadTexture(Minecraft.getMinecraft().getTextureManager(),"/assets/minecraft/moonlight/img/mainmenu/s.png");
        m = TextureLoader.loadTexture(Minecraft.getMinecraft().getTextureManager(),"/assets/minecraft/moonlight/img/mainmenu/m.png");
        o = TextureLoader.loadTexture(Minecraft.getMinecraft().getTextureManager(),"/assets/minecraft/moonlight/img/mainmenu/o.png");
        a = TextureLoader.loadTexture(Minecraft.getMinecraft().getTextureManager(),"/assets/minecraft/moonlight/img/mainmenu/a.png");
        //log添加
        logs.add(new ChangeLog("1.8x autoblock for Hypixel", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("Hypixel tower/towermove", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("New Hypixel rotations for scaffold", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("Smart option for KillAura", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("Glide speed bypassing Hypixel", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("New notification mode", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("TargetHUD showing up from far away", ChangeLogType.FIX));
        logs.add(new ChangeLog("Issues with towermove", ChangeLogType.FIX));
        logs.add(new ChangeLog("Buttons on MainMenu not working", ChangeLogType.FIX));
        logs.add(new ChangeLog("NewGuiMainMenu for MoonLight", ChangeLogType.ADDITION));
        logs.add(new ChangeLog("NewGuiMainMenu by Brave233awa", ChangeLogType.ADDITION));
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //获取屏幕宽高
        ScaledResolution sr = new ScaledResolution(mc);
        width = sr.getScaledWidth();
        height = sr.getScaledHeight();
        //壁纸
        RenderUtils.drawImage1(backgroundResource, -20, 0, width+20, height);
        //大背景
        Blur.startBlur();
        RoundedUtils.drawRound((float) width /2-150, (float) height /2-100,300,200,10,new Color(0,0,0,100));
        Blur.endBlur(10,3);
        //ClientName
        Fonts.Exhi.get(55).drawCenteredStringWithOutline("Moonlight", (float) width /2, (float) height /2-95, Color.WHITE.getRGB(),
                new Color(224 , 238 , 224).getRGB());
        // 绘制竖线
        float startX = (float) width / 2 - 50;
        float startY  = (float) height / 2 - 60;
        float endX = (float) width / 2 -50;
        float endY = (float) height / 2 + 100;
        int lineColor = Color.WHITE.getRGB();
        RoundedUtils.drawLine( startX, startY, endX, endY, 1f,lineColor);
        // 绘制横线
        float startX2 = (float) width / 2 - 150;
        float startY2 = (float) height / 2 - 60;
        float endX2 = (float) width / 2 + 150;
        float endY2 = (float) height / 2 - 60;
        RoundedUtils.drawLine( startX2, startY2, endX2, endY2, 1f,lineColor);
        //鼠标悬浮效果
        if (MouseUtils.isHovered2((float) width /2-145, (float) height /2-50,  80, 25, mouseX, mouseY)){
            //singleplayer
            hoverAlpha = Math.min(hoverAlpha + HOVER_SPEED, 1f); // 逐渐增加透明度
            scale = Math.min(scale + SCALE_SPEED, 1.1f); // 逐渐增加缩放
            RoundedUtils.drawRoundOutline((float) width /2-143 , (float) height /2-50 ,  80 * scale, 25 * scale, 8f,0.5f,
                    new Color(0, 0, 0, (int) (hoverAlpha * 90)), new Color(255,255,255, (int) (hoverAlpha * 255)));
        } else if (MouseUtils.isHovered2((float) width /2-140, (float) height /2-20,  80, 25, mouseX, mouseY)){
            //Multiplayer
            hoverAlpha = Math.min(hoverAlpha + HOVER_SPEED, 1f); // 逐渐增加透明度
            scale = Math.min(scale + SCALE_SPEED, 1.1f); // 逐渐增加缩放
            RoundedUtils.drawRoundOutline((float) width /2-143, (float) height /2-20,  80 * scale, 25 * scale, 8f,0.5f,
                    new Color(0, 0, 0, (int) (hoverAlpha * 90)), new Color(255,255,255, (int) (hoverAlpha * 255)));
        } else if (MouseUtils.isHovered2((float) width /2-140, (float) height /2+10,  80, 25, mouseX, mouseY)){
            //Options
            hoverAlpha = Math.min(hoverAlpha + HOVER_SPEED, 1f); // 逐渐增加透明度
            scale = Math.min(scale + SCALE_SPEED, 1.1f); // 逐渐增加缩放
            RoundedUtils.drawRoundOutline((float) width /2-143, (float) height /2+10,  80* scale, 25* scale,8f,0.5f,
                    new Color(0, 0, 0, (int) (hoverAlpha * 90)), new Color(255,255,255, (int) (hoverAlpha * 255)));
        } else if (MouseUtils.isHovered2((float) width /2-140, (float) height /2+40,  80, 25, mouseX, mouseY)){
            //Alts
            hoverAlpha = Math.min(hoverAlpha + HOVER_SPEED, 1f); // 逐渐增加透明度
            scale = Math.min(scale + SCALE_SPEED, 1.1f); // 逐渐增加缩放
            RoundedUtils.drawRoundOutline((float) width /2-143, (float) height /2+40,  80*scale, 25*scale, 8f,0.5f,
                    new Color(0, 0, 0, (int) (hoverAlpha * 90)), new Color(255,255,255, (int) (hoverAlpha * 255)));
        }else {
            hoverAlpha = Math.max(hoverAlpha - HOVER_SPEED, 0f); // 逐渐减少透明度
            scale = Math.max(scale - SCALE_SPEED, 1f); // 逐渐减少缩放
        }
        //Gui Main Menu
        //Singleplayer
        Fonts.interMedium.get(18).drawCenteredString("Singleplayer", (float) width /2-105, (float) height /2-40, Color.WHITE.getRGB());
        RenderUtils.drawImage1(s, (float) width /2-Fonts.interMedium.get(18).getStringWidth("Singleplayer")-27, (float) height /2-45,  16, 16);
        //Multiplayer
        Fonts.interMedium.get(18).drawCenteredString("Multiplayer", (float) width /2-105, (float) height /2-10, Color.WHITE.getRGB());
        RenderUtils.drawImage1(m, (float) width /2-Fonts.interMedium.get(18).getStringWidth("Singleplayer")-27, (float) height /2-15,  16, 16);
        //Options
        Fonts.interMedium.get(18).drawCenteredString("Options", (float) width /2-105, (float) height /2+20, Color.WHITE.getRGB());
        RenderUtils.drawImage1(o, (float) width /2-Fonts.interMedium.get(18).getStringWidth("Singleplayer")-27, (float) height /2+15,  16, 16);
        //Alts
        Fonts.interMedium.get(18).drawCenteredString("Alts", (float) width /2-105, (float) height /2+50, Color.WHITE.getRGB());
        RenderUtils.drawImage1(a, (float) width /2-Fonts.interMedium.get(18).getStringWidth("Singleplayer")-27, (float) height /2+45,  16, 16);
        //version
        Fonts.interMedium.get(18).drawCenteredString("version:"+Moonlight.INSTANCE.getVersion(), (float) width /2-105, (float) height /2+85, Color.WHITE.getRGB());
        //changelog
        int i = 1;
        Fonts.interMedium.get(18).drawCenteredString("ChangeLog:", (float) width /2-16, (float) height /2-46, -1);//老哥你把这个写到循环里面干什么
        for (ChangeLog changeLog : logs) {
            if (changeLog != null) {
                if (changeLog.getLog() != null) {
                    Fonts.interSemiBold.get(15).drawString(changeLog.type.character + changeLog.getLog(), (float) width /2-40, (float) height /2-46 + i * (Fonts.interMedium.get(18).getHeight()) , changeLog.type.stringColor);
                }
                i++;
            }
        }
        // 更新和绘制雨滴
        for (Drop drop : new ArrayList<>(drops)) {
            drop.update();
            drop.draw();
            if (!drop.isOnScreen(height)) {
                drops.remove(drop);
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        //鼠标点击效果
        if (MouseUtils.isHovered2((float) width /2-140, (float) height /2-50,  80, 25, mouseX, mouseY)){
            mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (MouseUtils.isHovered2((float) width /2-140, (float) height /2-20,  80, 25, mouseX, mouseY)){
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (MouseUtils.isHovered2((float) width /2-140, (float) height /2+10,  80, 25, mouseX, mouseY)){
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }
        if (MouseUtils.isHovered2((float) width /2-140, (float) height /2+40,  80, 25, mouseX, mouseY)){
            mc.displayGuiScreen(Moonlight.INSTANCE.getAltRepositoryGUI());
        }
        // 处理鼠标点击事件
        drops.add(new Drop(mouseX, mouseY)); // 创建一个新的雨滴
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    public boolean doesGuiPauseGame () {
        return false;
    }
    public static class ChangeLog {

                @Getter
                private final String log;
                private final NewGuiMainMenu.ChangeLogType type;

                public ChangeLog(String log, NewGuiMainMenu.ChangeLogType type) {
                    this.log = log;
                    this.type = type;
                }
            }
    public enum ChangeLogType {
                ADDITION("[+]", new Color(224, 238, 224).getRGB()),
                FIX("[~]", new Color(255, 231, 186).getRGB()),
                REMOVAL("[-]", new Color(255, 181, 197).getRGB());


                @Getter
                private final String character;
                private final int stringColor;

                ChangeLogType(String character, int stringColor) {
                    this.character = character;
                    this.stringColor = stringColor;
                }
            }
    public static class Drop {
                private float x, y;
                private float speed;
                private float size;

                public Drop(float x, float y) {
                    this.x = x;
                    this.y = y;
                    this.speed = (float) (Math.random() * 5 + 2); // 随机速度
                    this.size = (float) (Math.random() * 1 + 1); // 随机大小
                }

                public void update() {
                    y += speed; // 更新位置
                }

                public void draw() {
                    drawCircle(x, y, size, new Color(181 ,181 ,181).getRGB()); // 绘制雨滴
                }

                public boolean isOnScreen(int height) {
                    return y < height; // 判断雨滴是否还在屏幕上
                }
            }
    public static void drawCircle ( float x, float y, float radius, int color){
                GlStateManager.enableBlend();
                GlStateManager.disableTexture2D();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.color(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);

                GL11.glBegin(GL11.GL_TRIANGLE_FAN);
                GL11.glVertex2f(x, y);
                for (int i = 0; i <= 360 * 2; i++) {
                    double angle = Math.toRadians(i);
                    GL11.glVertex2f((float) (x + Math.cos(angle) * radius), (float) (y + Math.sin(angle) * radius));
                }
                GL11.glEnd();

                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
            }
}
