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

import net.minecraft.client.gui.GuiScreen;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ColorValue;
import wtf.moonlight.features.values.impl.ModeValue;

import java.awt.*;

@ModuleInfo(name = "ClickGUI", category = ModuleCategory.Visual, key = Keyboard.KEY_RSHIFT)
public class ClickGUI extends Module {
    public final ModeValue mode = new ModeValue("Mode", new String[]{"NeverLose", "DropDown","Exhi","Astolfo"}, "NeverLose", this);

    public final ColorValue color = new ColorValue("Color", new Color(128, 128, 255), this);
    public final BoolValue rainbow = new BoolValue("Rainbow",true,this,() -> mode.is("Exhi"));

    @Override
    public void onEnable() {
        GuiScreen guiScreen = switch (mode.get()) {
            case "NeverLose" -> INSTANCE.getNeverLose();
            case "DropDown" -> INSTANCE.getDropdownGUI();
            case "Exhi" -> INSTANCE.getSkeetGUI();
            case "Astolfo" -> INSTANCE.getAstolfoGui();
            default -> null;
        };
        mc.displayGuiScreen(guiScreen);
        if(mode.is("Exhi")){
            INSTANCE.getSkeetGUI().alpha = 0.0;
            INSTANCE.getSkeetGUI().targetAlpha = 255.0;
            INSTANCE.getSkeetGUI().open = true;
            INSTANCE.getSkeetGUI().closed = false;
        }
        toggle();
        super.onEnable();
    }
}
