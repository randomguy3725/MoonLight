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
package wtf.moonlight.gui.font;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import wtf.moonlight.Moonlight;

import java.awt.*;
import java.io.InputStream;

public enum Fonts {
    interBold("inter/Inter_Bold"),
    interMedium("inter/Inter_Medium"),
    interRegular("inter/Inter_Regular"),
    interSemiBold("inter/Inter_SemiBold"),
    psRegular("product-sans/Regular"),
    psBold("product-sans/Bold"),
    nursultan("others/Nursultan"),
    Tahoma("others/Exhi"),
    skeet("others/skeet"),
    noti("others/noti"),
    noti2("others/noti2"),
    session("others/session"),
    session2("others/session2"),
    neverlose("others/nlicon"),
    sfui("others/sfui");

    private final String file;
    private final Float2ObjectMap<FontRenderer> fontMap = new Float2ObjectArrayMap<>();

    Fonts(String file) {
        this.file = file;
    }

    public FontRenderer get(float size) {
        return this.fontMap.computeIfAbsent(size, font -> {
            try {
                return create(this.file, size, true);
            } catch (Exception var5) {
                throw new RuntimeException("Unable to load font: " + this, var5);
            }
        });
    }

    public FontRenderer get(float size, boolean antiAlias) {
        return this.fontMap.computeIfAbsent(size, font -> {
            try {
                return create(this.file, size, antiAlias);
            } catch (Exception var5) {
                throw new RuntimeException("Unable to load font: " + this, var5);
            }
        });
    }

    public FontRenderer create(String file, float size, boolean antiAlias) {
        try (
                var in = Preconditions.checkNotNull(
                        Moonlight.class.getResourceAsStream("/assets/minecraft/" + Moonlight.INSTANCE.getClientName().toLowerCase() + "/font/" + file + ".ttf"), "Font resource is null"
                )
        ) {
            var font = Font.createFont(0, in).deriveFont(Font.PLAIN, size);

            if (font != null) {
                return new FontRenderer(font, antiAlias);
            } else {
                throw new RuntimeException("Failed to create font");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create font", ex);
        }
    }
}
