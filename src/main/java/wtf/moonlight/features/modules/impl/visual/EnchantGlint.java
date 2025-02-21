package wtf.moonlight.features.modules.impl.visual;

import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ColorValue;

import java.awt.*;

@ModuleInfo(name = "EnchantGlint", category = ModuleCategory.Visual)
public class EnchantGlint extends Module {

    public final BoolValue syncColor = new BoolValue("Sync Color", false, this);
    public final ColorValue color = new ColorValue("Color",new Color(0,255,255),this ,() -> !syncColor.get());
}
