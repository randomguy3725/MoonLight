package wtf.moonlight.utils.render;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class TextureLoader {
    public static ResourceLocation loadTexture(TextureManager textureManager, String resourcePath) {
        try (InputStream inputStream = TextureLoader.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + resourcePath);
            }
            BufferedImage image = ImageIO.read(inputStream);
            DynamicTexture dynamicTexture = new DynamicTexture(image);
            ResourceLocation resourceLocation = textureManager.getDynamicTextureLocation("custom_texture", dynamicTexture);

            // 设置纹理过滤
            textureManager.bindTexture(resourceLocation);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            return resourceLocation;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load texture: " + resourcePath, e);
        }
    }
}
